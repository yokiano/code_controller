package yarden.mytools.codecontroller.presentation.viewimpl.tornadofx.panes

import org.kodein.di.generic.instance
import org.kodein.di.tornadofx.kodein
import tornadofx.*
import yarden.mytools.codecontroller.presentation.viewimpl.tornadofx.MyStyle
import yarden.mytools.codecontroller.presentation.viewimpl.tornadofx.PaneType
import yarden.mytools.codecontroller.presentation.viewimpl.tornadofx.TornadoDriver

class InfoPane : ResponsivePane() {

    override val type = PaneType.Info

    override val root = vbox {
        addClass(MyStyle.infoPane)
        prefWidth = primaryStage.width * 0.2
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

    init {
        minWidth = 100.0
        minHeight = 50.0

        root.minWidth = minWidth
        root.minHeight = minHeight

    }

}
