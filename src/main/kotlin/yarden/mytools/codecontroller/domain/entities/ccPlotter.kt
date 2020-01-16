package yarden.mytools.codecontroller.domain.entities

import PlotterChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.sendBlocking
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance

class CCPlotter(override val kodein: Kodein) : KodeinAware{

    private val dataChannel : PlotterChannel by instance()

    fun sendData(data : Pair<Double,Double>) {
        dataChannel.send(data)
    }

}