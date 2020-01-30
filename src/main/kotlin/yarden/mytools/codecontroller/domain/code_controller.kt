package yarden.mytools.codecontroller.domain

import GuiEventsChannel
import GuiStateController
import GuiUnitsChannel
import InfoLabelChannel
import InternalChannel
import PlotterChannel
import yarden.mytools.codecontroller.presentation.implementations.tornadofx.TornadoDriver
import yarden.mytools.codecontroller.presentation.implementations.tornadofx.TornadoApp
import kotlinx.coroutines.*
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.*
import org.kodein.di.tornadofx.installTornadoSource
import reactToChannelOn
import yarden.mytools.codecontroller.domain.entities.CCPlotter
import yarden.mytools.codecontroller.domain.entities.DataPoint
import yarden.mytools.codecontroller.presentation.implementations.tornadofx.PlotLine
import yarden.mytools.codecontroller.presentation.implementations.tornadofx.TInfoLabel
import kotlin.random.Random

@ExperimentalCoroutinesApi
object CodeController : KodeinAware {

    // <<< Kodein configurations
    data class UnitKodeinParams(val type: CCType, val id: String)


    override val kodein = Kodein {
        installTornadoSource()

        bind<CCUnit>() with multiton { params: UnitKodeinParams ->
            params.run {
                when (type) {
                    CCType.BOOL -> CCBool(id)
                    CCType.DOUBLE -> CCDouble(id)
                    CCType.VEC2 -> CCVec(id)
                }
            }
        }

        bind<PlotLine>() with multiton { id: String ->
            PlotLine(id, kodein)
        }

        bind<TInfoLabel>() with multiton { id: String ->
            TInfoLabel(id)
        }

        bind<GuiEventsChannel>() with singleton { GuiEventsChannel() }
        bind<GuiUnitsChannel>() with singleton { GuiUnitsChannel() }
        bind<GuiStateController>() with singleton { GuiStateController(kodein) }
        bind<TornadoDriver>() with singleton { TornadoDriver(kodein) }
        bind<TornadoApp>() with singleton { TornadoApp(kodein) }

        bind<CCPlotter>() with singleton { CCPlotter(kodein) }
        bind<PlotterChannel>() with singleton { PlotterChannel() }
        bind<InfoLabelChannel>() with singleton { InfoLabelChannel() }

        bind<InternalChannel>() with singleton { InternalChannel() }

    }
    // Kodein configurations >>>

    // <<< Properties declaration
    private val eventsChannel: GuiEventsChannel by instance()
    private val uiChannel: GuiUnitsChannel by instance()
    private val guiStateController: GuiStateController by instance()
    private val internalChannel: InternalChannel by instance()

    private val plotter: CCPlotter by instance()
    private val infoLabelChannel: InfoLabelChannel by instance()

    var controllerState: ControllerState = UNUSED
    private var callCounter = 0
    // Properties declaration >>>

    init {
        // Launch the TornadoFx application
        guiStateController.launchApp()

        // Listen to the internal Channel - currently only turns on/off the controller
        internalChannel.channel.reactToChannelOn(this) {
            controllerState = when (controllerState) {
                LIVE -> PAUSED
                PAUSED -> LIVE
                UNUSED -> UNUSED
            }
        }

        // launch a background job to get events from the UI.
        GlobalScope.launch {
            eventsConsumer()
        }


        // launch a timer event to report usage statistics
        GlobalScope.launch {
            while (true) {
                ccInfo("CCPS", callCounter.toString())
                callCounter = 0
                delay(1000)
            }
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

    private fun <T : CCUnit> registerUnitIfNew(unit: T, initCode: T.() -> Unit) {
        unit.run {
            when (state) {
                CCUnitState.NEW -> {
                    initCode()
                    sendGuiUnit(uiChannel)
                    state = CCUnitState.LIVE

                    when (this@CodeController.controllerState) {
                        UNUSED -> this@CodeController.controllerState = LIVE
                        else -> Unit
                    }
                }
                CCUnitState.LIVE -> {
                    callCounter++
                }
            }
        }
    }

    // ------ BOOL ------ //                                                // ------ BOOL ------ //                                                // ------ BOOL ------ //
    fun ccBool(id: String,fallBack: Boolean = true, initCode: CCBool.() -> Unit = {}): Boolean {
        if (controllerState is PAUSED) return fallBack

        val unit = (getUnit(CCType.BOOL, id) as CCBool)
        registerUnitIfNew(unit, initCode)
        return unit.value
    }

    fun ccToggleCode(id: String, on: Boolean = true, f: () -> Unit) {
        if (controllerState is PAUSED) return // If CC is paused the toggled code will not run.

        val curr = ccBool(id)
        when {
            curr && on -> f()
            curr && !on -> return
            !curr && on -> return
            !curr && !on -> f()
        }
    }


    // ------ DOUBLE ------ //                                                // ------ DOUBLE ------ //                                                // ------ DOUBLE ------ //
    fun ccDouble(id: String, fallBack: Double = 0.0, initCode: CCDouble.() -> Unit = {}): Double {
        if (controllerState is PAUSED) return fallBack

        val unit = (getUnit(CCType.DOUBLE, id) as CCDouble)
        registerUnitIfNew(unit, initCode)
        return unit.value
    }

    // ------ VECTOR ------ //                                                // ------ VECTOR ------ //                                                // ------ VECTOR ------ //
    fun ccVec(id: String, fallBack: Pair<Double,Double> = Pair(0.0,0.0), initCode: CCVec.() -> Unit = {}): Pair<Double, Double> {
        if (controllerState is PAUSED) return fallBack

        val unit = (getUnit(CCType.VEC2, id) as CCVec)
        registerUnitIfNew(unit, initCode)
        return unit.value
    }

    // ------ INFO ------ //                                                // ------ INFO ------ //                                                // ------ INFO ------ //
    fun ccInfo(id: String, info: String, howMany: Double = 1.0) {
        if (controllerState is PAUSED) return

        if (Random.nextDouble() > howMany) {
            return
        }
        infoLabelChannel.send(CCInfoDatum(id, info))
    }

    // ------ PLOT ------ //                                                // ------ PLOT ------ //                                                // ------ PLOT ------ //
    //   howMany - between 0.0..1.0, the higher the more dataPoints.
    fun ccPlot(id: String, x: Double, y: Double, howMany: Double = 1.0) {
        if (controllerState is PAUSED) return

        if (Random.nextDouble() > howMany) {
            return
        }

        val dataPoint = DataPoint(id, Pair(x, y))
        plotter.sendData(dataPoint)
    }


}


sealed class ControllerState
object UNUSED : ControllerState()
object LIVE : ControllerState()
object PAUSED : ControllerState()
