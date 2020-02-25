package yarden.mytools.codecontroller.presentation.viewimpl.tornadofx.panes

import javafx.scene.Parent
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.XYChart
import javafx.scene.layout.Priority
import tornadofx.*
import yarden.mytools.codecontroller.presentation.viewimpl.tornadofx.MyStyle
import yarden.mytools.codecontroller.presentation.viewimpl.tornadofx.PaneType

class PlotPane() : ResponsivePane() {

    override val type = PaneType.Plot

    override val root = vbox {

        val seriesList = ArrayList<XYChart.Series<Number, Number>>()
        if (driver.plotter.visible) {
            val xAxis = NumberAxis().apply { tickLabelFill = MyStyle.textColor }
            val yAxis = NumberAxis().apply { tickLabelFill = MyStyle.textColor }

            linechart("Plotter", xAxis, yAxis) {
                addClass(MyStyle.lineChart)
                xAxis.isForceZeroInRange = false
                vgrow = Priority.ALWAYS
                animated = false


/*  This is a binding to make the width 4 times of the main window width. will implement it probably via the split pane dividers.
                val widthResizeBinding =
                    doubleBinding(this@borderpane.widthProperty(), prefWidthProperty()) { value / 4.0 }
                prefWidthProperty().bind(widthResizeBinding)
*/

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