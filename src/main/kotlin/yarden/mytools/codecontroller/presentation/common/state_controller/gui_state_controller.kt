import kotlinx.coroutines.*
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance
import tornadofx.*
import yarden.mytools.codecontroller.presentation.common.entities.CCGuiUnit
import yarden.mytools.codecontroller.presentation.implementations.tornadofx.TornadoDriver

class GuiStateController(override val kodein: Kodein)  : KodeinAware {

    private val unitsChannel : GuiUnitsChannel by instance()
    private val eventsChannel : GuiEventsChannel by instance()

    private val guiUnits: ArrayList<CCGuiUnit> = ArrayList()

    private val presentationDriver : TornadoDriver by instance()

//    var state : GuiState = GuiState.Empty

    init {
        listenToNewUnits()
    }

    private fun sendEvent(guiUnit: CCGuiUnit) {
        // TODO - instead of sending modified classes, send a "command" to the yarden.mytools.codecontroller.domain layer (code_controller.kt) to make an operation on the local repository
        eventsChannel.send(guiUnit)
    }

    fun launchApp() {
        presentationDriver.launchApp()
    }

    private fun addNewUnit(unit : CCGuiUnit) {
        guiUnits.add(unit)
        presentationDriver.addUnit(unit)
    }

    private fun listenToNewUnits() {
        GlobalScope.launch {
            for (newElement in unitsChannel.channel) {
                addNewUnit(newElement)
            }
        }
    }

}


