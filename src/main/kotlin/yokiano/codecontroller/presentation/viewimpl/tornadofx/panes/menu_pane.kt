package yokiano.codecontroller.presentation.viewimpl.tornadofx.panes

import javafx.geometry.Orientation
import javafx.scene.paint.Color
import javafx.stage.Screen
import org.kodein.di.generic.instance
import org.kodein.di.tornadofx.kodein
import tornadofx.*
import yokiano.codecontroller.presentation.viewimpl.tornadofx.*

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

        // Button to switch from horizontal to vertical views.
        togglebutton("") {
            addClass(MyStyle.toggleButton)
            isSelected = false
            updateToggleStyle(false)

            selectedProperty().onChange {
                driver.screenOrientation = when(driver.mainView.splitpane.orientation) {
                    Orientation.HORIZONTAL -> Orientation.VERTICAL
                    Orientation.VERTICAL -> Orientation.HORIZONTAL
                    null -> Orientation.HORIZONTAL
                }
                driver.mainView.splitpane.orientation = driver.screenOrientation

                driver.activePanes.forEach {
                    it.setup()
                }

                driver.currentScreenBounds.apply {
                    val widthRate = primaryStage.width / width
                    val heightRate = primaryStage.height / height
                    primaryStage.width = heightRate * width
                    primaryStage.height = widthRate * height

                    val newY = (((primaryStage.x - minX) / (maxX - minX)) * height) + minY
                    val newX = (((primaryStage.y - minY) / (maxY - minY)) * width) + minX
                    primaryStage.x = newX.coerceIn(minX,maxX - primaryStage.width)
                    primaryStage.y = newY.coerceIn(minY, maxY - primaryStage.height)

                }
            }
        }
    }
}
