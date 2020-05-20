package yokiano.codecontroller.presentation.viewimpl.tornadofx.panes

import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.ScrollPane
import javafx.scene.layout.Priority
import tornadofx.*
import yokiano.codecontroller.presentation.viewimpl.tornadofx.PaneType
import yokiano.codecontroller.presentation.viewimpl.tornadofx.TXYControl
import yokiano.codecontroller.presentation.viewimpl.tornadofx.controls.XYControlView

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

            paddingAll = 10.0
            bindChildren(driver.unitsList.listVM.value.filter {
                it.item is TXYControl
            }.toObservable()) { unitVM ->
                val unit = unitVM.item as TXYControl
                XYControlView(unit, this@VectorPane).run {
                    setControlScaleHandler(controlScale)
                    root.attachTo(this@flowpane)
                }
            }
        }
    }
}


