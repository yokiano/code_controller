package yokiano.codecontroller.presentation.viewimpl.tornadofx.panes

import javafx.scene.Node
import org.kodein.di.generic.instance
import org.kodein.di.tornadofx.kodein
import tornadofx.*
import yokiano.codecontroller.presentation.viewimpl.tornadofx.MyStyle
import yokiano.codecontroller.presentation.viewimpl.tornadofx.PaneType
import yokiano.codecontroller.presentation.viewimpl.tornadofx.TornadoDriver

class InfoPane : ResponsivePane() {

    override val type = PaneType.Info
    override lateinit var draggable: Node

    override val paneRoot = vbox {
        draggable = this
        addClass(MyStyle.infoPane)
//        prefWidth = primaryStage.width * 0.2
        paddingVertical = 20.0
        paddingHorizontal = 10.0
        bindChildren(driver.infoLabelList.asObservable()) { infoLabel ->
            borderpane {
                left {
                    label("${infoLabel.id} - ")
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
