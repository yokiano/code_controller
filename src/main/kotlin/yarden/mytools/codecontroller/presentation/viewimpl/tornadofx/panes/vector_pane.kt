package yarden.mytools.codecontroller.presentation.viewimpl.tornadofx.panes

import XYControl
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.ScrollPane
import javafx.scene.layout.Priority
import org.kodein.di.generic.instance
import org.kodein.di.tornadofx.kodein
import tornadofx.*
import yarden.mytools.codecontroller.presentation.viewimpl.tornadofx.MyStyle
import yarden.mytools.codecontroller.presentation.viewimpl.tornadofx.PaneType
import yarden.mytools.codecontroller.presentation.viewimpl.tornadofx.TXYControl
import yarden.mytools.codecontroller.presentation.viewimpl.tornadofx.TornadoDriver
import yarden.mytools.codecontroller.presentation.viewimpl.tornadofx.controls.XYControlView

class VectorPane : ResponsivePane() {

    override val type = PaneType.Vector

    override lateinit var draggable: Node

    override val paneRoot = scrollpane {
        hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
        fixScrollerMouseEvents(this)
        flowpane {
            draggable = this
            prefWidthProperty().bind(this@scrollpane.widthProperty())
            alignment = Pos.CENTER
            hgrow = Priority.ALWAYS; vgrow = Priority.ALWAYS

            paddingAll = 20.0
            bindChildren(driver.unitsList.listVM.value.filter {
                it.item is TXYControl
            }.toObservable()) { unitVM ->
                val unit = unitVM.item as TXYControl
                val xyControl = XYControlView(unit,this@VectorPane)
                xyControl.root.attachTo(this)

            }
        }
    }

}


