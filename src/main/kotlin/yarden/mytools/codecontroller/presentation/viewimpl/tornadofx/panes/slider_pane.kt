package yarden.mytools.codecontroller.presentation.viewimpl.tornadofx.panes


import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.ScrollPane
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import tornadofx.*
import yarden.mytools.codecontroller.presentation.viewimpl.tornadofx.PaneType
import yarden.mytools.codecontroller.presentation.viewimpl.tornadofx.TSlider
import yarden.mytools.codecontroller.presentation.viewimpl.tornadofx.controls.SliderView

class SliderPane : ResponsivePane() {

    override val type = PaneType.Slider

    override lateinit var draggable: Node

    override val root = scrollpane {
        hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
        flowpane {
            draggable = this
            prefWidthProperty().bind(this@scrollpane.widthProperty())
            alignment = Pos.CENTER
            paddingAll = 20
            hgrow = Priority.ALWAYS

            bindChildren(driver.unitsList.listVM.value.filter { it.item is TSlider }.asObservable()) { unitVM ->
                when (val unit = unitVM.item) {
                    is TSlider -> {
                        SliderView(unit).root
                    }
                    else -> {
                        label("Unrecognized control unit") {
                            style {
                                textFill = Color.RED
                            }
                        }
                    }
                }
            }
        }
    }

    init {
        setMouseEvents()
    }
}
