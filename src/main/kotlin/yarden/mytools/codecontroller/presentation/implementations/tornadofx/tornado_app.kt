package yarden.mytools.codecontroller.presentation.implementations.tornadofx

import XYControl
import javafx.beans.value.ObservableValue
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.XYChart
import javafx.scene.control.ToggleButton
import javafx.scene.control.Tooltip
import javafx.scene.layout.*
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

object GlobalConfig : View() {
    override val root = borderpane { }

}

class MainView() : View() {

    private val driver: TornadoDriver by kodein().instance()

    override val root = borderpane {
        addClass(MyStyle.root)

        // ------ LABELS AND XY CONTROLLERS ------ //
        left {
            hbox {

                // --- iNFO LABELS
                vbox {
                    addClass(MyStyle.labelsPane)
                    maxWidth = 250.0
                    paddingVertical = 20.0
                    paddingHorizontal = 10.0
                    bindChildren(driver.infoLabelList.asObservable()) { infoLabel ->
                        hbox {
                            label("${infoLabel.id} - ") {
                                prefWidth = 150.0
                            }
                            label {
                                textProperty().bindBidirectional(infoLabel.valueProperty)
                            }
                        }
                    }
                }

                // --- XY CONTROLLERS
                flowpane {
                    alignment = Pos.CENTER
                    paddingAll = 20.0
                    bindChildren(driver.unitsList.listVM.value.filter { it.item is TXYControl }.toObservable()) { unitVM ->
                        val unit = unitVM.item as TXYControl
//                        println("unit.value = ${unit.value.x}")
                        val c = XYControl(unit.id, unit.range, unit.value)

//                        unit.valueProperty.onChange {
//                            it?.let {
//                                println("changing")
//                                c.updatePointer(it.x, it.y)
//                            }
//
//                        }
                        c.pointerProperty.bindBidirectional(unit.valueProperty)
//                        c.pointer.xProperty.bindBidirectional(unit.valueProperty.value.xProperty)
//                        c.pointerProperty.value.yProperty.bindBidirectional(unit.valueProperty.value.yProperty)
                        c.attachConfigButtons(unit.configView)
                        c.root.attachTo(this)
                    }
                }
            }
        }
        // ------ TOGGLES AND SLIDERS ------ //
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
                                        updateToggleStyle(unit.valueProperty.value)

                                        selectedProperty().onChange {
                                            unit.valueProperty.value = !unit.valueProperty.value
                                            updateToggleStyle(unit.valueProperty.value)
                                        }
                                    }

                                    unit.configView.root.maxWidth = this.width
                                    unit.configView.root.attachTo(this)
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

                                    // Attaching the config buttons
                                    val item = (unitVM.item as TSlider)
                                    item.configView.root.maxWidth = this.width
                                    item.configView.root.attachTo(this)

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
        // -------------- PLOT & ON/OFF TOGGLE--------------
        right {
            borderpane {
                right {
                    hbox {
                        separator(Orientation.VERTICAL) {
                            paddingHorizontal = 10.0
                        }
                        togglebutton("") {
                            addClass(MyStyle.toggleButton)
                            isSelected = true
                            updateToggleStyle(true)

                            selectedProperty().onChange {
                                driver.internalChannel.send(it)
                                updateToggleStyle(isSelected)
                            }
                        }
                    }
                }
                left {
                    vbox {
                        val seriesList = ArrayList<XYChart.Series<Number, Number>>()
                        if (driver.plotter.visible) {
                            val xA = NumberAxis()
                            val yA = NumberAxis()
                            linechart("Plotter", xA, yA) {
                                xA.isForceZeroInRange = false
                                vgrow = Priority.ALWAYS
                                animated = false
                                addClass(MyStyle.lineChart)
                                for (plotLine in driver.plotter.lines) {
                                    val singleSeries =
                                        series(plotLine.id) {
                                            addClass(MyStyle.plotLine)
                                            // add the already existing data points
                                            for (dataPoint in plotLine.dataPointsList) {
                                                data(dataPoint.x, dataPoint.y)
                                            }
                                        }
                                    seriesList.add(singleSeries)

                                    plotLine.dataPointsList.onChange { listChange ->
                                        val newData = listChange.list.last()

                                        singleSeries.apply {


                                            data(newData.x, newData.y)
                                            if (singleSeries.data.size > driver.plotter.maxDataPoints) {
                                                singleSeries.data.removeAt(0)
                                            }
                                            //                                    if (chart.data[0].data.size > driver.plotter.maxDataPoints ) {
                                            //                                        chart.data[0].data.removeAt(0)
                                            //                                    }
                                        }
                                    }
                                }
                            }
                        }
                        hbox {
                            for (plotLine in driver.plotter.lines) {
                                button("Reset ${plotLine.id}") {
                                    action {
                                        for (series in seriesList) {
                                            if (series.name == plotLine.id) {
                                                series.data.clear()
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

            }
        }
        getAllNodes(this).filter { it is HBox || it is VBox || it is FlowPane }.addClass(MyStyle.someBox)
    }

    private fun ToggleButton.updateToggleStyle(value: Boolean) {
        if (value) {
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
