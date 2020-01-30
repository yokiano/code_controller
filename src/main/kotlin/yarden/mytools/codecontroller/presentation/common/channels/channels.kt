import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.sendBlocking
import yarden.mytools.codecontroller.domain.CCInfoDatum
import yarden.mytools.codecontroller.domain.entities.DataPoint
import yarden.mytools.codecontroller.presentation.common.entities.CCGuiUnit

class GuiUnitsChannel {
    val channel = Channel<CCGuiUnit>()


    fun send(guiUnit: CCGuiUnit) {
        channel.sendBlocking(guiUnit)
    }
}

class PlotterChannel {
    val channel = Channel<DataPoint>()

    fun send(dataPoint: DataPoint) {
        channel.sendBlocking(dataPoint)
    }
}

class InfoLabelChannel {
    val channel = Channel<CCInfoDatum>()

    fun send(datum: CCInfoDatum) {
        channel.sendBlocking(datum)
    }
}

// Currently used only for the global on/off toggle button.
class InternalChannel {
    val channel = Channel<Boolean>()

    fun send(newVal: Boolean) {
        channel.sendBlocking(newVal)
    }
}