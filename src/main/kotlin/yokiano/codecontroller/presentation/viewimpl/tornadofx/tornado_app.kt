package yokiano.codecontroller.presentation.viewimpl.tornadofx

import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import tornadofx.*


class TornadoApp(override val kodein: Kodein) : App(MainView::class, MyStyle::class), KodeinAware {
    init {

//        reloadStylesheetsOnFocus()
//        reloadViewsOnFocus()
    }
}

// Dummy view to have a single configuration file for all controls.
object ControllersConfig : View() {
    override val root = borderpane { }
}
// Dummy view to have a configuration file for window dimensions and position.
object WindowConfig : View() {
    override val root = borderpane { }
}
