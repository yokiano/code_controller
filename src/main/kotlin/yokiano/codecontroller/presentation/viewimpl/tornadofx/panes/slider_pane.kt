package yokiano.codecontroller.presentation.viewimpl.tornadofx.panes


import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.ScrollPane
import javafx.scene.control.skin.ScrollPaneSkin
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import tornadofx.*
import yokiano.codecontroller.presentation.viewimpl.tornadofx.PaneType
import yokiano.codecontroller.presentation.viewimpl.tornadofx.TSlider
import yokiano.codecontroller.presentation.viewimpl.tornadofx.controls.SliderView

class SliderPane : ResponsivePane() {

    override val type = PaneType.Slider

    override lateinit var draggable: Node

    override val paneRoot = scrollpane {
        vgrow = Priority.ALWAYS
        hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
        fixScrollerMouseEvents(this)

//        isPannable = true
//        isMouseTransparent = true
//        isPickOnBounds = false
        stackpane {
            vgrow = Priority.ALWAYS
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
    }

}
