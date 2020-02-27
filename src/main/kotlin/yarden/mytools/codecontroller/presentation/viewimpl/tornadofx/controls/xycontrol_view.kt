package yarden.mytools.codecontroller.presentation.viewimpl.tornadofx.controls

import XYControl

import yarden.mytools.codecontroller.presentation.viewimpl.tornadofx.*
import javafx.beans.value.ObservableValue
import javafx.geometry.Pos
import javafx.scene.control.ToggleButton
import javafx.scene.control.Tooltip
import javafx.scene.layout.Priority
import org.kodein.di.generic.instance
import org.kodein.di.tornadofx.kodein
import tornadofx.*

class XYControlView(private val tUnit: TXYControl) : ControlView(tUnit) {

    val driver: TornadoDriver by kodein().instance()

    val xyControlObject = XYControl(tUnit.id,tUnit.range,tUnit.valueProperty)
    override val control = xyControlObject.root

    override val root = vbox {
        alignment = Pos.CENTER

        // Configuration Buttons
        hbox {
            alignment = Pos.CENTER

            vbox {
                alignment = Pos.CENTER
                label(tUnit.id)
                label("${xyControlObject.valueLabel}") {
                    textProperty().bindBidirectional(xyControlObject.valueLabel)
                    paddingBottom = -14.0
                }
            }
        }

        control.attachTo(this)
    }


}

