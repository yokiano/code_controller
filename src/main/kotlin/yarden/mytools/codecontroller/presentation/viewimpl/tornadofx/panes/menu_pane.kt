package yarden.mytools.codecontroller.presentation.viewimpl.tornadofx.panes

import javafx.scene.paint.Color
import org.kodein.di.generic.instance
import org.kodein.di.tornadofx.kodein
import tornadofx.*
import yarden.mytools.codecontroller.presentation.viewimpl.tornadofx.*

object MenuPane : View() {

    val driver: TornadoDriver by kodein().instance()

    // --- MAIN MENU
    override val root = vbox {
        style {
            backgroundColor += Color(0.0, 0.0, 0.0, 0.3)
        }

        // ON\OFF button.
        togglebutton("") {
            addClass(MyStyle.toggleButton)
            isSelected = true
            updateToggleStyle(true)

            selectedProperty().onChange {
                driver.internalChannel.send(it)
                updateToggleStyle(isSelected)
            }
        }

        // Currently used for reloading views
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

        // Button to disable the fast resizing feature. resizing through touching the dividers will still be possible though.
        togglebutton("") {
            addClass(MyStyle.toggleButton)
            isSelected = false
            updateToggleStyle(false)

            selectedProperty().onChange {
                driver.globalDisableFastResize = isSelected
                updateToggleStyle(isSelected)
            }
        }

    }
}
