package yokiano.codecontroller.presentation.viewimpl.tornadofx.panes

import javafx.beans.property.SimpleDoubleProperty
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.Node
import tornadofx.*
import yokiano.codecontroller.presentation.viewimpl.tornadofx.MyStyle

class ServiceBar(
    val controlScale: SimpleDoubleProperty,
    val textScale: SimpleDoubleProperty,
    validFeatures: Array<Boolean>
) : View() {

    companion object {

        enum class Features {
            CONTROL_RESIZE,
            TEXT_RESIZE,
        }
    }

    val _maxHeight = 4.px
    val scaleStep = 0.05

    override val root = flowpane {
        style {
            backgroundColor += c("#5d5d5d44")
            maxHeight = _maxHeight
            prefHeight = _maxHeight
            alignment = Pos.CENTER
            orientation = Orientation.HORIZONTAL
        }

        fun configureMouseEvents(node: Node) {
            node.setOnMouseEntered {
                node.addClass(MyStyle.zoomButtonHover)
                node.removeClass(MyStyle.serviceBarButton)
            }
            node.setOnMouseExited {
                node.removeClass(MyStyle.zoomButtonHover)
                node.addClass(MyStyle.serviceBarButton)
            }
        }

        if (validFeatures[Features.CONTROL_RESIZE.ordinal]) {
            runLater {
                // Decrease Size Button (MINUS)
                button("cc-") {
//                button {
                    addClass(MyStyle.serviceBarButton)
//                    addClass(MyStyle.serviceBarButton, MyStyle.zoomButtonMinus)
                    tooltip("Decrease Control Size")
                    configureMouseEvents(this)

                    action {
                        controlScale.value += (-scaleStep)
                    }
                }

                // Increase size button (PLUS)
                button("cc+") {
//                button {
                    addClass(MyStyle.serviceBarButton)
//                    addClass(MyStyle.serviceBarButton, MyStyle.zoomButtonPlus)
                    tooltip("Increase Control Size")
                    configureMouseEvents(this)

                    action {
                        controlScale.value += scaleStep
                    }

                }

            }
        }

        if (validFeatures[Features.TEXT_RESIZE.ordinal]) {
            runLater {
                // Text Decrease
                button("A-") {
                    addClass(MyStyle.serviceBarButton, MyStyle.textZoomButton)
                    tooltip("Decrease Text Size")
                    configureMouseEvents(this)

                    action {
                        textScale.value += (-scaleStep)
                    }

                }
                // Text Increase
                button("A+") {
                    addClass(MyStyle.serviceBarButton, MyStyle.textZoomButton)
                    tooltip("Increase Text Size")
                    configureMouseEvents(this)

                    action {
                        textScale.value += scaleStep
                    }

                }

            }
        }

        this.hide()
    }


}