package yokiano.codecontroller.domain.entities

import PlotterChannel
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance

class CCPlotter(override val kodein: Kodein) : KodeinAware{

    private val dataChannel : PlotterChannel by instance()

    fun sendData(dataPoint: DataPoint) {
        dataChannel.send(dataPoint)
    }

}

data class DataPoint(val id :String,val data : Pair<Double,Double>, val limit: Int)