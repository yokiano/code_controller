package yarden.mytools.codecontroller.domain

import GuiEventsChannel
import GuiStateController
import GuiUnitsChannel
import PlotterChannel
import yarden.mytools.codecontroller.presentation.implementations.tornadofx.TornadoDriver
import yarden.mytools.codecontroller.presentation.implementations.tornadofx.TornadoApp
import kotlinx.coroutines.*
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.*
import org.kodein.di.tornadofx.installTornadoSource
import yarden.mytools.codecontroller.domain.entities.CCPlotter

@ExperimentalCoroutinesApi
object CodeController : KodeinAware {

    // <<< Kodein configurations
    data class UnitKodeinParams(val type: CCType, val id: String)

    private var state : ControllerState = ControllerState.Unused

    override val kodein = Kodein {
        installTornadoSource()

        bind<CCUnit>() with multiton { params: UnitKodeinParams ->
            params.run {
                when (type) {
                    CCType.BOOL -> CCBool(id)
                    CCType.DOUBLE -> CCDouble(id)
                }
            }
        }

        bind<GuiEventsChannel>() with singleton { GuiEventsChannel() }
        bind<GuiUnitsChannel>() with singleton { GuiUnitsChannel() }
        bind<GuiStateController>() with singleton { GuiStateController(kodein) }
        bind<TornadoDriver>() with singleton { TornadoDriver(kodein) }
        bind<TornadoApp>() with singleton { TornadoApp(kodein) }

        bind<CCPlotter>() with singleton { CCPlotter(kodein) }
        bind<PlotterChannel>() with singleton { PlotterChannel() }

    }
    // Kodein configurations >>>

    // <<< Properties declaration
    private val eventsChannel : GuiEventsChannel by instance()
    private val uiChannel : GuiUnitsChannel by  instance()
    private val guiStateController : GuiStateController by instance()

    private val plotter : CCPlotter by instance()
    // Properties declaration >>>

    init {
        // Launch the TornadoFx application
        guiStateController.launchApp()

        // launch a background job to get events from the UI.
        GlobalScope.launch {
            eventsConsumer()
        }
    }


    private suspend fun eventsConsumer() {
        // The for loop will consume each event once it arrives and will never finish.
        for (event in eventsChannel.channel) {
            event.run {
                // This instance() call will fetch the (only) unit with the specified ID from the defined multiton in kodein.
                val unit: CCUnit by kodein.instance(
                    arg = UnitKodeinParams(
                        type = ccType,
                        id = this.id
                    )
                )
                unit.updateValue(event.value)
            }
        }
    }

    private fun getUnit(type: CCType, id: String): CCUnit {
        val unit: CCUnit by instance(
            arg = UnitKodeinParams(
                type,
                id
            )
        )
        return unit
    }

    private fun <T : CCUnit >handleUnitState(unit: T,initCode:  T.() -> Unit) {
        unit.run {
            when(state) {
                CCUnitState.NEW -> {
                    initCode()
                    sendGuiUnit(uiChannel.channel)
                    state = CCUnitState.LIVE

                    if (this@CodeController.state is ControllerState.Unused) {
                        // TODO - here I need to implement the init (only when actually in use)
                        this@CodeController.state = ControllerState.InUse
                    }
                }
                else -> {}
            }
        }


    }

    fun ccBool(id: String, initCode: CCBool.() -> Unit = {}): Boolean {
        val unit = (getUnit(CCType.BOOL, id) as CCBool)
        handleUnitState(unit, initCode)
        return unit.value
    }

    fun ccDouble(id: String, initCode: CCDouble.() -> Unit = {}): Double {
        val unit = (getUnit(CCType.DOUBLE, id) as CCDouble)
        handleUnitState(unit,initCode)
        return unit.value
    }

    fun ccPlot(x : Double, y: Double) {
        plotter.sendData(Pair(x,y))
    }

    sealed class ControllerState {
        object Unused : ControllerState()
        object InUse : ControllerState()
    }
}