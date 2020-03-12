import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.sendBlocking
import yokiano.codecontroller.presentation.common.entities.CCGuiUnit

class GuiEventsChannel {
    val channel : Channel<CCGuiUnit> = Channel<CCGuiUnit>()

    fun send(guiUnit : CCGuiUnit) {
        channel.sendBlocking(guiUnit)
    }
}