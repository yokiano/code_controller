import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.shape.Line
import javafx.scene.shape.Rectangle
import tornadofx.*

class XYControl(val id: String) : View() {

    var pointer = Vector2D.ZERO
    var pointerX = SimpleDoubleProperty(30.0)
    var pointerY = SimpleDoubleProperty(30.0)
    val valueLabel = SimpleStringProperty("(${pointerX.value},${pointerY.value})")

    val size = 150.0
    val limit = 190.0
//    var px : Double by pxp

    lateinit var rect: Rectangle

    override val root: VBox = vbox {
        setPrefSize(limit, limit)

        alignment = Pos.CENTER

        controller
            label(this@XYControl.id)
            label("$valueLabel - ${pointerX.value}") {
                textProperty().bind(valueLabel)
                paddingBottom = -14.0
            }
    }

    val controller = stackpane {

        paddingAll = 15.0
        group {
            rect = rectangle {
                fill = Color.BLACK
                width = size
                height = size
                arcWidth = 10.0
                arcHeight = 10.0

                fun update(x: Double, y: Double) {

                    val nx = x.coerceIn(0.0, width)
                    val ny = y.coerceIn(0.0, height)
                    pointerX.value = nx
                    pointerY.value = ny
                    valueLabel.value = "($nx,$ny)"
                }

                setOnMouseDragged {
                    it.apply {
                        update(x, y)
                    }
                }
                setOnTouchMoved {
                    it.touchPoint.apply {
                        update(x, y)
                    }
                }
                val draggedEvent = onMouseDragged
                val touchEvent = onTouchMoved
                setOnMouseClicked {
                    update(it.x, it.y)
                }


                // ------ CHILDREN ------ //                                                // ------ CHILDREN ------ //
                circle {
                    fill = Color.WHITE
                    centerXProperty().bind(pointerX)
                    centerYProperty().bind(pointerY)
                    radius = 7.0

                    onMouseDragged = draggedEvent
                    onTouchMoved = touchEvent

                }

                val setParams: Line.() -> Unit = {
                    stroke = Color.WHITE
                    fill = Color.WHITE
                    strokeWidth = .5
                    onMouseDragged = draggedEvent
                    onTouchMoved = touchEvent


                }
                line {
                    startX = 0.0
                    startYProperty().bind(pointerY)
                    endX = width
                    endYProperty().bind(pointerY)


                }.setParams()
                line {
                    startXProperty().bind(pointerX)
                    startY = 0.0
                    endXProperty().bind(pointerX)
                    endY = height
                }.setParams()


            }

        }

    }
}