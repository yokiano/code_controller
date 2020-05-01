package yokiano.codecontroller.presentation.viewimpl.tornadofx

import GuiEventsChannel
import GuiPresentationDriver
import InternalChannel
import javafx.application.Platform
import javafx.geometry.Orientation
import javafx.geometry.Rectangle2D
import javafx.stage.Screen
import javafx.stage.Stage
import javafx.stage.StageStyle
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance
import tornadofx.*
import yokiano.codecontroller.domain.*
import yokiano.codecontroller.presentation.common.CCGuiUnit
import yokiano.codecontroller.presentation.viewimpl.tornadofx.panes.*
import java.awt.Menu
import kotlin.reflect.full.createInstance

@Suppress("MemberVisibilityCanBePrivate")
class TornadoDriver(override val kodein: Kodein) : Controller(), GuiPresentationDriver, KodeinAware {

    val tornadoApp: TornadoApp by instance<TornadoApp>()

    val activePanes = ArrayList<ResponsivePane>()

    val unitsList = UnitsListViewModel(UnitList())
    private val eventsChannel: GuiEventsChannel by instance<GuiEventsChannel>()
    val internalChannel: InternalChannel by instance<InternalChannel>()

    val plotter = Plotter()

    val mainView by lazy { find(MainView::class) }

    val infoLabelList = ArrayList<TInfoLabel>()

    var globalIsFastResizeEnabled = true

    val primaryScreenBounds: Rectangle2D
        get() = Screen.getPrimary().visualBounds

    val currentScreenBounds: Rectangle2D
        get() {
//            val position =
            Screen.getScreens().forEach {
                if (it.bounds.contains(primaryStage.x, primaryStage.y)) {
                    return it.visualBounds
                }
            }
            return Screen.getPrimary().visualBounds
        }

    var screenOrientation = Orientation.HORIZONTAL

    override fun launchApp(initialOrientation: ScreenOrientation) {

        screenOrientation = when (initialOrientation) {
            HORIZONTAL -> Orientation.HORIZONTAL
            VERTICAL -> Orientation.VERTICAL
            FULL_SCREEN -> Orientation.HORIZONTAL
        }

        tornadoApp.init()
        Platform.startup {
            val stage = Stage().apply {

                // Set default position and dimensions
                when (initialOrientation) {
                    HORIZONTAL -> {
                        height = primaryScreenBounds.height / 3.0
                        width = primaryScreenBounds.width - 30.0
                        x = (primaryScreenBounds.width - width) / 2.0
                        y = primaryScreenBounds.height - height - 40.0 + 400
//                y = screenBounds.height - height - 40.0
                    }
                    VERTICAL -> {
                        width = primaryScreenBounds.width / 3.0
                        height = primaryScreenBounds.height - 40.0
                        x = primaryScreenBounds.width - width
                        y = primaryScreenBounds.height - height
                    }
                    FULL_SCREEN -> isFullScreen = true
                }

                // Change position and dimensions according to saved config.
                runLater {
                    with(WindowConfig.config) {
                        x = double("x", x)
                        y = double("y", y)
                        width = double("width", width)
                        height = double("height", height)
                        screenOrientation = Orientation.values()
                            .getOrElse(int("orientation", screenOrientation.ordinal)) { screenOrientation }
                    }
                }

                initStyle(StageStyle.UNDECORATED)
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
                        else -> {
                            println("illegal state change ($it) was detected for ${tUnit.id} ")
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
            val newPane = when (tUnit.targetPaneType) {
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
            }

            addNewPanes(newPane)  // Views are reloaded in the outer function, causing this addition to take effect.
        }
    }

    fun addNewPanes(vararg panes: ResponsivePane) {
        activePanes.apply {
            addAll(panes)
            sortPanes()
        }
        reloadViews()
    }

    private fun removePanes(vararg panes: ResponsivePane) {
        activePanes.apply {
            removeAll(panes)
            sortPanes()
        }

        mainView.splitpane.items.removeAll(panes.map { it.root })
    }

    fun removePaneIfNeeded(unit: TUnit<*>) {
        if (unitsList.item.isOneOfAType(unit)) {
            val panesToRemove = when (unit.targetPaneType) {
                PaneType.Slider -> activePanes.filter { it.type == PaneType.Slider }
                PaneType.Button -> activePanes.filter { it.type == PaneType.Button }
                PaneType.Vector -> activePanes.filter { it.type == PaneType.Vector }
                PaneType.Info -> {
                    println("Error when trying to remove unit. Type of pane is Info."); ArrayList<ResponsivePane>()
                }
                PaneType.Plot -> {
                    println("Error when trying to remove unit. Type of pane is Plot."); ArrayList<ResponsivePane>()
                }
            }

            removePanes(*panesToRemove.toTypedArray())
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

        // Checking if it's the first data point in a LINE
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
        runLater {
            // Reloading the Views. look kind of dumb but needed to trigger the views and panes to reload.
            FX.primaryStage.scene.findUIComponents().forEach {
                activePanes.filter { it.type != PaneType.Info }.forEach {
                    val tmp = it
                    activePanes.remove(it)
                    activePanes.add(tmp::class.createInstance())
                }
                FX.replaceComponent(it)
            }

        }
    }

    fun flipOrientation() {
        screenOrientation = when (screenOrientation) {
            Orientation.HORIZONTAL -> Orientation.VERTICAL
            Orientation.VERTICAL -> Orientation.HORIZONTAL
        }

        mainView.splitpane.orientation = screenOrientation

        activePanes.forEach {
            it.setup()
        }

        currentScreenBounds.apply {
            val widthRate = MenuPane.primaryStage.width / width
            val heightRate = MenuPane.primaryStage.height / height
            MenuPane.primaryStage.width = (heightRate * width).coerceAtMost(maxX)
            MenuPane.primaryStage.height = (widthRate * height).coerceAtMost(maxY)

            val newY = (((MenuPane.primaryStage.x - minX) / (maxX - minX)) * height) + minY
            val newX = (((MenuPane.primaryStage.y - minY) / (maxY - minY)) * width) + minX
            MenuPane.primaryStage.x = newX.coerceIn(minX, maxX - MenuPane.primaryStage.width)
            MenuPane.primaryStage.y = newY.coerceIn(minY, maxY - MenuPane.primaryStage.height)

        }

        MenuPane.adjustButtonOrientation()
        mainView.borderPane.children.remove(MenuPane.root)
        when (screenOrientation) { // (The new orientation)
            Orientation.HORIZONTAL -> {
                mainView.borderPane.left = MenuPane.root
            }
            Orientation.VERTICAL -> {
                mainView.borderPane.top = MenuPane.root
            }

        }
    }
}

fun ArrayList<ResponsivePane>.sortPanes() {
    this.sortBy { it.type.ordinal }
}
