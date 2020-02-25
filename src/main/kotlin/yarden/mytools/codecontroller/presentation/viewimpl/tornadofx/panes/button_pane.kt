package yarden.mytools.codecontroller.presentation.viewimpl.tornadofx.panes

import XYControl
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.control.ScrollPane
import javafx.scene.layout.Priority
import org.kodein.di.generic.instance
import org.kodein.di.tornadofx.kodein
import tornadofx.*
import yarden.mytools.codecontroller.presentation.viewimpl.tornadofx.*

class ButtonPane : ResponsivePane() {

    override val type = PaneType.Button

    override val root = scrollpane {
        hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
        flowpane {
            prefWidthProperty().bind(this@scrollpane.widthProperty())

            orientation = Orientation.HORIZONTAL
            // Toggles pane
            addClass(MyStyle.togglesVBox)
            alignment = Pos.BASELINE_LEFT
            bindChildren(driver.unitsList.listVM.value.filter { it.item is TToggle }.toObservable()) { unitVM ->
                when (val unit = unitVM.item) {
                    is TToggle -> {
                        vbox {
                            alignment = Pos.CENTER
                            label(unit.id) {
                                addClass(MyStyle.toggleLabel)
                            }
                            val button = togglebutton("") {
                                addClass(MyStyle.toggleButton)
                                isSelected = unit.initialValue
                                updateToggleStyle(unit.valueProperty.value)

                                selectedProperty().onChange {
                                    unit.valueProperty.value = !unit.valueProperty.value
                                    updateToggleStyle(unit.valueProperty.value)
                                }
                            }

                            // Attach config buttons.
                            if (!driver.hideConfigButtons) {
                                unit.configView.root.run {
                                    maxWidth = 100.0
                                    attachTo(this@vbox)
                                }
                            }
                        }
                    }
                    else -> {
                        label("Unrecognized control unit")
                    }
                }
            }
        }
    }

    init {
        minWidth = 150.0
        minHeight = 300.0


        root.minWidth = minWidth
        root.minHeight = minHeight
    }

}


