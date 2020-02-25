package yarden.mytools.codecontroller.presentation.viewimpl.tornadofx.panes

import javafx.scene.paint.Color
import org.kodein.di.generic.instance
import org.kodein.di.tornadofx.kodein
import tornadofx.*
import yarden.mytools.codecontroller.presentation.viewimpl.tornadofx.*

object MenuPane : ResponsivePane() {

    override val type = PaneType.Menu

    // --- MAIN MENU
    override val root = vbox {


        togglebutton("") {
            addClass(MyStyle.toggleButton)
            isSelected = true
            updateToggleStyle(true)

            selectedProperty().onChange {
                driver.internalChannel.send(it)
                updateToggleStyle(isSelected)
            }


        }

        togglebutton("") {
            addClass(MyStyle.hideConfig)
            isSelected = driver.hideConfigButtons
            hideConfigButtonStyle(isSelected)

            selectedProperty().onChange {
                driver.run {
                    hideConfigButtons = !hideConfigButtons
                    reloadViews()
                }
                // TODO - abstract the different toggle buttons to reuse functionality of toggles.
                hideConfigButtonStyle(isSelected)

            }
        }

        style {
            backgroundColor += Color(0.0, 0.0, 0.0, 0.3)
        }
    }
}
