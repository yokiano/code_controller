package yokiano.codecontroller.presentation.viewimpl.tornadofx.panes

import javafx.beans.property.SimpleDoubleProperty
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.Cursor
import javafx.scene.Node
import javafx.scene.control.ContextMenu
import javafx.scene.control.ScrollPane
import javafx.scene.input.MouseButton.*
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import mapTo
import org.kodein.di.generic.instance
import org.kodein.di.tornadofx.kodein
import tornadofx.*
import yokiano.codecontroller.presentation.viewimpl.tornadofx.*
import kotlin.reflect.jvm.jvmName

abstract class ResponsivePane() : View() {
    // TODO - make the panes singletons/multitons instead instantiating them like now.

    // --- DEPENDENCIES ---
    val driver: TornadoDriver by kodein().instance<TornadoDriver>()

    // --- INTERNAL PROPERTIES ---
    abstract val type: PaneType
    abstract val draggable: Node // supports the fast resize feature.
    private var hidden = false

    // --- HELPER PROPERTIES ---
    private val indexInSplitPane: Int
        get() {
            return driver.activePanes.indexOf(this)
        }

    // --- SERVICE BAR RELATED ---
    open val validServiceBarFeatures: Array<Boolean> = Array(ServiceBar.Companion.Features.values().size) { true }
    val controlScale = SimpleDoubleProperty(1.0)
    val textScale = SimpleDoubleProperty(1.0)
    val serviceBar: ServiceBar by lazy { ServiceBar(controlScale, textScale, validServiceBarFeatures) }

    var isFastResizeEnabled = true
    val contextMenu: ContextMenu by lazy { contextmenu() }
    abstract val paneRoot: Node

    // Memo - stackpane was wrapped with vbox. probably doesn't matter.
    override val root = stackpane {
        vgrow = Priority.ALWAYS

        alignment = Pos.BOTTOM_CENTER
        runLater {
            paneRoot.attachTo(this)
            paneRoot.addClass(MyStyle.responsivePane)

            serviceBar.root.attachTo(this)

            // Burger Button for serviceBar
            button {
                style {
                    graphic = javaClass.getResource("/service_bar/burger_icon.png").toURI()
                    backgroundColor += Color.TRANSPARENT
                }
                this.setOnMouseEntered {
                    serviceBar.root.show()
                    this.hide()
                }
                serviceBar.root.setOnMouseExited {
                    this.show()
                    serviceBar.root.hide()
                }

            }
        }


    }

    // Needed for resizing panes with mouse drag event.
    private var lastMouseX = 0.0 to 0.0
    private var lastMouseY = 0.0 to 0.0
    private var dividerToDrag = 0
    private var shouldShowContextMenu = true

    init {
        runLater {
            setup()
        }
    }

    fun setup() {
        setMouseEvents(root)
        setMouseEvents(draggable)
        setTextScaleHandler()
    }

    private fun showHidePanes(pane: ResponsivePane) {
        pane.run {
            if (this.hidden) {
                driver.mainView.splitpane.addCCPanes(this, atIndex = this.indexInSplitPane)
                this.hidden = false
            } else {
                hidePane(this)
            }
        }
    }

    private fun hidePane(pane: ResponsivePane) {
        driver.mainView.splitpane.items.remove(pane.root)
        pane.hidden = true
    }

    private fun setRightClick(node: Node) {
        node.setOnContextMenuRequested { event ->
            if (shouldShowContextMenu) {
                contextMenu.apply {
                    items.clear()

                    item("Hide This Pane") {
                        action {
                            hidePane(this@ResponsivePane)
                        }
                    }
                    separator()

                    menu("Show/Hide Panes") {
                        driver.activePanes.forEachIndexed() { i, it ->
                            checkmenuitem("${it.type.javaClass.simpleName} Pane") {
                                isSelected = !it.hidden
                                action {
                                    showHidePanes(it)
                                }
                            }
                            it.setup()
                        }
                    }

                }
                contextMenu.show(node, event.screenX, event.screenY)
                event.consume()
            }


        }

    }

    private fun setMouseEvents(node: Node) {
        node.apply {

            // --- For Fast Resize and Fast Move mechanisms
            setOnMousePressed {
                mousePressedEvent(it)
            }

            setOnMouseDragged {
                handleMouseDragged(it)
            }
            setOnMouseReleased {
                root.cursor = Cursor.DEFAULT
            }

            setRightClick(this)

        }
    }

    private fun Node.mousePressedEvent(event: MouseEvent) {
        event.apply {
            lastMouseX = screenX to sceneX
            lastMouseY = screenY to sceneY

            val halfWay = when (driver.screenOrientation) {
                Orientation.HORIZONTAL -> boundsInLocal.width / 2.0
                Orientation.VERTICAL -> {
                    val scroll = this@ResponsivePane.paneRoot
                    if (scroll is ScrollPane) {
                        scroll.viewportBounds.height / 2.0
                    } else {
                        boundsInLocal.height / 2.0
                    }
                }
            }

            dividerToDrag = when (driver.screenOrientation) {
                Orientation.HORIZONTAL -> {
                    if (x > halfWay) indexInSplitPane else indexInSplitPane - 1
                }
                Orientation.VERTICAL -> {
                    if (y < halfWay) indexInSplitPane - 1 else indexInSplitPane
                }
            }
            for (i in 0..dividerToDrag) {
                if (driver.activePanes[i].hidden) {
                    dividerToDrag--
                }
            }


        }

        shouldShowContextMenu = true
        if (contextMenu.isShowing) {
            contextMenu.hide()
        }
    }

