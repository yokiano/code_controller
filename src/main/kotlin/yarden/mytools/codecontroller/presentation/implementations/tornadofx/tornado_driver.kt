package yarden.mytools.codecontroller.presentation.implementations.tornadofx

import GuiEventsChannel
import GuiPresentationDriver
import javafx.application.Platform
import javafx.stage.Stage
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance
import yarden.mytools.codecontroller.presentation.common.entities.CCGuiUnit
import tornadofx.Controller
import tornadofx.onChange
import tornadofx.runLater

class TornadoDriver(override val kodein: Kodein) : Controller(), GuiPresentationDriver, KodeinAware {

    val unitsList = UnitsListViewModel(UnitList())
    private val eventsChannel : GuiEventsChannel by instance()

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

    }

    override fun launchApp() {
            val tornadoApp: TornadoApp by instance()
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


}
