package yokiano.codecontroller.presentation.viewimpl.tornadofx.controls

import XYControl
import javafx.beans.property.SimpleDoubleProperty

import yokiano.codecontroller.presentation.viewimpl.tornadofx.*
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.input.MouseButton
import org.kodein.di.generic.instance
import org.kodein.di.tornadofx.kodein
import tornadofx.*
import yokiano.codecontroller.presentation.viewimpl.tornadofx.panes.ResponsivePane

class XYControlView(private val tUnit: TXYControl, val parentPane: ResponsivePane) : ControlView(tUnit) {

    val driver: TornadoDriver by kodein().instance<TornadoDriver>()

    val xyControlObject = XYControl(this,tUnit.range,tUnit.valueProperty)
    override val control = xyControlObject.root


/*
    override fun bindScale(property: SimpleDoubleProperty) {
        property.onChange {
            root.scale(driver.globalParams.controlScalingAnimationDuration, Point2D(it))
//            control.scaleX = it
        }
    }
*/
    override val root = vbox {
        alignment = Pos.CENTER
        paddingAll = 10.0

        disablePaneResizeOnDrag()

        hbox {
            alignment = Pos.CENTER

            vbox {
                alignment = Pos.CENTER
                label(tUnit.id)
                label("${xyControlObject.valueLabel}") {
                    textProperty().bindBidirectional(xyControlObject.valueLabel)
                }
            }
        }

        control.attachTo(this)
    }

    // This logic prevents from the parent pane to resize when changing the XYControl value.
    private fun Node.disablePaneResizeOnDrag() {
        setOnMousePressed {
            parentPane.isFastResizeEnabled = false
        }
        setOnMouseReleased {
            parentPane.isFastResizeEnabled = true
        }
    }
}

