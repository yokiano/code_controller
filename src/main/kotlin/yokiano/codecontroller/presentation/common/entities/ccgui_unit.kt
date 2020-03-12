package yokiano.codecontroller.presentation.common.entities

import cleanDecimal
import yokiano.codecontroller.domain.CCType
import yokiano.codecontroller.domain.CCUnitState

interface CCGuiUnit {
    val id: String
    val value : Any
    val ccType : CCType
    val default : Any
    val state : CCUnitState

    fun sourceToValueReplacement() : String
}

// TODO - add spinner control (similar to JavaFX's spinner control. good for integers mainly.

data class CCGuiSlider(override val id: String) : CCGuiUnit {
    override var default: Double = 1.0 // Will be also the initial value
    set(v) {
        field = v
        value = v
    }

    override var value = default
    override val ccType = CCType.DOUBLE
    override var state = CCUnitState.LIVE
    var range: ClosedFloatingPointRange<Double> = (0.0).rangeTo(default)

    override fun sourceToValueReplacement() : String {
        return "$value".cleanDecimal()
    }
}

class CCGuiToggle(override val id: String,default: Boolean) : CCGuiUnit {
    override var default: Boolean = default
        set(v) {
            field = v
            value = v
        }
    override var value : Boolean = default
    override val ccType = CCType.BOOL
    override var state = CCUnitState.LIVE

    override fun sourceToValueReplacement(): String {
        return "$value"
    }
}

class CCGuiXYControl(override val id: String) : CCGuiUnit {
    override var default = Pair(0.0,0.0)
        set(v) {
            field = v
            value = v
        }
    override var value = default
    override val ccType = CCType.VEC2
    override var state = CCUnitState.LIVE

    var range =  Pair(Pair(0.0,0.0),Pair(1.0,1.0))

    override fun sourceToValueReplacement(): String {
        return "Pair(${value.first},${value.second})"
    }

}