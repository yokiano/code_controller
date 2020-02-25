package yarden.mytools.codecontroller.presentation.viewimpl.tornadofx

import GuiEventsChannel
import GuiPresentationDriver
import InternalChannel
import javafx.application.Platform
import javafx.geometry.Rectangle2D
import javafx.scene.Node
import javafx.stage.Screen
import javafx.stage.Stage
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance
import tornadofx.*
import yarden.mytools.codecontroller.domain.CCUnitState
import yarden.mytools.codecontroller.presentation.common.entities.CCGuiUnit
import yarden.mytools.codecontroller.presentation.viewimpl.tornadofx.panes.*
import kotlin.reflect.full.createInstance

@Suppress("MemberVisibilityCanBePrivate")
class TornadoDriver(override val kodein: Kodein) : Controller(), GuiPresentationDriver, KodeinAware {

    val tornadoApp: TornadoApp by instance()

    val activePanes = ArrayList<ResponsivePane>()

    val unitsList = UnitsListViewModel(UnitList())
    private val eventsChannel: GuiEventsChannel by instance()
    val internalChannel: InternalChannel by instance()

    val plotter = Plotter()

    val mainView by lazy { find(MainView2::class) }

    val infoLabelList = ArrayList<TInfoLabel>()

    var hideConfigButtons = false // TODO - remove this

    val screenBounds: Rectangle2D
        get() = Screen.getPrimary().visualBounds

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

                addPaneIfNeeded(tUnit)
            }
        }
        tUnit.configView.loadFromConfigFile()
        reloadViews()
    }

    fun removeUnit(tUnit: TUnitViewModel<out Any?>) {
        removePaneIfNeeded(tUnit.item)
        unitsList.item.list.remove(tUnit)

        reloadViews()
    }

    fun addPaneIfNeeded(tUnit: TUnit<*>) {
        if (unitsList.item.isOneOfAType(tUnit)) {
            val newPane = when(tUnit.targetPaneType) {
                PaneType.Slider -> SliderPane()
                PaneType.Button -> ButtonPane()
                PaneType.Vector -> VectorPane()
                PaneType.Plot -> {
                    println("Error when trying to add the first unit to a pane. Type of pane is Plot, we shouldn't reach this situation.")
                    PlotPane()
                }
                PaneType.Info -> {
                    println("Error when trying to add the first unit to a pane. Type of pane is Info, we shouldn't reach this situation.")
                    InfoPane()
                }
                PaneType.Menu -> {
                    println("Error when trying to add the first unit to a pane. Type of pane is Menu, we shouldn't reach this situation.")
                    MenuPane
                }
            }

            activePanes.add(newPane) // Views are reloaded in the outer function, causing this addition to take effect.
        }
    }

    fun removePaneIfNeeded(unit: TUnit<*>) {
        if (unitsList.item.isOneOfAType(unit)) {
            val panesToRemove = when(unit.targetPaneType) {
                PaneType.Slider -> activePanes.filter { it.type == PaneType.Slider }
                PaneType.Button -> activePanes.filter { it.type == PaneType.Button }
                PaneType.Vector -> activePanes.filter { it.type == PaneType.Vector }
                PaneType.Info -> {println("Error when trying to remove unit. Type of pane is Info."); ArrayList<ResponsivePane>()}
                PaneType.Menu -> {println("Error when trying to remove unit. Type of pane is Menu."); ArrayList<ResponsivePane>()}
                PaneType.Plot -> {println("Error when trying to remove unit. Type of pane is Plot."); ArrayList<ResponsivePane>()}
            }

            activePanes.removeAll(panesToRemove)
            panesToRemove.forEach {
                mainView.splitpane.items.remove(it.root)
            }
        }
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
                activePanes.filter { it.type != PaneType.Menu && it.type != PaneType.Info  }.forEach {
                    val tmp = it
                    activePanes.remove(it)
                    activePanes.add(tmp::class.createInstance())
                }
                FX.replaceComponent(it)
            }
        }
    }


}
