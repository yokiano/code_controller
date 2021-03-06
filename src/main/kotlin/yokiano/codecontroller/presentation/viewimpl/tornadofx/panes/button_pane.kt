package yokiano.codecontroller.presentation.viewimpl.tornadofx.panes

import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.ScrollPane
import javafx.scene.layout.Priority
import tornadofx.*
import yokiano.codecontroller.presentation.viewimpl.tornadofx.*
import yokiano.codecontroller.presentation.viewimpl.tornadofx.controls.ToggleView

class ButtonPane : ResponsivePane() {

    override val type = PaneType.Button

    override lateinit var draggable: Node

    override val paneRoot = scrollpane {
        hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
        vgrow = Priority.ALWAYS
        fixScrollerMouseEvents(this)
        flowpane {
            draggable = this
            prefWidthProperty().bind(this@scrollpane.widthProperty())
            alignment = Pos.CENTER
            paddingTop = 10.0
            hgrow = Priority.ALWAYS
            orientation = Orientation.HORIZONTAL
            // Toggles pane
            addClass(MyStyle.togglesVBox)
            bindChildren(driver.unitsList.listVM.value.filter { it.item is TToggle }.toObservable()) { unitVM ->
                when (val unit = unitVM.item) {
                    is TToggle -> {
                        ToggleView(unit).run {
                            setControlScaleHandler(controlScale)
                            root
                        }
                    }
                    else -> {
                        label("Unrecognized control unit")
                    }
                }
            }
        }
    }


}


