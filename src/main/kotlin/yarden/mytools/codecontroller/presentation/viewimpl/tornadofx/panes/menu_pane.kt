package yarden.mytools.codecontroller.presentation.viewimpl.tornadofx.panes

import javafx.scene.Node
import javafx.scene.paint.Color
import tornadofx.*
import yarden.mytools.codecontroller.presentation.viewimpl.tornadofx.*

object MenuPane : ResponsivePane() {

    override val type = PaneType.Menu

    override lateinit var draggable: Node

    // --- MAIN MENU
    override val root = vbox {

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


        button("") {
            addClass(MyStyle.hideConfig,MyStyle.hideConfigOff)

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

        style {
            backgroundColor += Color(0.0, 0.0, 0.0, 0.3)
        }
    }
}
