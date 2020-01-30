import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import yarden.mytools.codecontroller.domain.CodeController

public fun Double.mapTo(beforeMin: Double, beforeMax: Double, afterMin: Double, afterMax: Double): Double {
    val n = (this - beforeMin) / (beforeMax - beforeMin)
    return afterMin + n * (afterMax - afterMin)
}

class Queue (list:MutableList<Any>){

    var items:MutableList<Any> = list

    fun isEmpty():Boolean = items.isEmpty()

    fun size():Int = items.count()

    override  fun toString() = items.toString()

    fun enqueue(element:Any){
        items.add(element)
    }

    fun dequeue():Any?{
        if (this.isEmpty()){
            return null
        } else {
            return items.removeAt(0)
        }
    }

    fun peek():Any?{
        return items[0]
    }
}

public fun <T,R> Channel<T>.reactToChannelOn(receiver : R, op: R.(element: T) -> Unit) {
    val channel = this
    GlobalScope.launch {
        for (newElement in channel) {
            receiver.op(newElement)
        }
    }
}
