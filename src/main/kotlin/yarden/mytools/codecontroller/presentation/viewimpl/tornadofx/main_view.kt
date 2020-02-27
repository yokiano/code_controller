package yarden.mytools.codecontroller.presentation.viewimpl.tornadofx

import javafx.scene.Parent
import javafx.scene.control.Button
import javafx.scene.control.SplitPane
import javafx.scene.control.ToggleButton
import javafx.scene.control.skin.SplitPaneSkin
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.kodein.di.generic.instance
import org.kodein.di.tornadofx.kodein
import tornadofx.*
import yarden.mytools.codecontroller.presentation.viewimpl.tornadofx.panes.*
import java.time.LocalDateTime

class MainView2() : View() {

    private val driver: TornadoDriver by kodein().instance()

    val splitpane = splitpane()

    // TODO - hide on creation and show when the controls are added in real time.
    // TODO - Add plotPane

    override val root = borderpane {
        addClass(MyStyle.mainView)

        left {
            MenuPane.root.attachTo(this)
        }
        center {

            splitpane.apply {

                attachTo(this@center)

                addInfoPane() // The infoPane is visible by default hence shouldn't be hidden at start up

                addCCPanes(*driver.activePanes.toTypedArray())

                runLater { autoPlaceDividers() }

            }

        }
    }

    fun addInfoPane() {
        driver.activePanes.apply {
            if ( first() is InfoPane) {
                removeAt(0)
                add(0,InfoPane())
            } else {
                add(0,InfoPane())
            }
        }
    }
/*
    override fun onDock() {
        println("from onDock - width = ${this.splitpane.width}")
        println("from onDock - this = ${this}")
    }

    override fun onBeforeShow() {
        println("from onBeforeShow - width = ${this.splitpane.width}")
    }

    override fun onUndock() {
        println("from onUnDock - width = ${this.splitpane.width}")
        println("from onUnDock - this = ${this}")
    }
*/


    fun SplitPane.autoPlaceDividers() {

        val infoDivider = 0.10
        setDividerPosition(0, infoDivider) // the info pane Has fixed initial width.

        val step = (1-infoDivider) / (items.size - 1)
        var offset = infoDivider + step

        for (i in 1 until dividers.size) {
            setDividerPosition(i, offset)
            offset += step
        }
    }

}


/*
enum class PaneDefaultWidth(val defaultPosition: Double) {
    Info(0.15),
}
fun SplitPane.setDefaultLayout() {

//    setDividerPosition(PaneDefaultWidth.Info.ordinal,)
}
*/

// Adding a list of panes to the split view item list and hides the panes if requestex. the panes will be shown again only after they are in use.
fun SplitPane.addCCPanes(vararg panes: ResponsivePane) {
    for (p in panes) {
        items.add(p.root)
    }
}

fun ToggleButton.updateToggleStyle(value: Boolean) {
    if (value) {
        removeClass(MyStyle.toggleButtonOff)
        addClass(MyStyle.toggleButtonOn)
    } else {
        removeClass(MyStyle.toggleButtonOn)
        addClass(MyStyle.toggleButtonOff)
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
