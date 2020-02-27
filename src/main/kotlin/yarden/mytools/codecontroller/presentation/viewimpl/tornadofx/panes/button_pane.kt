package yarden.mytools.codecontroller.presentation.viewimpl.tornadofx.panes

import XYControl
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.control.ScrollPane
import javafx.scene.layout.Priority
import org.kodein.di.generic.instance
import org.kodein.di.tornadofx.kodein
import tornadofx.*
import yarden.mytools.codecontroller.presentation.viewimpl.tornadofx.*
import yarden.mytools.codecontroller.presentation.viewimpl.tornadofx.controls.ToggleView

class ButtonPane : ResponsivePane() {

    override val type = PaneType.Button

    override lateinit var draggable: Node

    override val root = vbox {
        draggable = this
        scrollpane {
            hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
            flowpane {
                paddingTop = 10.0
                prefWidthProperty().bind(this@scrollpane.widthProperty())
                alignment = Pos.CENTER
                orientation = Orientation.HORIZONTAL
                // Toggles pane
                addClass(MyStyle.togglesVBox)
                bindChildren(driver.unitsList.listVM.value.filter { it.item is TToggle }.toObservable()) { unitVM ->
                    when (val unit = unitVM.item) {
                        is TToggle -> {
                            ToggleView(unit).root
                        }
                        else -> {
                            label("Unrecognized control unit")
                        }
                    }
                }
            }
        }
    }

    init {
        setMouseEvents()
    }

}


