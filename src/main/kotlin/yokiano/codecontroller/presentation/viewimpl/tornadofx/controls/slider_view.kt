@file:Suppress("UNCHECKED_CAST")

package yokiano.codecontroller.presentation.viewimpl.tornadofx.controls

import javafx.beans.value.ObservableValue
import javafx.geometry.Pos
import javafx.scene.control.Tooltip
import javafx.scene.layout.Priority
import org.kodein.di.generic.instance
import org.kodein.di.tornadofx.kodein
import tornadofx.*
import yokiano.codecontroller.presentation.viewimpl.tornadofx.MyStyle
import yokiano.codecontroller.presentation.viewimpl.tornadofx.TSlider
import yokiano.codecontroller.presentation.viewimpl.tornadofx.TornadoDriver

class SliderView(val tUnit: TSlider) : ControlView(tUnit) {

    val driver: TornadoDriver by kodein().instance()

    override val control = slider(tUnit.range.start, tUnit.range.endInclusive, tUnit.initialValue) {
        addClass(MyStyle.sliderStyle)
        hgrow = Priority.ALWAYS

        bind(tUnit.valueProperty as ObservableValue<Number>)

        onHover {
            tooltip =
                Tooltip("${tUnit.range.toString()}  \n *Right-Click to for menu")
        }
    }

    override val root = vbox {
        hgrow = Priority.ALWAYS
        alignment = Pos.CENTER
        val unitValueP = tUnit.valueProperty as ObservableValue<Number>
        label(tUnit.id) {
            addClass(MyStyle.sliderLabel)
            paddingBottom = 4.0
            hgrow = Priority.ALWAYS
        }

        control.attachTo(this)

        textfield(unitValueP) {
            addClass(MyStyle.sliderTextField)
            hgrow = Priority.ALWAYS
            autosize()
            onHover {
                tooltip = Tooltip(tUnit.valueProperty.value.toString())
            }
        }
    }
}

