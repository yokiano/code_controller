package yarden.mytools.codecontroller.presentation.viewimpl.tornadofx.panes


import javafx.beans.value.ObservableValue
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.control.ScrollPane
import javafx.scene.control.Tooltip
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import org.kodein.di.generic.instance
import org.kodein.di.tornadofx.kodein
import tornadofx.*
import yarden.mytools.codecontroller.presentation.viewimpl.tornadofx.MyStyle
import yarden.mytools.codecontroller.presentation.viewimpl.tornadofx.PaneType
import yarden.mytools.codecontroller.presentation.viewimpl.tornadofx.TSlider
import yarden.mytools.codecontroller.presentation.viewimpl.tornadofx.TornadoDriver

class SliderPane : ResponsivePane() {

    override val type = PaneType.Slider

    override val root = scrollpane {
        hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
        flowpane {
            prefWidthProperty().bind(this@scrollpane.widthProperty())
            alignment = Pos.CENTER
            paddingAll = 20
            hgrow = Priority.ALWAYS

            bindChildren(driver.unitsList.listVM.value.filter { it.item is TSlider }.asObservable()) { unitVM ->
                when (val unit = unitVM.item) {
                    is TSlider -> {
                        val unitValueP = unit.valueProperty as ObservableValue<Number>
                        vbox {
//                            paddingHorizontal = 30
                            hgrow = Priority.ALWAYS

                            label(unit.id) {
                                addClass(MyStyle.sliderLabel)
                                hgrow = Priority.ALWAYS

                            }
                            autosize()
                            slider(range = unit.range, value = unit.initialValue) {
                                addClass(MyStyle.sliderStyle)
                                alignment = Pos.CENTER
                                hgrow = Priority.ALWAYS
                                //                                        orientation = Orientation.VERTICAL

                                bind(unit.valueProperty)
                                onDoubleClick {
                                    unitVM.valueVM.value.value = unit.initialValue
                                }
                                onRightClick {
                                    println("saving data to file")
                                }
                                onHover {
                                    tooltip =
                                        Tooltip("${unit.range.toString()} \n *Double-Click to reset \n *Right-Click to save value")
                                }
                            }
                            textfield(unitValueP) {
                                addClass(MyStyle.sliderTextField)
                                hgrow = Priority.ALWAYS
                                autosize()
                                onHover {
                                    tooltip = Tooltip(unit.valueProperty.value.toString())
                                }
                            }

                            // Attaching the config buttons
                            if (!driver.hideConfigButtons) unit.configView.root.run {
                                maxWidth = 100.0
                                attachTo(this@vbox)
                            }

                        }
                    }
                    else -> {
                        label("Unrecognized control unit") {
                            style {
                                textFill = Color.RED
                            }
                        }
                    }
                }
            }
        }
    }


}
