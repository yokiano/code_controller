package yokiano.codecontroller.domain

import GuiUnitsChannel
import yokiano.codecontroller.presentation.common.CCGuiSlider
import yokiano.codecontroller.presentation.common.CCGuiToggle
import yokiano.codecontroller.presentation.common.CCGuiUnit
import yokiano.codecontroller.presentation.common.CCGuiXYControl


enum class CCType {
    DOUBLE,
    BOOL,
    VEC2
}

enum class CCUnitState {
    NEW,
    LIVE,
    DEAD
}

interface CCUnit {
    val id: String
    val type: CCType

    val value: Any
    val default: Any

    var state: CCUnitState

    fun getGuiUnit(): CCGuiUnit
    fun updateValue(newVal: Any)

    fun sendGuiUnit(channel: GuiUnitsChannel) {
        val guiUnit = getGuiUnit()
        channel.send(guiUnit)
    }
}


class CCDouble(override val id: String) : CCUnit {
    var range: ClosedFloatingPointRange<Double> = 0.0..1.0

    override var default: Double = 0.0
        set(v) {
            field = v
            value = v
        }
    override var value: Double = default

    override val type = CCType.DOUBLE

    override var state = CCUnitState.NEW

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
        return CCGuiToggle(id, default).also {
            it.default = default
        }
    }

    override fun updateValue(newVal: Any) {
        value = !value
    }
}


@Suppress("UNCHECKED_CAST")
class CCVec(override val id: String) : CCUnit {
    var range = Pair(Pair(0.0, 0.0), Pair(1.0, 1.0))
        set(v) {
            field = v
            default = Pair(v.first.first, v.first.second)
        }
    override var default = Pair(range.first.first, range.first.second)
        set(v) {
            field = v
            value = v
        }

    override var value = default
    override val type = CCType.VEC2
    override var state = CCUnitState.NEW

    fun setRange(topLeftX: Double, topLeftY: Double, bottomRightX: Double, bottomRightY: Double) {
        range = Pair(Pair(topLeftX, topLeftY), Pair(bottomRightX, bottomRightY))
    }

    override fun getGuiUnit(): CCGuiUnit {
        val xyControl = CCGuiXYControl(id).also {
            it.default = this.default
            it.range = this.range

        }
        return xyControl
    }

    override fun updateValue(newVal: Any) {
        if (newVal is Pair<*, *> && newVal.first is Double && newVal.second is Double) {
            this.value = newVal as Pair<Double, Double>
        }
    }
}

data class CCInfoDatum(val id: String, val info : String,val tooltip: String)

