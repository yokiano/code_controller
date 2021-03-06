package yokiano.codecontroller.presentation.viewimpl.tornadofx

import XYPoint
import yokiano.codecontroller.presentation.common.CCGuiSlider
import yokiano.codecontroller.presentation.common.CCGuiToggle
import yokiano.codecontroller.presentation.common.CCGuiUnit
import yokiano.codecontroller.presentation.common.CCGuiXYControl

class UnitAdapter {

    companion object {
        fun toTornadoUnit(ccUnit : CCGuiUnit) : TUnit<*> {
            return when (ccUnit) {
                is CCGuiToggle -> {
                    TToggle(ccUnit.id, ccUnit.default)
                }
                is CCGuiSlider -> {
                    TSlider(ccUnit.id, ccUnit.default, ccUnit.range)
                }
                is CCGuiXYControl -> {
                    val range = Pair(XYPoint(ccUnit.range.first.first,ccUnit.range.first.second),XYPoint(ccUnit.range.second.first,ccUnit.range.second.second))
                    TXYControl(ccUnit.id, range,XYPoint(ccUnit.default.first,ccUnit.default.second))
                }
                else -> {
                    TToggle("Error with unit conversion. The type of the controller is unknown.")
                }
            }
        }

        fun toCCUnit( tUnit : TUnit<*>) : CCGuiUnit {
            return when (tUnit) {
                is TToggle -> {
                    CCGuiToggle(
                        tUnit.id,
                        tUnit.initialValue
                    ).apply {
                        value = tUnit.valueProperty.value
                        state = tUnit.stateProperty.value
                    }
                }
                is TSlider -> {
                    CCGuiSlider(tUnit.id).apply {
                        value = tUnit.valueProperty.value
                        state = tUnit.stateProperty.value
                    }
                }
                is TXYControl -> {
                    CCGuiXYControl(tUnit.id).apply {
                        tUnit.valueProperty.value.run {
                            value = Pair(x,y)
                        }
                        state = tUnit.stateProperty.value
                    }
                }
                else -> CCGuiToggle(
                    "Error with unit conversion. The type of the controller is unknown",
                    false
                )
            }
        }
    }
}