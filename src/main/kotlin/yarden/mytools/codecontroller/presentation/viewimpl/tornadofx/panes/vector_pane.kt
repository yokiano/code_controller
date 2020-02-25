package yarden.mytools.codecontroller.presentation.viewimpl.tornadofx.panes

import XYControl
import javafx.geometry.Pos
import javafx.scene.control.ScrollPane
import javafx.scene.layout.Priority
import org.kodein.di.generic.instance
import org.kodein.di.tornadofx.kodein
import tornadofx.*
import yarden.mytools.codecontroller.presentation.viewimpl.tornadofx.MyStyle
import yarden.mytools.codecontroller.presentation.viewimpl.tornadofx.PaneType
import yarden.mytools.codecontroller.presentation.viewimpl.tornadofx.TXYControl
import yarden.mytools.codecontroller.presentation.viewimpl.tornadofx.TornadoDriver

class VectorPane : ResponsivePane() {

    override val type = PaneType.Vector


    override val root = scrollpane {
        hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
        flowpane {
            prefWidthProperty().bind(this@scrollpane.widthProperty())
            alignment = Pos.CENTER
            hgrow = Priority.ALWAYS; vgrow = Priority.ALWAYS

            paddingAll = 20.0
            bindChildren(driver.unitsList.listVM.value.filter {
                it.item is TXYControl
            }.toObservable()) { unitVM ->
                val unit = unitVM.item as TXYControl
                XYControl(unit.id, unit.range, unit.valueProperty).run {
                    if (!driver.hideConfigButtons) attachConfigButtons(unit.configView)
                    root.attachTo(this@flowpane)
                }
            }
        }
    }

    init {
/* no need for minWidth actually - the user will be able to shrink panes as much as he likes.
        minWidth = 250.0
        minHeight = 300.0

        root.minWidth = minWidth
        root.minHeight = minHeight
*/
    }

}


