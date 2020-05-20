package yokiano.codecontroller.presentation.viewimpl.tornadofx.controls

import javafx.beans.Observable
import javafx.beans.property.SimpleDoubleProperty
import javafx.geometry.BoundingBox
import javafx.geometry.Bounds
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.control.ContextMenu
import javafx.scene.control.Control
import javafx.scene.layout.Pane
import tornadofx.*
import yokiano.codecontroller.presentation.viewimpl.tornadofx.MyStyle
import yokiano.codecontroller.presentation.viewimpl.tornadofx.TUnit
import javax.naming.Context

abstract class ControlView(val unit: TUnit<*>) : View() {

    abstract override val root: Parent
    abstract val control: Node
    lateinit var contextMenu: ContextMenu
    open fun setControlScaleHandler(property: SimpleDoubleProperty) {

        val animationDuration = 70.millis
        val originalBounds by lazy { control.boundsInLocal }

        property.onChange { newScale ->

            if (control is Control) {
                (control as Control).prefHeightProperty().animate(originalBounds.height * newScale, animationDuration)
                (control as Control).prefWidthProperty().animate(originalBounds.width * newScale, animationDuration)
            } else if (control is Pane) {
                (control as Pane).prefHeightProperty().animate(originalBounds.height * newScale, animationDuration)
                (control as Pane).prefWidthProperty().animate(originalBounds.width * newScale, animationDuration)
            }
            control.scale(80.millis, Point2D(newScale)) {
                node.translateY = -(node.boundsInLocal.height*(1-newScale))/2
                println("node.translateY = ${node.translateY}")
            }
            root.layout()
            root.applyCss()
        }
    }




    init {
        runLater {
            control.apply {
                contextMenu = contextmenu(unit.configView.itemsLambda)
            }

        }
    }
}