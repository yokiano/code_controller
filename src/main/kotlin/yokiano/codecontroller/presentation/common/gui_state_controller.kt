import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance
import yokiano.codecontroller.domain.ScreenOrientation
import yokiano.codecontroller.presentation.common.CCGuiUnit
import yokiano.codecontroller.presentation.viewimpl.tornadofx.TornadoDriver

class GuiStateController(override val kodein: Kodein) : KodeinAware {

    private val unitsChannel: GuiUnitsChannel by instance<GuiUnitsChannel>()
    private val eventsChannel: GuiEventsChannel by instance<GuiEventsChannel>()

    private val guiUnits: ArrayList<CCGuiUnit> = ArrayList()

    private val presentationDriver: TornadoDriver by instance<TornadoDriver>()

    private val plotterChannel: PlotterChannel by instance<PlotterChannel>()
    private val infoLabelChannel: InfoLabelChannel by instance<InfoLabelChannel>()

    init {
        // Define the action for each of the data channels
        unitsChannel.channel.reactToChannelOn(this) { unit -> addNewUnit(unit) }
        plotterChannel.channel.reactToChannelOn(this) { data ->
            presentationDriver.addDataPointTo(data.id, data.data, data.limit)
        }
        infoLabelChannel.channel.reactToChannelOn(this) { data ->
            presentationDriver.updateInfoLabel(data.id,data.info,data.tooltip)
        }
    }

//    private fun sendEvent(guiUnit: CCGuiUnit) {
//        // TODO - instead of sending modified classes, send a "command" to the yokiano.codecontroller.domain layer (code_controller.kt) to make an operation on the local repository
//        eventsChannel.send(guiUnit)
//    }

    fun launchApp(initialOrientation: ScreenOrientation) {
        presentationDriver.launchApp(initialOrientation)
    }

    private fun addNewUnit(unit: CCGuiUnit) {
        guiUnits.add(unit)
        presentationDriver.addUnit(unit)
    }

    /*public fun <T> reactToChannelWith(channel: Channel<T>, op: GuiStateController.(element: T) -> Unit) {
        GlobalScope.launch {
            for (newElement in channel) {
                op(newElement)
            }
        }
    }*/


}


