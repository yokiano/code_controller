package yarden.mytools.codecontroller.presentation.implementations.tornadofx

import yarden.mytools.codecontroller.presentation.common.entities.CCGuiSlider
import yarden.mytools.codecontroller.presentation.common.entities.CCGuiToggle
import yarden.mytools.codecontroller.presentation.common.entities.CCGuiUnit

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
                else -> CCGuiToggle("Error with unit conversion",false)
            }
        }
    }
}