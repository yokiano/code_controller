import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.sendBlocking
import yarden.mytools.codecontroller.presentation.common.entities.CCGuiUnit

class GuiUnitsChannel {
    val channel = Channel<CCGuiUnit>()


    fun send(guiUnit : CCGuiUnit) {
        channel.sendBlocking(guiUnit)
    }
}

class PlotterChannel {
    val channel = Channel<Pair<Double,Double>>()

    fun send(data : Pair<Double,Double>) {
        channel.sendBlocking(data)
    }
}