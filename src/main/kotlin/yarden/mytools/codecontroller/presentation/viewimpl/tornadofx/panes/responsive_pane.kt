package yarden.mytools.codecontroller.presentation.viewimpl.tornadofx.panes

import javafx.scene.Cursor
import javafx.scene.Node
import javafx.scene.control.ScrollPane
import javafx.scene.input.MouseButton.*
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Priority
import mapTo
import org.kodein.di.generic.instance
import org.kodein.di.tornadofx.kodein
import tornadofx.*
import yarden.mytools.codecontroller.presentation.viewimpl.tornadofx.PaneType
import yarden.mytools.codecontroller.presentation.viewimpl.tornadofx.TornadoDriver
import java.awt.geom.Point2D
import kotlin.reflect.jvm.jvmName

abstract class ResponsivePane() : View() {
    // TODO - make the panes singletons/multitons instead instantiating them like now.
    val driver: TornadoDriver by kodein().instance()
    abstract val type: PaneType
    abstract val draggable: Node
    val indexInSplitPane: Int
        get() {
            return driver.activePanes.indexOf(this)
        }

    var disableFastResize = false

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
    var lastMouseX = 0.0 to 0.0
    var lastMouseY = 0.0 to 0.0
    var dividerToDrag = 0

    init {
        runLater { setup() }
    }

    fun setup() {
        setMouseEvents(root)
        setMouseEvents(draggable)
    }

    fun setMouseEvents(node: Node) {
        node.apply {

            // --- For Fast Resize and Fast Move mechanisms
            setOnMousePressed {
                mousePressedEvent(it)
            }
            setOnMouseDragged {
                handleMouseDragged(it, this)
            }
            setOnMouseReleased {
                root.cursor = Cursor.DEFAULT
            }
            setOnMouseDragOver {
                println("Drag over")
            }
            setOnMouseDragEntered {
                println("drag entered")
            }
        }
    }

    private fun Node.mousePressedEvent(event: MouseEvent) {
        event.apply {
            lastMouseX = screenX to sceneX
            lastMouseY = screenY to sceneY

            dividerToDrag = when {
                x > (boundsInLocal.width / 2.0) -> {
                    indexInSplitPane
                }
                x <= (boundsInLocal.width / 2.0) -> {
                    indexInSplitPane - 1
                }
                else -> {
                    println("Error when deciding which divider to move. x=${x}, (width/2)=${boundsInLocal.width}, returning the right side divider.")
                    indexInSplitPane
                }
            }
        }
    }


    private fun handleMouseDragged(event: MouseEvent, node: Node) {

        if (driver.globalDisableFastResize || disableFastResize) return



        when (event.button) {
            PRIMARY -> node.fastPaneResize(event,dividerToDrag)
            SECONDARY -> fastMove(event)
            MIDDLE -> fastResize(event)
            NONE -> return
            null -> return
        }
        root.cursor = Cursor.MOVE
        updateLastMousePoint(event)

    }

    private fun fastResize(
        event: MouseEvent, onlyHeight: Boolean = false
    ) {

        val progressX = event.screenX - lastMouseX.first
        val progressY = event.screenY - lastMouseY.first

        primaryStage.height = (primaryStage.height + progressY)

        if(onlyHeight) return

        if (event.sceneX > primaryStage.width / 2.0) {
            primaryStage.width = (primaryStage.width + progressX)
        } else {
            primaryStage.width = (primaryStage.width - progressX)
            primaryStage.x += progressX
        }


    }

    private fun Node.fastPaneResize(event: MouseEvent, dividerIndex: Int) {

        val progressX = (event.sceneX - lastMouseX.second).mapTo(0.0, driver.mainView.splitpane.width, 0.0, 1.0)

        // Moving the divider according to the mouse movement. attempting to adjust the most left and most right edges (which do not exist) will simply return and do nothing.
        driver.mainView.splitpane.apply {
            val lastPosition = dividerPositions.getOrElse(dividerIndex) {
                fastResize(event)
                return
            }
            setDividerPosition(dividerToDrag, lastPosition + progressX)
            fastResize(event,onlyHeight = true)
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

    fun updateLastMousePoint(event: MouseEvent) {
        lastMouseX = event.screenX to event.sceneX
        lastMouseY = event.screenY to event.sceneY
    }

    // This function fixes an issue where the mouse will trigger it's event on the ScrollPaneSkin and it will not forward it to resize the pane.
    fun fixScrollerMouseEvents(sp: ScrollPane) {
        sp.apply {
            addEventFilter(MouseEvent.MOUSE_DRAGGED) {
                if (it.pickResult.intersectedNode == null) {
                    return@addEventFilter
                }
                if (it.pickResult.intersectedNode::class.jvmName.contains("ScrollPaneSkin\$6")) {
                    handleMouseDragged(it, this)
                }
            }
            addEventFilter(MouseEvent.MOUSE_PRESSED) {
                if (it.pickResult.intersectedNode::class.jvmName.contains("ScrollPaneSkin\$6")) {
                    mousePressedEvent(it)
                }
            }
            addEventFilter(MouseEvent.MOUSE_RELEASED) {
                if (it.pickResult.intersectedNode == null) {
                    return@addEventFilter
                }
                if (it.pickResult.intersectedNode::class.jvmName.contains("ScrollPaneSkin\$6")) {
                    root.cursor = Cursor.DEFAULT
                }
            }

        }

    }
}
