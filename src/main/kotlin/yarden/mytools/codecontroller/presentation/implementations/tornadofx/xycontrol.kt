import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.shape.Line
import javafx.scene.shape.Rectangle
import tornadofx.*

class XYControl(val id: String, val range: Pair<XYPoint, XYPoint>, value: XYPoint) : View() {

    val pointerProperty = SimpleObjectProperty<XYPoint>(value)
    var pointer: XYPoint by pointerProperty

    private val dimensions = 150.0

    val internalX = SimpleDoubleProperty(fromValuetoPixel(value.x, value.y).first)
    val internalY = SimpleDoubleProperty(fromValuetoPixel(value.x, value.y).second)

    val valueLabel = SimpleStringProperty("(${"%.2f".format(pointer.x)},${"%.2f".format(pointer.y)})")



    lateinit var configBox: HBox
    lateinit var rect: Rectangle

    override val root: VBox = vbox {
        alignment = Pos.CENTER
        configBox = hbox {
            alignment = Pos.CENTER

            vbox {
                alignment = Pos.CENTER
                label(this@XYControl.id)
                label("$valueLabel") {
                    textProperty().bindBidirectional(valueLabel)
                    paddingBottom = -14.0
                }
            }
        }

        controller
    }

    val controller = stackpane {
        paddingAll = 15.0
        group {
            rect = rectangle {
                stroke = Color.BLANCHEDALMOND
                strokeWidth = 2.0
                fill = Color.BLACK
                width = dimensions
                height = dimensions
                arcWidth = 10.0
                arcHeight = 10.0


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
                    centerXProperty().bindBidirectional(internalX)
                    centerYProperty().bindBidirectional(internalY)
                    radius = 3.0

                    onMouseDragged = draggedEvent
                    onTouchMoved = touchEvent

                }

                val setParams: Line.() -> Unit = {
                    stroke = Color.BLANCHEDALMOND
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

    fun updatePointer(x: Double, y: Double) {
        fromPixelToValue(x, y).run {
            pointer.x = first
            pointer.y = second
            internalX.value = x.coerceIn(0.0, dimensions)
            internalY.value = y.coerceIn(0.0, dimensions)

        }
        valueLabel.value = "(${"%.2f".format(pointer.x)},${"%.2f".format(pointer.y)})"
    }

    fun attachConfigButtons(cv: ConfigView<XYPoint>) {
        cv.root.run {
            maxWidth = (this@XYControl.dimensions / 2.0)
            attachTo(configBox)
        }
    }

    private fun fromPixelToValue(x: Double, y: Double): Pair<Double, Double> {
        val nx = x.coerceIn(0.0, rect.width).mapTo(0.0, rect.width, range.first.x, range.second.x)
        val ny = y.coerceIn(0.0, rect.height).mapTo(0.0, rect.height, range.first.y, range.second.y)
        return (nx to ny)
    }

    private fun fromValuetoPixel(x: Double, y: Double): Pair<Double, Double> {
//        val nx = x.mapTo(range.first.x, range.second.x, 0.0, 150.0)
//        val ny = y.mapTo(range.first.y, range.second.y, 0.0, 150.0)
        val nx = x.mapTo(range.first.x, range.second.x, 0.0, dimensions)
        val ny = y.mapTo(range.first.y, range.second.y, 0.0, dimensions)
        return (nx to ny)
    }
}

class XYPoint(x_: Double, y_: Double) {
    val xProperty = SimpleDoubleProperty(x_)
    val yProperty = SimpleDoubleProperty(y_)
    var x: Double by xProperty
    var y: Double by yProperty

}