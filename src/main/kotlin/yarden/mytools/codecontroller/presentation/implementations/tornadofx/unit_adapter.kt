package yarden.mytools.codecontroller.presentation.implementations.tornadofx

import XYPoint
import yarden.mytools.codecontroller.presentation.common.entities.CCGuiSlider
import yarden.mytools.codecontroller.presentation.common.entities.CCGuiToggle
import yarden.mytools.codecontroller.presentation.common.entities.CCGuiUnit
import yarden.mytools.codecontroller.presentation.common.entities.CCGuiXYControl

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
                    TToggle("Error with unit conversion")
                }
            }
        }

        fun toCCUnit( tUnit : TUnit<*>) : CCGuiUnit {
            return when (tUnit) {
                is TToggle -> {
                    CCGuiToggle(tUnit.id,tUnit.initialValue).apply {
                        value = tUnit.valueProperty.value
                    }
                }
                is TSlider -> {
                    CCGuiSlider(tUnit.id).apply {
                        value = tUnit.valueProperty.value
                    }
                }
                is TXYControl -> {
                    CCGuiXYControl(tUnit.id).apply {
                        tUnit.valueProperty.value.run {
                            value = Pair(x,y)
                        }
                    }
                }
                else -> CCGuiToggle("Error with unit conversion",false)
            }
        }
    }
}