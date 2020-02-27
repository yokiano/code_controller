package yarden.mytools.codecontroller.presentation.viewimpl.tornadofx.controls

import yarden.mytools.codecontroller.presentation.viewimpl.tornadofx.*
import javafx.beans.value.ObservableValue
import javafx.geometry.Pos
import javafx.scene.control.ToggleButton
import javafx.scene.control.Tooltip
import javafx.scene.layout.Priority
import org.kodein.di.generic.instance
import org.kodein.di.tornadofx.kodein
import tornadofx.*

class ToggleView(private val tUnit: TToggle) : ControlView(tUnit) {

    val driver: TornadoDriver by kodein().instance()

    override val control = ToggleButton().apply {
        addClass(MyStyle.toggleButton)
        isSelected = tUnit.initialValue
        updateToggleStyle(tUnit.valueProperty.value)

        selectedProperty().onChange {
            tUnit.valueProperty.value = !tUnit.valueProperty.value
            updateToggleStyle(tUnit.valueProperty.value)
        }

    }

    override val root = vbox {
        alignment = Pos.CENTER
        label(unit.id) {
            addClass(MyStyle.toggleLabel)
        }
        control.attachTo(this)
    }


}

