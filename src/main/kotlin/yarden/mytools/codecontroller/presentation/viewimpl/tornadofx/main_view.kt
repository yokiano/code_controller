package yarden.mytools.codecontroller.presentation.viewimpl.tornadofx

import javafx.scene.control.SplitPane
import javafx.scene.control.ToggleButton
import javafx.scene.control.skin.SplitPaneSkin
import org.kodein.di.generic.instance
import org.kodein.di.tornadofx.kodein
import tornadofx.*
import yarden.mytools.codecontroller.presentation.viewimpl.tornadofx.panes.*

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
            splitpane.attachTo(this)
                .addCCPanes(InfoPane()) // The infoPane is visible by default hence shouldn't be hidden at start up

            splitpane.addCCPanes(*driver.activePanes.toTypedArray())

            val infoDivider = 0.15
            splitpane.setDividerPositions(infoDivider)
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

fun ToggleButton.hideConfigButtonStyle(value: Boolean) {
    if (value) {
        removeClass(MyStyle.hideConfigOff)
        addClass(MyStyle.hideConfigOn)
    } else {
        removeClass(MyStyle.hideConfigOn)
        addClass(MyStyle.hideConfigOff)
    }
}
