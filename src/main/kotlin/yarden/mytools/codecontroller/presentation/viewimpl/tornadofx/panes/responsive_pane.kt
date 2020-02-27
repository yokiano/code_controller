package yarden.mytools.codecontroller.presentation.viewimpl.tornadofx.panes

import javafx.scene.Cursor
import javafx.scene.Node
import mapTo
import org.kodein.di.generic.instance
import org.kodein.di.tornadofx.kodein
import tornadofx.*
import yarden.mytools.codecontroller.presentation.viewimpl.tornadofx.PaneType
import yarden.mytools.codecontroller.presentation.viewimpl.tornadofx.TornadoDriver

abstract class ResponsivePane() : View() {
    // TODO - make the panes singletons/multitons instead instantiating them like now.
    val driver: TornadoDriver by kodein().instance()
    abstract val type: PaneType
    abstract val draggable: Node
    val indexInSplitPane: Int
        get() {
            return driver.activePanes.indexOf(this)
        }

    var mouseStartingPoint = 0.0
    var dividerToDrag = 0
    fun setMouseEvents() {
        draggable.apply {
            setOnMousePressed {
                mouseStartingPoint = it.sceneX

                dividerToDrag = when {
                    it.x > (boundsInLocal.width / 2.0) -> {
                        indexInSplitPane
                    }
                    it.x <= (boundsInLocal.width / 2.0) -> {
                        indexInSplitPane - 1
                    }
                    else -> {
                        println("Error when deciding which divider to move. x=${it.x}, (width/2)=${boundsInLocal.width}, returning the right side divider.")
                        indexInSplitPane
                    }
                }
            }
            setOnMouseDragged {
                cursor = Cursor.H_RESIZE

                val mouseDragProgress = (it.sceneX - mouseStartingPoint).mapTo(0.0,driver.mainView.splitpane.width,0.0,1.0)

                // Moving the divider 1 step in the direction of mouse movement. if the most left and right dividers are attempted to move and cause out of bounds error we simply return.
                driver.mainView.splitpane.apply {
                    val lastPosition = dividerPositions.getOrElse(dividerToDrag) {
                        return@setOnMouseDragged
                    }
                    println("x=${it.sceneX}, mouseProgress=${mouseDragProgress}, SUM = ${lastPosition + mouseDragProgress}")
                    setDividerPosition(dividerToDrag, lastPosition + mouseDragProgress)
                }

                mouseStartingPoint = it.sceneX
            }

            setOnMouseReleased {
                cursor = Cursor.DEFAULT
            }
        }
    }
}
