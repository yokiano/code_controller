package yarden.mytools.codecontroller.presentation.common.entities

import yarden.mytools.codecontroller.domain.CCType

interface CCGuiUnit {
    val id: String
    val value : Any
    val ccType : CCType
    val default : Any
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
    var range: ClosedFloatingPointRange<Double> = (0.0).rangeTo(default)
}

class CCGuiToggle(override val id: String,default: Boolean) : CCGuiUnit {
    override var default: Boolean = default
        set(v) {
            field = v
            value = v
        }
    override var value : Boolean = default
    override val ccType = CCType.BOOL

}