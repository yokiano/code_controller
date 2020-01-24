import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.shape.Line
import javafx.scene.shape.Rectangle
import tornadofx.*

class XYControl(val id: String, val range: Pair<XYPoint, XYPoint>) : View() {

    val pointerProperty = SimpleObjectProperty<XYPoint>(XYPoint(0.0, 0.0))
    var pointer : XYPoint by pointerProperty

    val internalX = SimpleDoubleProperty(0.0)
    val internalY = SimpleDoubleProperty(0.0)

    //    var pointerX = SimpleDoubleProperty(30.0)
//    var pointerY = SimpleDoubleProperty(30.0)
    val valueLabel = SimpleStringProperty("(${pointer.x},${pointer.y})")

    val size = 150.0
    val limit = 190.0
//    var px : Double by pxp

    lateinit var rect: Rectangle

    override val root: VBox = vbox {
        setPrefSize(limit, limit)

        alignment = Pos.CENTER

        controller
        label(this@XYControl.id)
        label("$valueLabel") {
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

                fun updatePointer(x: Double, y: Double) {
                    val nx = x.coerceIn(0.0, width)
                    val ny = y.coerceIn(0.0, height)
                    pointer.x = nx.mapTo(0.0,width,range.first.x,range.second.x)
                    pointer.y = ny.mapTo(0.0,height,range.first.y,range.second.y)
                    internalX.value = nx
                    internalY.value = ny
                    valueLabel.value = "(${"%.2f".format(pointer.x)},${"%.2f".format(pointer.y)})"
                }

                setOnMouseDragged {
                    it.apply {
                        updatePointer(x, y)
                    }
                }
                setOnTouchMoved {
                    it.touchPoint.apply {
                        updatePointer(x, y)
                    }
                }
                val draggedEvent = onMouseDragged
                val touchEvent = onTouchMoved
                setOnMouseClicked {
                    updatePointer(it.x, it.y)
                }


                // ------ CHILDREN ------ //                                                // ------ CHILDREN ------ //



                circle {
                    fill = Color.WHITE
//                    pointer.xProperty.bindBidirectional(centerXProperty())
//                    pointer.yProperty.bindBidirectional(centerYProperty())
                    centerXProperty().bind(internalX)
                    centerYProperty().bind(internalY)
                    radius = 6.0

                    onMouseDragged = draggedEvent
                    onTouchMoved = touchEvent

                }

                val setParams: Line.() -> Unit = {
                    stroke = Color.GRAY
                    fill = Color.WHITE
                    strokeWidth = .5
                    onMouseDragged = draggedEvent
                    onTouchMoved = touchEvent

                }

                line {
                    startX = 0.0
                    startYProperty().bind(internalY)
                    endX = width
                    endYProperty().bind(internalY)
                }.setParams()
                line {
                    startXProperty().bind(internalX)
                    startY = 0.0
                    endXProperty().bind(internalX)
                    endY = height
                }.setParams()


            }

        }

    }
}

class XYPoint(x_: Double, y_: Double) {
    val xProperty = SimpleDoubleProperty(x_)
    val yProperty = SimpleDoubleProperty(y_)
    var x: Double by xProperty
    var y: Double by yProperty

}