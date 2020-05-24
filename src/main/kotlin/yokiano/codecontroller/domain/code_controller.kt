package yokiano.codecontroller.domain

import GuiEventsChannel
import GuiStateController
import GuiUnitsChannel
import InfoLabelChannel
import InternalChannel
import PlotterChannel
import cleanDecimal
import yokiano.codecontroller.presentation.viewimpl.tornadofx.TornadoDriver
import yokiano.codecontroller.presentation.viewimpl.tornadofx.TornadoApp
import kotlinx.coroutines.*
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.*
import org.kodein.di.tornadofx.installTornadoSource
import reactToChannelOn
import yokiano.codecontroller.presentation.viewimpl.tornadofx.PlotLine
import yokiano.codecontroller.presentation.viewimpl.tornadofx.TInfoLabel
import kotlin.random.Random

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

        bind<CCPlotter>() with singleton {
            CCPlotter(
                kodein
            )
        }
        bind<PlotterChannel>() with singleton { PlotterChannel() }
        bind<InfoLabelChannel>() with singleton { InfoLabelChannel() }

        bind<InternalChannel>() with singleton { InternalChannel() }

    }
    // Kodein configurations >>>

    // <<< Properties declaration
    private val valueEventsChannel: GuiEventsChannel by instance<GuiEventsChannel>()
    private val uiChannel: GuiUnitsChannel by instance<GuiUnitsChannel>()
    private val internalChannel: InternalChannel by instance<InternalChannel>()
    private val infoLabelChannel: InfoLabelChannel by instance<InfoLabelChannel>()

    private val guiStateController: GuiStateController by instance<GuiStateController>()

    private val plotter: CCPlotter by instance<CCPlotter>()

    var controllerState: ControllerState = UNUSED
    private var callCounter = 0
    // Properties declaration >>>

    var initialOrientation: ScreenOrientation = HORIZONTAL

    init {
        // Launch the TornadoFx application
        guiStateController.launchApp(initialOrientation)

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
                ccInfo("CCPS", callCounter.toString(),tooltip = "Controller Calls Per Second")
                callCounter = 0
                delay(1000)
            }
        }
    }

    private suspend fun eventsConsumer() {
        // The for loop will consume each event once it arrives and will never finish.
        for (event in valueEventsChannel.channel) {
            event.run {
                // This instance() call will fetch the (only) unit with the specified ID from the defined multiton in kodein.
                val unit: CCUnit by instance(
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
                CCUnitState.LIVE, CCUnitState.DEAD -> {
                    callCounter++
                }
            }
        }
    }

    // ------ BOOL ------ //                                                // ------ BOOL ------ //                                                // ------ BOOL ------ //
    fun ccBool(id: String, default: Boolean = true, initCode: CCBool.() -> Unit = {}): Boolean {
        if (controllerState is PAUSED) return default
        (getUnit(CCType.BOOL, id) as CCBool).apply {
            if (state == CCUnitState.NEW) {
                this.default = default
            }
            registerUnitIfNew(this, initCode)
            return this.value
        }
    }

    // Will execute the code given by 'f()' according to 'invokeWhen'
    fun ccToggleCode(id: String, invokeWhen: Boolean = true, f: () -> Unit = {}) {
        if (controllerState is PAUSED) return // If CC is paused the toggled code will not run.

        val curr = ccBool(id)
        when {
            curr && invokeWhen -> f()
            curr && !invokeWhen -> return
            !curr && invokeWhen -> return
            !curr && !invokeWhen -> f()
        }
    }

    // ------ DOUBLE ------ //                                                // ------ DOUBLE ------ //                                                // ------ DOUBLE ------ //
    fun ccDouble(id: String, default: Double = 0.0, range : ClosedFloatingPointRange<Double> = 0.0..1.0, initCode: CCDouble.() -> Unit = {}): Double {
        if (controllerState is PAUSED) return default

        (getUnit(CCType.DOUBLE, id) as CCDouble).apply {
            if (state == CCUnitState.NEW) {
                this.range = range
                this.default = default
            }
            registerUnitIfNew(this, initCode)
            return this.value
        }
    }

    // ------ VECTOR ------ //                                                // ------ VECTOR ------ //                                                // ------ VECTOR ------ //
    fun ccVec2(
        id: String,
        default: Pair<Double, Double> = Pair(0.0, 0.0),
        range : Pair<Pair<Double,Double>,Pair<Double,Double>> = ((0.0 to 0.0) to (1.0 to 1.0)),
        initCode: CCVec.() -> Unit = {}
    ): Pair<Double, Double> {
        if (controllerState is PAUSED) return default

        (getUnit(CCType.VEC2, id) as CCVec).apply {
            if (state == CCUnitState.NEW) {
                this.range = range
                this.default = default
            }
            registerUnitIfNew(this, initCode)
            return this.value
        }
    }

    // ------ INFO ------ //                                                // ------ INFO ------ //                                                // ------ INFO ------ //
    fun ccInfo(id: String, info: String, reduceCalls: Double = 1.0,tooltip: String = "") {
        if (controllerState is PAUSED) return

        if (Random.nextDouble() > reduceCalls) {
            return
        }
        infoLabelChannel.send(CCInfoDatum(id, info.cleanDecimal(),tooltip))
    }

    // ------ PLOT ------ //                                                // ------ PLOT ------ //                                                // ------ PLOT ------ //
    //   howOften - between 0.0..1.0, the higher the more dataPoints.
    // howMany - maximum number of data points in the plot
    fun ccPlot(id: String, x: Double, y: Double, reduceCalls: Double = 1.0, howMany: Int = Int.MAX_VALUE) {
        if (controllerState is PAUSED) return

        if (Random.nextDouble() > reduceCalls) {
            return
        }

        val dataPoint = DataPoint(id, Pair(x, y), howMany)
        plotter.sendData(dataPoint)
    }


}


sealed class ControllerState
object UNUSED : ControllerState()
object LIVE : ControllerState()
object PAUSED : ControllerState()

sealed class ScreenOrientation
object HORIZONTAL : ScreenOrientation()
object VERTICAL : ScreenOrientation()
object FULL_SCREEN : ScreenOrientation()