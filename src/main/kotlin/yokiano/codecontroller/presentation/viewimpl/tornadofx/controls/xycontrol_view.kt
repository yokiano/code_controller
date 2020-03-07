package yokiano.codecontroller.presentation.viewimpl.tornadofx.controls

import XYControl

import yokiano.codecontroller.presentation.viewimpl.tornadofx.*
import javafx.beans.value.ObservableValue
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.ToggleButton
import javafx.scene.control.Tooltip
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import org.kodein.di.generic.instance
import org.kodein.di.tornadofx.kodein
import tornadofx.*
import yokiano.codecontroller.presentation.viewimpl.tornadofx.panes.ResponsivePane

class XYControlView(private val tUnit: TXYControl, val parentPane: ResponsivePane) : ControlView(tUnit) {

    val driver: TornadoDriver by kodein().instance()

    val xyControlObject = XYControl(tUnit.id,tUnit.range,tUnit.valueProperty)
    override val control = xyControlObject.root

    override val root = vbox {
        alignment = Pos.CENTER

        disablePaneResizeOnDrag()

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

    // This logic prevents from the parent pane to resize when changing the XYControl value.
    private fun Node.disablePaneResizeOnDrag() {
        setOnMousePressed {
            parentPane.disableFastResize = true
        }
        setOnMouseReleased {
            parentPane.disableFastResize = false
        }
    }
}

