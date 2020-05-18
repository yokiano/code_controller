package yokiano.codecontroller.presentation.viewimpl.tornadofx.panes

import javafx.geometry.Orientation
import javafx.scene.paint.Color
import org.kodein.di.generic.instance
import org.kodein.di.tornadofx.kodein
import tornadofx.*
import yokiano.codecontroller.presentation.viewimpl.tornadofx.*

object MenuPane : View() {

    val driver: TornadoDriver by kodein().instance<TornadoDriver>()

    // --- MAIN MENU
    override val root = flowpane {

        adjustButtonOrientation()

        style {
            paddingTop = 8
            paddingLeft = 3
            backgroundColor += Color(0.0, 0.0, 0.0, 0.3)
        }

        // Power On/Off button.
        togglebutton("") {
            tooltip("Enable / Disable Controllers")
            addClass(MyStyle.ccToggleButton)
            isSelected = true
            updateToggleStyle(isSelected, MyStyle.powerButton_off, MyStyle.powerButton_on)

            selectedProperty().onChange {
                driver.internalChannel.send(it)
                updateToggleStyle(isSelected, MyStyle.powerButton_off, MyStyle.powerButton_on)
            }
        }

        // Reloading Views - currently cause bugs
/*
        button("") {
            addClass(MyStyle.hideConfig, MyStyle.hideConfigOff)

            setOnMousePressed {
                setPressedStyle(true)
            }
            setOnMouseReleased {
                setPressedStyle(false)
            }

            action {
                driver.reloadViews()
            }
        }
*/

        // Fast Resize Enable/Disable Button. (resizing through touching the dividers will still be possible even if fast resize is disabled)
        togglebutton("") {
            tooltip("Enable / Disable Fast Resize")
            addClass(MyStyle.ccToggleButton)
            isSelected = true
            updateToggleStyle(isSelected, MyStyle.fastResizeButton_on, MyStyle.fastResizeButton_off)

            selectedProperty().onChange {
                driver.globalIsFastResizeEnabled = isSelected
                updateToggleStyle(isSelected, MyStyle.fastResizeButton_on, MyStyle.fastResizeButton_off)
            }
        }

        // Orientation Button
        togglebutton("") {
            tooltip("Change panel orientation")
            addClass(MyStyle.ccToggleButton)

            runLater {
                isSelected = when (driver.screenOrientation) {
                    Orientation.HORIZONTAL -> true
                    Orientation.VERTICAL -> false
                }
                updateToggleStyle(
                    isSelected,
                    MyStyle.orientationButton_horizontal_on,
                    MyStyle.orientationButton_vertical_on
                )
            }

            action {
                updateToggleStyle(
                    isSelected,
                    MyStyle.orientationButton_horizontal_on,
                    MyStyle.orientationButton_vertical_on
                )
                driver.flipOrientation()
            }
        }
    }

    fun adjustButtonOrientation() {
        runLater {
            root.orientation = when (driver.screenOrientation) {
                Orientation.HORIZONTAL -> Orientation.VERTICAL
                Orientation.VERTICAL -> Orientation.HORIZONTAL
            }


        }
    }
}
