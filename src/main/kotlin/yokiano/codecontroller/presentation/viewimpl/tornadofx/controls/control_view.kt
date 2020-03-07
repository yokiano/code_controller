package yokiano.codecontroller.presentation.viewimpl.tornadofx.controls

import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.control.Control
import javafx.util.Duration
import kotlinx.coroutines.delay
import tornadofx.*
import yokiano.codecontroller.presentation.viewimpl.tornadofx.TSlider
import yokiano.codecontroller.presentation.viewimpl.tornadofx.TUnit

abstract class ControlView(val unit: TUnit<*>) : View() {

    abstract override val root: Parent
    abstract val control: Node

    init {
        runLater {
            control.apply {
                contextmenu(unit.configView.itemsLambda)
            }
        }
    }
}