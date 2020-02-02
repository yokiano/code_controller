import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance
import yarden.mytools.codecontroller.presentation.common.entities.CCGuiUnit
import yarden.mytools.codecontroller.presentation.implementations.tornadofx.TornadoDriver

class GuiStateController(override val kodein: Kodein) : KodeinAware {

    private val unitsChannel: GuiUnitsChannel by instance()
    private val eventsChannel: GuiEventsChannel by instance()

    private val guiUnits: ArrayList<CCGuiUnit> = ArrayList()

    private val presentationDriver: TornadoDriver by instance()

    private val plotterChannel: PlotterChannel by instance()
    private val infoLabelChannel: InfoLabelChannel by instance()

    init {
        unitsChannel.channel.reactToChannelOn(this) { unit -> addNewUnit(unit) }
        plotterChannel.channel.reactToChannelOn(this) { data ->
            presentationDriver.addDataPointTo(data.id, data.data)
        }
        infoLabelChannel.channel.reactToChannelOn(this) { data ->
            presentationDriver.updateInfoLabel(data.id,data.info)
        }

    }

//    private fun sendEvent(guiUnit: CCGuiUnit) {
//        // TODO - instead of sending modified classes, send a "command" to the yarden.mytools.codecontroller.domain layer (code_controller.kt) to make an operation on the local repository
//        eventsChannel.send(guiUnit)
//    }

    fun launchApp() {
        presentationDriver.launchApp()
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


