package yokiano.codecontroller.presentation.viewimpl.tornadofx

import javafx.geometry.Orientation
import javafx.scene.control.Button
import javafx.scene.control.SplitPane
import javafx.scene.control.ToggleButton
import javafx.scene.layout.BorderPane
import javafx.scene.paint.Color
import org.kodein.di.generic.instance
import org.kodein.di.tornadofx.kodein
import tornadofx.*
import yokiano.codecontroller.presentation.viewimpl.tornadofx.panes.*

class MainView() : View() {

    private val driver: TornadoDriver by kodein().instance<TornadoDriver>()

    val splitpane: SplitPane by lazy { splitpane() }
    lateinit var borderPane: BorderPane


    // TODO - hide on creation and show when the controls are added in real time.
    override val root = stackpane {

        borderPane = borderpane {
            addClass(MyStyle.ccWindow)
            when (driver.screenOrientation) {
                Orientation.HORIZONTAL -> {
                    left = MenuPane.root
                }
                Orientation.VERTICAL -> {
                    top = MenuPane.root
                }
            }

            center {

                squeezebox {
                    splitpane.apply {

                        orientation = driver.screenOrientation
                        attachTo(this@center)

                        addInfoPane() // The infoPane is visible by default hence shouldn't be hidden at start up

                        addCCPanes(*driver.activePanes.toTypedArray())

                        runLater { autoPlaceDividers() }
                    }
                }

            }
        }


        group {
            rectangle {
                isMouseTransparent = true
                fill = Color.TRANSPARENT
                strokeWidth = 1.0
                stroke = MyStyle.defaultTextColor
                x = 2.0
                y = 2.0
                val widthBinding = doubleBinding(primaryStage.widthProperty(), widthProperty()) {
                    value - 4.0
                }
                val heightBinding = doubleBinding(primaryStage.heightProperty(), heightProperty()) {
                    value - 3.0
                }
                widthProperty().bind(widthBinding)
                heightProperty().bind(heightBinding)
            }
        }


    }

    fun addInfoPane() {
        driver.activePanes.apply {

            if (size <= 0) {
                return
            }
            if (first() is InfoPane) {
                removeAt(0)
                add(0, InfoPane())
            } else {
                add(0, InfoPane())
            }
        }
    }

    fun SplitPane.autoPlaceDividers() {

        val infoDivider = 0.10
        setDividerPosition(0, infoDivider) // the info pane Has fixed initial width.

        val step = (1 - infoDivider) / (items.size - 1)
        var offset = infoDivider + step

        for (i in 1 until dividers.size) {
            setDividerPosition(i, offset)
            offset += step
        }
    }
    // Adding a list of panes to the split view item list and hides the panes if requestex. the panes will be shown again only after they are in use.
}

fun SplitPane.addCCPanes(vararg panes: ResponsivePane, atIndex: Int = -1) {
    for (p in panes) {
        if (atIndex >= 0) {
            items.add(atIndex, p.root)
        } else {
            items.add(p.root)
        }
    }
}


fun ToggleButton.updateToggleStyle(
    value: Boolean,
    onStyle: CssRule = MyStyle.toggleButtonOn,
    offStyle: CssRule = MyStyle.toggleButtonOff
) {
    if (value) {
        removeClass(offStyle)
        addClass(onStyle)
    } else {
        removeClass(onStyle)
        addClass(offStyle)
    }
}

fun Button.setPressedStyle(value: Boolean) {
    if (value) {
        removeClass(MyStyle.hideConfigOff)
        addClass(MyStyle.hideConfigOn)
    } else {
        removeClass(MyStyle.hideConfigOn)
        addClass(MyStyle.hideConfigOff)
    }
}
