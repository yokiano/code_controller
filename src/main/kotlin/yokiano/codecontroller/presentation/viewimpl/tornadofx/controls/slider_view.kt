@file:Suppress("UNCHECKED_CAST")

package yokiano.codecontroller.presentation.viewimpl.tornadofx.controls

import javafx.beans.Observable
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.value.ObservableValue
import javafx.geometry.Pos
import javafx.scene.control.Control
import javafx.scene.control.Tooltip
import javafx.scene.layout.Priority
import org.kodein.di.generic.instance
import org.kodein.di.tornadofx.kodein
import tornadofx.*
import tornadofx.Stylesheet.Companion.thumb
import tornadofx.controlsfx.statusbar
import yokiano.codecontroller.presentation.viewimpl.tornadofx.MyStyle
import yokiano.codecontroller.presentation.viewimpl.tornadofx.TSlider
import yokiano.codecontroller.presentation.viewimpl.tornadofx.TornadoDriver

class SliderView(val tUnit: TSlider) : ControlView(tUnit) {

    val driver: TornadoDriver by kodein().instance<TornadoDriver>()

    override val control = slider(tUnit.range.start, tUnit.range.endInclusive, tUnit.initialValue) {
        addClass(MyStyle.sliderStyle)
        hgrow = Priority.ALWAYS

        bind(tUnit.valueProperty as ObservableValue<Number>)

        tooltip =
            Tooltip("${tUnit.range.toString()}  \n *Right-Click to for menu")
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

// Archived for documentation
/*
    override fun bindScale(property: SimpleDoubleProperty) {
        val duration = driver.globalParams.controlScalingAnimationDuration
        property.onChange {
            val newScale = it
*/
/*            val track = control.getChildList()?.getOrNull(0)
            track?.apply {
//                track.scale(duration, Point2D(it))
            }


            val thumb = control.getChildList()?.getOrNull(1)
            thumb?.apply {
//                thumb.scale(duration, Point2D(it))
            }*//*


*/
/*
            control.scale(duration, Point2D(it))
            val labels = root.lookupAll(".label")
            labels.forEach {
                it.style(true) {
                    fontSize = Dimension(10 * newScale, Dimension.LinearUnits.px)
                }
            }
*//*


//            need to put a common code in the responsive pane that will refer to textScale and will change all labels and text fields.
            root.scale(duration, Point2D(it))
        }

*/
/*1
        (control as Control).prefWidthProperty().apply {
            val dBinding = doubleBinding(this,property) { property.value * 150.0}
            this.bind(dBinding)
        }
*//*

    }
*/