    private fun handleMouseDragged(event: MouseEvent) {

        if (!driver.globalIsFastResizeEnabled || !isFastResizeEnabled) return

        when (event.button) {
            PRIMARY -> fastPaneResize(event, dividerToDrag)
            SECONDARY -> fastMove(event)
            MIDDLE -> fastResize(event)
            NONE -> return
            null -> return
        }
        root.cursor = Cursor.MOVE
//        contextMenu.hide()
        updateLastMousePoint(event)
        runAsync { updateWindowConfig() }

        shouldShowContextMenu =
            false // this is to make sure the context menu will not popup when dragging with right mouse button.
    }

    private fun updateWindowConfig() {
        with(GeneralConfig.config) {
            primaryStage.apply {
                set("x" to x)
                set("y" to y)
                set("width" to width)
                set("height" to height)
                set("orientation" to driver.screenOrientation.ordinal)
            }
            save()
        }
    }

    private fun fastResize(
        event: MouseEvent,
        resizeHeight: Boolean = true,
        resizeWidth: Boolean = true,
        fixedDividers: Boolean = false
    ) {
        val progressX = event.screenX - lastMouseX.first
        val progressY = event.screenY - lastMouseY.first

        with(primaryStage) {
            if (resizeHeight) {
                if (event.sceneY > height / 2.0) {
                    height = (height + progressY)
                } else {
                    height = (height - progressY)
                    y += progressY
                }
            }

            if (resizeWidth) {
                val isRightEdge = (event.sceneX > width / 2.0)
                if (fixedDividers) {
                    val oldWidth = driver.mainView.splitpane.width
                    with(driver.mainView.splitpane) {
//                        println("Old width = ${oldWidth}, current width = ${width}")
                        dividers.forEachIndexed { index, divider ->
                            val distance = divider.position * oldWidth
                            val newPosition = if (isRightEdge) {
                                distance / (oldWidth + progressX)
                            } else {
                                (distance - progressX) / (oldWidth - progressX)
                            }
                            setDividerPosition(index, newPosition)
                        }
                    }
                }

                if (isRightEdge) {
                    width = (width + progressX)
                } else {
                    width = (width - progressX)
                    x += progressX
                }
            }
        }
    }

    private fun fastPaneResize(event: MouseEvent, dividerIndex: Int) {

        val progress = if (driver.screenOrientation == Orientation.HORIZONTAL) {
            (event.sceneX - lastMouseX.second).mapTo(0.0, driver.mainView.splitpane.width, 0.0, 1.0)
        } else {
            (event.sceneY - lastMouseY.second).mapTo(0.0, driver.mainView.splitpane.height, 0.0, 1.0)
        }

        // Moving the divider according to the mouse movement. attempting to adjust the most left and most right edges (which do not exist) will cause panel resize.
        driver.mainView.splitpane.apply {

            val lastPosition = dividerPositions.getOrElse(dividerIndex) {
                // In case the most left/right edges are subject to change
                fastResize(event, resizeHeight = false, fixedDividers = true)
                return
            }
            setDividerPosition(dividerToDrag, lastPosition + progress)


        }
    }

    private fun fastMove(
        event: MouseEvent
    ) {
        val progressX = event.screenX - lastMouseX.first
        val progressY = event.screenY - lastMouseY.first

        primaryStage.x += progressX
        primaryStage.y += progressY
    }

    private fun updateLastMousePoint(event: MouseEvent) {
        lastMouseX = event.screenX to event.sceneX
        lastMouseY = event.screenY to event.sceneY
    }

    // This function fixes an issue where the mouse will trigger it's event on the ScrollPaneSkin and it will not forward it to resize the pane.
    fun fixScrollerMouseEvents(sp: ScrollPane) {

        val scrollSkinString = "ScrollPaneSkin\$6"
        sp.apply {
            addEventFilter(MouseEvent.MOUSE_DRAGGED) {
                if (it.pickResult.intersectedNode == null) {
                    return@addEventFilter
                }
                if (it.pickResult.intersectedNode::class.jvmName.contains(scrollSkinString)) {
                    handleMouseDragged(it)
                }
            }
            addEventFilter(MouseEvent.MOUSE_PRESSED) {
                if (it.pickResult.intersectedNode::class.jvmName.contains(scrollSkinString)) {
                    mousePressedEvent(it)
                }
            }
            addEventFilter(MouseEvent.MOUSE_RELEASED) {
                if (it.pickResult.intersectedNode == null) {
                    return@addEventFilter
                }
                if (it.pickResult.intersectedNode::class.jvmName.contains(scrollSkinString)) {
                    root.cursor = Cursor.DEFAULT
                }
            }
        }
    }

    open fun setTextScaleHandler() {
        textScale.onChange {
            val labelsAndTextFields = ArrayList<Node>()
            labelsAndTextFields.addAll(root.lookupAll(".label"))
            labelsAndTextFields.addAll(root.lookupAll(".text-field"))
//            labelsAndTextFields.addAll(root.lookupAll("Text")) // <-- works for the plot numbers, but it is overwritten by plot behavior and flickers
            val newScale = it
            labelsAndTextFields.forEach {
                it.style(true) {
                    fontSize = Dimension(MyStyle.defaultTextSize * newScale, Dimension.LinearUnits.px)
                }
            }
        }
    }
}
