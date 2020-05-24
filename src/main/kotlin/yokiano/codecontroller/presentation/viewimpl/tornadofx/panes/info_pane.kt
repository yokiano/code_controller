package yokiano.codecontroller.presentation.viewimpl.tornadofx.panes

import javafx.scene.Node
import org.kodein.di.generic.instance
import org.kodein.di.tornadofx.kodein
import tornadofx.*
import yokiano.codecontroller.presentation.viewimpl.tornadofx.MyStyle
import yokiano.codecontroller.presentation.viewimpl.tornadofx.PaneType
import yokiano.codecontroller.presentation.viewimpl.tornadofx.TornadoDriver
import yokiano.codecontroller.presentation.viewimpl.tornadofx.panes.ServiceBar.Companion.Features
class InfoPane : ResponsivePane() {

    override val type = PaneType.Info
    override lateinit var draggable: Node

    override val validServiceBarFeatures = arrayOf(false,true) // Only text resize

    override val paneRoot = vbox {
        draggable = this
        addClass(MyStyle.infoPane)
        bindChildren(driver.infoLabelList.asObservable()) { infoLabel ->
            borderpane {
                left {
                    label("${infoLabel.id} - ") {
                        tooltip(infoLabel.tooltip)
                    }
                }
                right {
                    label {
                        textProperty().bindBidirectional(infoLabel.valueProperty)
                    }
                }
            }
        }
    }


}
