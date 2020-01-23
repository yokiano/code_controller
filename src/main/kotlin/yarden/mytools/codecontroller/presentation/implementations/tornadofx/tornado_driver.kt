package yarden.mytools.codecontroller.presentation.implementations.tornadofx

import GuiEventsChannel
import GuiPresentationDriver
import XYControl
import javafx.application.Platform
import javafx.stage.Stage
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance
import tornadofx.*
import yarden.mytools.codecontroller.domain.CCUnitState
import yarden.mytools.codecontroller.presentation.common.entities.CCGuiUnit

@Suppress("MemberVisibilityCanBePrivate")
class TornadoDriver(override val kodein: Kodein) : Controller(), GuiPresentationDriver, KodeinAware {

    val tornadoApp: TornadoApp by instance()

    val unitsList = UnitsListViewModel(UnitList())
    private val eventsChannel: GuiEventsChannel by instance()

    val plotter = Plotter()


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

    var once = false
    override fun addUnit(ccUnit: CCGuiUnit) {
        val tUnit = UnitAdapter.toTornadoUnit(ccUnit)
        val tUnitVM = TUnitViewModel(tUnit)
        // runLater is required so the driver will be updated from the JavaFX thread.
        runLater {
            unitsList.apply {
                item.list.add(tUnitVM)
                item.sort()
                tUnit.valueProperty.onChange {
                    println("sending data")
                    eventsChannel.send(UnitAdapter.toCCUnit(tUnit))
                }
            }

        }
        reloadViews()
    }

    fun addDataPointTo(id: String, data : Pair<Double,Double>) {
        val plotLine : PlotLine by instance( arg = id)
        val dataVec = Vector2D(data.first,data.second)
        runLater {
            plotLine.add(dataVec)
        }

        if (plotLine.state == CCUnitState.NEW) {
            plotter.addPlotLine(plotLine)
            reloadViews()
            plotLine.state = CCUnitState.LIVE
        }

        if (!plotter.visible) {
            reloadViews()
            plotter.visible = true
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
