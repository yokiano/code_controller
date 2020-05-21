package yokiano.codecontroller.presentation.viewimpl.tornadofx.controls

import javafx.beans.property.SimpleDoubleProperty
import yokiano.codecontroller.presentation.viewimpl.tornadofx.*
import javafx.beans.value.ObservableValue
import javafx.geometry.Pos
import javafx.scene.control.Control
import javafx.scene.control.ToggleButton
import javafx.scene.control.Tooltip
import javafx.scene.layout.Priority
import javafx.stage.StageStyle
import org.kodein.di.generic.instance
import org.kodein.di.tornadofx.kodein
import tornadofx.*

class ToggleView(private val tUnit: TToggle) : ControlView(tUnit) {

    val driver: TornadoDriver by kodein().instance<TornadoDriver>()

    override val control = ToggleButton().apply {
        addClass(MyStyle.ccToggleButton)
        isSelected = tUnit.initialValue
        updateToggleStyle(tUnit.valueProperty.value)

        selectedProperty().onChange {
            tUnit.valueProperty.value = !tUnit.valueProperty.value
            updateToggleStyle(tUnit.valueProperty.value)
        }
    }

    val initialControlWidth by lazy { control.width }
    val initialControlHeight by lazy { control.height }


/*
    override fun bindScale(property: SimpleDoubleProperty) {
        val animationDuration = driver.globalParams.controlScalingAnimationDuration
        control.apply {
            minWidth  = 0.0
            maxWidth = Double.POSITIVE_INFINITY
            minHeight = 0.0
            maxHeight = Double.POSITIVE_INFINITY
        }

        property.onChange {
            root.scale(animationDuration, Point2D(it))
            control.prefWidthProperty().animate(property.value * initialControlWidth,animationDuration)
            control.prefHeightProperty().animate(property.value * initialControlHeight,animationDuration)
        }
    }
*/

    override val root = vbox {
        alignment = Pos.CENTER
        label(unit.id) {
            addClass(MyStyle.toggleLabel)
        }
        control.attachTo(this)
    }


}

