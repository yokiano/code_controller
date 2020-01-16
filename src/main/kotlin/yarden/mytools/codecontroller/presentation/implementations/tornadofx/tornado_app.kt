package yarden.mytools.codecontroller.presentation.implementations.tornadofx

import javafx.beans.value.ObservableValue
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.chart.NumberAxis
import javafx.scene.control.ToggleButton
import javafx.scene.control.Tooltip
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance
import org.kodein.di.tornadofx.kodein
import tornadofx.*

class TornadoApp(override val kodein: Kodein) : App(MainView::class, MyStyle::class), KodeinAware {
    init {
        reloadStylesheetsOnFocus()
        reloadViewsOnFocus()
    }
}

class MainView() : View() {

    private val driver: TornadoDriver by kodein().instance()

    override val root = borderpane {
        addClass(MyStyle.root)

        center {
            hbox {
                // Main hbox
                alignment = Pos.CENTER

                // -------------- TOGGLES --------------
                flowpane {
                    // Toggles pane
                    addClass(MyStyle.togglesVBox)
                    alignment = Pos.CENTER
                    orientation = Orientation.VERTICAL
                    bindChildren(driver.unitsList.listVM.value.filter { it.item is TToggle }.toObservable()) { unitVM ->
                        when (val unit = unitVM.item) {
                            is TToggle -> {
                                vbox {
                                    alignment = Pos.CENTER
                                    label(unit.id) {
                                        addClass(MyStyle.toggleLabel)
                                    }
                                    togglebutton("") {
                                        addClass(MyStyle.toggleButton)
                                        isSelected = unit.initialValue
                                        updateToggleStyle(unit)

                                        selectedProperty().onChange {
                                            unit.valueProperty.value = !unit.valueProperty.value
                                            updateToggleStyle(unit)
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

                // -------------- SLIDERS --------------
                hbox {
                    // Sliders pane (hbox)
                    alignment = Pos.CENTER
                    paddingAll = 20

                    bindChildren(driver.unitsList.listVM.value.filter { it.item is TSlider }.asObservable()) { unitVM ->
                        when (val unit = unitVM.item) {
                            is TSlider -> {
                                val unitValueP = unit.valueProperty as ObservableValue<Number>
                                vbox {
                                    paddingHorizontal = 30

                                    label(unit.id) {
                                        addClass(MyStyle.sliderLabel)
                                    }
                                    autosize()
                                    slider(range = unit.range, value = unit.initialValue) {
                                        addClass(MyStyle.sliderStyle)
                                        alignment = Pos.CENTER
                                        vgrow = Priority.ALWAYS
                                        orientation = Orientation.VERTICAL

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
                                        autosize()
                                        onHover {
                                            tooltip = Tooltip(unit.valueProperty.value.toString())
                                        }
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
        // -------------- PLOT --------------
        right {
//            val chartList = driver.chartSeries.plotList
            val pointList = driver.chartSeries.dataPointlistVM.value
            println("driver.chartSeries.dataPointlistVM.value.size = ${pointList.size}")
            if (pointList.size > 0) {
                linechart("Plotter", NumberAxis(), NumberAxis()) {
                    addClass(MyStyle.lineChart)
                    val singleSeries = series("X") {
                        addClass(MyStyle.chartSeries)
                        for (dataPoint in pointList) {
                            data(dataPoint.x,dataPoint.y)
                        }
                    }

                    pointList.onChange{
                        val newData = it.list.last()
                        singleSeries.apply {
                            data(newData.x,newData.y)
                        }
                    }
                }
            }

        }
        getAllNodes(this).filter { it is HBox || it is VBox }.addClass(MyStyle.someBox)
    }

    private fun ToggleButton.updateToggleStyle(unit: TToggle) {
        if (unit.valueProperty.value == true) {
            removeClass(MyStyle.toggleButtonOff)
            addClass(MyStyle.toggleButtonOn)
        } else {
            removeClass(MyStyle.toggleButtonOn)
            addClass(MyStyle.toggleButtonOff)

        }
    }
}

fun getAllNodes(root: Parent): ArrayList<Node> {
    val nodes = ArrayList<Node>()
    fun recurseNodes(node: Node) {
        nodes.add(node)
        if (node is Parent)
            for (child in node.childrenUnmodifiable) {
                recurseNodes(child)
            }
    }
    recurseNodes(root)
    return nodes
}