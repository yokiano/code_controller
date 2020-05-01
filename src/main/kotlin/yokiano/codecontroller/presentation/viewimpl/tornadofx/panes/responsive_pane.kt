package yokiano.codecontroller.presentation.viewimpl.tornadofx.panes

import javafx.geometry.Orientation
import javafx.scene.Cursor
import javafx.scene.Node
import javafx.scene.control.ContextMenu
import javafx.scene.control.ScrollPane
import javafx.scene.input.MouseButton.*
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Priority
import mapTo
import org.kodein.di.generic.instance
import org.kodein.di.tornadofx.kodein
import tornadofx.*
import yokiano.codecontroller.presentation.viewimpl.tornadofx.PaneType
import yokiano.codecontroller.presentation.viewimpl.tornadofx.TornadoDriver
import yokiano.codecontroller.presentation.viewimpl.tornadofx.WindowConfig
import yokiano.codecontroller.presentation.viewimpl.tornadofx.addCCPanes
import kotlin.reflect.jvm.jvmName

abstract class ResponsivePane() : View() {
    // TODO - make the panes singletons/multitons instead instantiating them like now.
    val driver: TornadoDriver by kodein().instance<TornadoDriver>()
    abstract val type: PaneType
    abstract val draggable: Node

    private var hidden = false

    private val indexInSplitPane: Int
        get() {
            return driver.activePanes.indexOf(this)
        }

    var isFastResizeEnabled = true
    private val contextMenu: ContextMenu by lazy { contextmenu() }
    abstract val paneRoot: Node

    override val root = vbox {

        stackpane {
            vgrow = Priority.ALWAYS
            runLater {
                paneRoot.attachTo(this)
            }
        }


    }

    // Needed for resizing panes with mouse drag event.
    private var lastMouseX = 0.0 to 0.0
    private var lastMouseY = 0.0 to 0.0
    private var dividerToDrag = 0
    private var shouldShowContextMenu = true

    init {
        runLater { setup() }
    }

    fun setup() {
        setMouseEvents(root)
        setMouseEvents(draggable)
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
//                Orientation.VERTICAL -> boundsInLocal.height / 2.0
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
        with(WindowConfig.config) {
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

    private fun fastResize(event: MouseEvent, resizeHeight: Boolean = true, resizeWidth: Boolean = true) {
        val progressX = event.screenX - lastMouseX.first
        val progressY = event.screenY - lastMouseY.first


        if (resizeHeight) {
            if (event.sceneY > primaryStage.height / 2.0) {
                primaryStage.height = (primaryStage.height + progressY)
            } else {
                primaryStage.height = (primaryStage.height - progressY)
                primaryStage.y += progressY
            }
        }

        if (resizeWidth) {
            if (event.sceneX > primaryStage.width / 2.0) {
                primaryStage.width = (primaryStage.width + progressX)
            } else {
                primaryStage.width = (primaryStage.width - progressX)
                primaryStage.x += progressX
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
                fastResize(event)
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
}
