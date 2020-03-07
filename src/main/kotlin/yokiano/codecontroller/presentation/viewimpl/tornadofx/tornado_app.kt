package yokiano.codecontroller.presentation.viewimpl.tornadofx

import XYControl
import javafx.beans.value.ObservableValue
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.XYChart
import javafx.scene.control.ScrollPane
import javafx.scene.control.ToggleButton
import javafx.scene.control.Tooltip
import javafx.scene.layout.FlowPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance
import org.kodein.di.tornadofx.kodein
import tornadofx.*
import yokiano.codecontroller.presentation.viewimpl.tornadofx.panes.InfoPane
import yokiano.codecontroller.presentation.viewimpl.tornadofx.panes.MenuPane


class TornadoApp(override val kodein: Kodein) : App(MainView2::class, MyStyle::class), KodeinAware {
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
