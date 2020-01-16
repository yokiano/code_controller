package yarden.mytools.codecontroller.presentation.implementations.tornadofx

import GuiEventsChannel
import GuiPresentationDriver
import javafx.application.Platform
import javafx.stage.Stage
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance
import tornadofx.*
import yarden.mytools.codecontroller.presentation.common.entities.CCGuiUnit

class TornadoDriver(override val kodein: Kodein) : Controller(), GuiPresentationDriver, KodeinAware {

    val tornadoApp: TornadoApp by instance()

    val unitsList = UnitsListViewModel(UnitList())
    private val eventsChannel: GuiEventsChannel by instance()

    val chartSeries = ChartSeriesViewModel(ChartSeries())

    override fun launchApp() {
        tornadoApp.init()
        Platform.startup {
            val stage = Stage().apply {
                minHeight = 300.0
                x = 20.0
                y = 900.0
                isFullScreen = true
                // TODO - change to something other than hard coded numbers. using Screen.getScreens() and the visual bounds property
            }
            tornadoApp.start(stage)
        }
    }

    override fun addUnit(ccUnit: CCGuiUnit) {
        val tUnit = UnitAdapter.toTornadoUnit(ccUnit)
        val tUnitVM = TUnitViewModel(tUnit)
        // runLater is required so the driver will be updated from the JavaFX thread.
        runLater {
            unitsList.apply {
                item.list.add(tUnitVM)
                item.sort()
                tUnit.valueProperty.onChange {
                    eventsChannel.send(UnitAdapter.toCCUnit(tUnit))
                }
            }

        }

        reloadViews()


    }

    fun addDataPoint(x : Double, y : Double) {
        runLater {
            chartSeries.item.add(Vector2D(x,y))
        }

        if (!chartSeries.visible) {
            reloadViews()
            chartSeries.visible = true
        }
    }

    fun reloadViews() {
        // Reloading the Views.
        runLater {
            FX.primaryStage.scene.findUIComponents().forEach {
                FX.replaceComponent(it)
            }
        }
    }

}
