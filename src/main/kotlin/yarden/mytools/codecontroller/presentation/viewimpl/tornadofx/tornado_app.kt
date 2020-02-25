package yarden.mytools.codecontroller.presentation.viewimpl.tornadofx

import XYControl
import javafx.beans.value.ObservableValue
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.XYChart
import javafx.scene.control.ScrollPane
import javafx.scene.control.ToggleButton
import javafx.scene.control.Tooltip
import javafx.scene.layout.FlowPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance
import org.kodein.di.tornadofx.kodein
import tornadofx.*
import yarden.mytools.codecontroller.presentation.viewimpl.tornadofx.panes.InfoPane
import yarden.mytools.codecontroller.presentation.viewimpl.tornadofx.panes.MenuPane
import yarden.mytools.codecontroller.presentation.viewimpl.tornadofx.panes.VectorPane

// TESTTTT
class TestView : View() {

    val button = button("something")

    val leftPane = scrollpane {
        vbox {
            button("action") {
                action {
                    println("${root.dividers}")

                }
            }

        }
    }

    val rightPane = scrollpane {
        vbox {
            button(" king")
        }
    }
    val morePane = scrollpane {
        vbox {
            button(" more king")
        }
    }
    override val root = splitpane(Orientation.HORIZONTAL, button).apply {
        //        dividers.first().position = 1.0
        style {
            backgroundColor += Color.RED
        }
    }

    init {
        root.items.add(leftPane)
        root.items.add(rightPane)
        root.items.add(morePane)

        println("aaa")
        println("fdf")
//        root.items.add(leftPane)
    }
//    override val root = splitpane(Orientation.HORIZONTAL, leftPane, rightPane).apply {
//        dividers.first().position = 1.0
//        style {
//            backgroundColor += Color.RED
//        }
//    }

/*
    val root_secondary = anchorpane {

        autosize()
        val screenBounds = Screen.getPrimary().bounds

        borderpane {

            fitToParentSize()
            left = hbox {
                autosize()
                hgrow = Priority.ALWAYS

                right = linechart("TEST CHART", NumberAxis(), NumberAxis()) {
                    autosize()
                    val a: Number = 0.2
                    val b = a


                    val resizeBinding = doubleBinding(this@anchorpane.widthProperty(),prefWidthProperty()) {
                        this.value / 2.0
                    }
                    */
/*
                    val resizeBinding = prefWidthProperty().doubleBinding(this@anchorpane.widthProperty()) {
                        it?.let { it.toDouble() / 2.0 } ?: this.width

                    }*//*

                    prefWidthProperty().bind(resizeBinding)
//                    prefWidthProperty().bind(this@anchorpane.widthProperty())

                    anchorpaneConstraints {
                        leftAnchor = 0.0
                        rightAnchor = 0.0
                    }


                }
            }
        }
    }
*/
}

class TornadoApp(override val kodein: Kodein) : App(MainView2::class, MyStyle::class), KodeinAware {
    init {
        reloadStylesheetsOnFocus()
        reloadViewsOnFocus()
    }
}


class MainView() : View() {

    private val driver: TornadoDriver by kodein().instance()

    override val root = borderpane {
        addClass(MyStyle.root)

        left {
            // ------ LABELS AND MAIN MENU ------ //                                                // ------ LABELS AND MAIN MENU ------ //                                                // ------ LABELS AND MAIN MENU ------ //
            hbox {

                // --- MAIN MENU
                // TODO - add option to hide the menu
                MenuPane.root.attachTo(this)

                // --- INFO LABELS
                InfoPane().root.attachTo(this)

            }
        }

        // ------ TOGGLES AND SLIDERS ------ //                                                // ------ TOGGLES AND SLIDERS ------ //                                                // ------ TOGGLES AND SLIDERS ------ //

        center {

            scrollpane {
                hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
                // --- Controllers HBOX
                flowpane {
                    alignment = Pos.CENTER
                    hgrow = Priority.ALWAYS
                    addClass(MyStyle.controllersFlowpane)

                    // -------------- TOGGLES --------------
                    flowpane {
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
        }

        // ------ XY-CONTROLLERS ------ //                                                // ------ XY-CONTROLLERS ------ //                                                // ------ XY-CONTROLLERS ------ //
        flowpane {
            alignment = Pos.CENTER
            paddingAll = 20.0
            bindChildren(driver.unitsList.listVM.value.filter { it.item is TXYControl }.toObservable()) { unitVM ->
                val unit = unitVM.item as TXYControl
                XYControl(unit.id, unit.range, unit.valueProperty).run {
                    if (!driver.hideConfigButtons) attachConfigButtons(unit.configView)
                    root.attachTo(this@flowpane)
                }
            }
        }

        // ------ PLOT  ------ //                                                // ------ PLOT  ------ //                                                // ------ PLOT  ------ //
        right {
            vbox {

                val seriesList = ArrayList<XYChart.Series<Number, Number>>()
                if (driver.plotter.visible) {
                    val xAxis = NumberAxis().apply { tickLabelFill = MyStyle.textColor }
                    val yAxis = NumberAxis().apply { tickLabelFill = MyStyle.textColor }

                    linechart("Plotter", xAxis, yAxis) {
                        addClass(MyStyle.lineChart)
                        xAxis.isForceZeroInRange = false
                        vgrow = Priority.ALWAYS
                        animated = false

                        val widthResizeBinding =
                            doubleBinding(this@borderpane.widthProperty(), prefWidthProperty()) { value / 4.0 }
                        prefWidthProperty().bind(widthResizeBinding)
//                        prefWidth = driver.screenBounds.width / 4.0


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
        getAllNodes(this).filter { it is HBox || it is VBox || it is FlowPane || it is ScrollPane }
            .addClass(MyStyle.someBox)
    }

    // ------ FUNCTIONS ------ //                                                // ------ FUNCTIONS ------ //                                                // ------ FUNCTIONS ------ //
    private fun ToggleButton.updateToggleStyle(value: Boolean) {
        if (value) {
            removeClass(MyStyle.toggleButtonOff)
            addClass(MyStyle.toggleButtonOn)
        } else {
            removeClass(MyStyle.toggleButtonOn)
            addClass(MyStyle.toggleButtonOff)
        }
    }

    private fun ToggleButton.hideConfigButtonStyle(value: Boolean) {
        if (value) {
            removeClass(MyStyle.hideConfigOff)
            addClass(MyStyle.hideConfigOn)
        } else {
            removeClass(MyStyle.hideConfigOn)
            addClass(MyStyle.hideConfigOff)
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

// Dummy view to have a single configuration file.
object GlobalConfig : View() {
    override val root = borderpane { }
}
