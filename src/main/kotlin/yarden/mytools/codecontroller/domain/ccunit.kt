package yarden.mytools.codecontroller.domain

import GuiUnitsChannel
import yarden.mytools.codecontroller.presentation.common.entities.*


enum class CCType {
    DOUBLE,
    BOOL,
    VEC2
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
        val toggle = CCGuiToggle(id, default).also {
            it.default = this.default
        }
        return toggle
    }

    override fun updateValue(newVal: Any) {
        value = !value
    }

}


class CCVec(override val id: String) : CCUnit {
    override var default = Pair(0.0, 0.0)
        set(v) {
            field = v
            value = v
        }

    override var value = default
    override val type = CCType.VEC2
    override var state = CCUnitState.NEW

    var range = Pair(Pair(0.0, 0.0), Pair(1.0, 1.0))
    fun setRange(leftTopX: Double, leftTopY: Double, rightBottomX: Double, rightBottomY: Double) {
        range = Pair(Pair(leftTopX, leftTopY), Pair(rightBottomX, rightBottomY))
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
        } else {
        }
    }
}

data class CCInfoDatum(val id: String, val info : String)

