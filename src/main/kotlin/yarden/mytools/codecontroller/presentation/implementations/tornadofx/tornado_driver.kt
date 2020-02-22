package yarden.mytools.codecontroller.presentation.implementations.tornadofx

import GuiEventsChannel
import GuiPresentationDriver
import InternalChannel
import javafx.application.Platform
import javafx.geometry.Rectangle2D
import javafx.stage.Screen
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
    val internalChannel: InternalChannel by instance()

    val plotter = Plotter()

    val infoLabelList = ArrayList<TInfoLabel>()

    var hideConfigButtons = false

    val screenBounds: Rectangle2D
        get() = Screen.getPrimary().visualBounds

    fun playWithWindow() {
        primaryStage.widthProperty()
    }

    override fun launchApp() {

        tornadoApp.init()
        Platform.startup {
            val stage = Stage().apply {

                height = screenBounds.height / 3.0
                width = screenBounds.width - 30.0

                x = (screenBounds.width - width) / 2.0
                y = screenBounds.height - height - 40.0
//                isFullScreen = true
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

                tUnit.stateProperty.onChange {
                    when (it) {
                        CCUnitState.DEAD -> {
                            println("DEAD detected on ${tUnit.id}")
                            eventsChannel.send(UnitAdapter.toCCUnit(tUnit))

                            removeUnit(tUnitVM)
                        }
                    }


                }
            }

        }
        tUnit.configView.loadFromConfigFile()
        reloadViews()
    }

    fun removeUnit(tUnit: TUnitViewModel<out Any?>) {
        unitsList.item.list.remove(tUnit)
        reloadViews()
    }

    fun addDataPointTo(id: String, data: Pair<Double, Double>, limit: Int) {
        val plotLine: PlotLine by instance(arg = id)
        val dataVec = Vector2D(data.first, data.second)

        // Don't issue data point if we reached the limit.
        if (plotLine.dataPointsList.size >= limit) {
            return
        }

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

    fun updateInfoLabel(id: String, info: String) {
        val infoLabel: TInfoLabel by instance(arg = id)

        if (infoLabel.state == CCUnitState.NEW) {
            infoLabelList.add(infoLabel)
            reloadViews()
            infoLabel.state = CCUnitState.LIVE
        }

        runLater {
            infoLabel.valueProperty.value = info
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
