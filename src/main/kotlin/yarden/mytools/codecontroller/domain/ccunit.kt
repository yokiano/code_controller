package yarden.mytools.codecontroller.domain

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.sendBlocking
import yarden.mytools.codecontroller.presentation.common.entities.CCGuiSlider
import yarden.mytools.codecontroller.presentation.common.entities.CCGuiToggle
import yarden.mytools.codecontroller.presentation.common.entities.CCGuiUnit


enum class CCType {
    DOUBLE,
    BOOL
}

enum class CCUnitState {
    NEW,
    LIVE
}

interface CCUnit {
    val id: String
    val type: CCType

    val value: Any
    val default: Any

    var state : CCUnitState

    fun getGuiUnit(): CCGuiUnit
    fun updateValue(newVal: Any)

    fun sendGuiUnit(channel: Channel<CCGuiUnit>) {
        val guiUnit = getGuiUnit()
        channel.sendBlocking(guiUnit)
    }
}



class CCDouble(override val id: String) : CCUnit {
    override var default: Double = 100.0
        set(v) {
            field = v
            value = v
        }
    override var value: Double = default

    override val type = CCType.DOUBLE

    override var state = CCUnitState.NEW

    var range: ClosedFloatingPointRange<Double> = 0.1..200.0

    override fun getGuiUnit(): CCGuiUnit {
        return CCGuiSlider(id).also {
            it.range = range
            it.default = default
        }
    }

    override fun updateValue(newVal: Any) {
        if (newVal is Double) {
            value = newVal
        }
    }

}

class CCBool(override val id: String) : CCUnit {
    override var default: Boolean = true
        set(v) {
            field = v
            value = v
        }

    override var value: Boolean = default
    override val type = CCType.BOOL


    override var state = CCUnitState.NEW

    override fun getGuiUnit(): CCGuiUnit {
        val toggle =  CCGuiToggle(id,default).also {
            it.default = this.default
        }
        return toggle
    }

    override fun updateValue(newVal: Any) {
        value = !value
    }

}
