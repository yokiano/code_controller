@file:Suppress("UNCHECKED_CAST")

package yokiano.codecontroller.presentation.viewimpl.tornadofx.controls

import cleanDecimal
import javafx.beans.Observable
import javafx.beans.property.Property
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.value.ObservableValue
import javafx.geometry.Pos
import javafx.scene.control.Control
import javafx.scene.control.TextField
import javafx.scene.control.Tooltip
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.Region
import javafx.util.StringConverter
import javafx.util.converter.NumberStringConverter
import org.kodein.di.generic.instance
import org.kodein.di.tornadofx.kodein
import tornadofx.*
import tornadofx.Stylesheet.Companion.thumb
import tornadofx.controlsfx.statusbar
import yokiano.codecontroller.presentation.viewimpl.tornadofx.MyStyle
import yokiano.codecontroller.presentation.viewimpl.tornadofx.TSlider
import yokiano.codecontroller.presentation.viewimpl.tornadofx.TornadoDriver
import java.text.DecimalFormat

class SliderView(val tUnit: TSlider) : ControlView(tUnit) {

    val driver: TornadoDriver by kodein().instance<TornadoDriver>()
    lateinit var textField: TextField

    val unitValueP = tUnit.valueProperty as ObservableValue<Number>

    override val control = slider(tUnit.range.start, tUnit.range.endInclusive, tUnit.initialValue) {
        addClass(MyStyle.sliderStyle)
        bind(unitValueP)

        tooltip =
            Tooltip("${tUnit.range.toString()}  \n Right-Click to for control menu")
    }

    override val root = vbox {
        paddingBottom = 8.0
        paddingRight = 5.0
        hbox {
            paddingBottom = 2.0
            alignment = Pos.CENTER
            prefWidthProperty().bindBidirectional(control.prefWidthProperty())
            label(tUnit.id) {
                paddingLeft = 10.0
                alignment = Pos.CENTER_LEFT
                runLater {
                    prefWidth = (parent as HBox).width * 0.5
                }
            }
            textField = textfield() {
                addClass(MyStyle.sliderTextField)
                runLater {
                    prefWidth = (parent as HBox).width * 0.5
                }
                runLater {
                    text = control.value.toString()
                }
                bind(unitValueP as ObservableValue<Double>,false,MyStringConverter(tUnit.initialValue))

                tooltip(tUnit.valueProperty.value.toString())
            }
        }
        control.attachTo(this)
    }
}

class MyStringConverter(private val defaultValue: Double) : StringConverter<Double>() {
    override fun toString(value: Double?): String {
        return value?.toString()?.cleanDecimal() ?: defaultValue.toString().cleanDecimal()
    }

    override fun fromString(string: String?): Double {
        return string?.cleanDecimal()?.toBigDecimalOrNull()?.toDouble() ?: defaultValue
    }
}
