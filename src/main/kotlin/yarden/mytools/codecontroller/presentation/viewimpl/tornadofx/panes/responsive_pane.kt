package yarden.mytools.codecontroller.presentation.viewimpl.tornadofx.panes

import javafx.scene.control.SplitPane
import org.kodein.di.generic.instance
import org.kodein.di.tornadofx.kodein
import tornadofx.*
import yarden.mytools.codecontroller.presentation.viewimpl.tornadofx.PaneType
import yarden.mytools.codecontroller.presentation.viewimpl.tornadofx.TornadoDriver

abstract class ResponsivePane() : View() {
    // TODO - make the panes singletons/multitons instead instantiating them like now.
    val driver: TornadoDriver by kodein().instance()
    abstract val type : PaneType

    var minWidth = 0.0
    var minHeight = 0.0

    fun hide() {
        root.hide()
    }
}
