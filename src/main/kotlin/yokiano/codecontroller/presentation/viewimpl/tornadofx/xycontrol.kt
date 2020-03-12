import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import javafx.scene.control.Control
import javafx.scene.layout.HBox
import javafx.scene.paint.Color
import javafx.scene.shape.Line
import javafx.scene.shape.Rectangle
import tornadofx.*
import yokiano.codecontroller.presentation.viewimpl.tornadofx.MyStyle

class XYControl(val id_: String, val range: Pair<XYPoint, XYPoint>, valueP: SimpleObjectProperty<XYPoint>) : View() {

    val pointerProperty = SimpleObjectProperty<XYPoint>(valueP.value)
    var pointer: XYPoint by pointerProperty

    private val dimensions = 150.0

    val internalX = SimpleDoubleProperty(fromValuetoPixel(valueP.value.x, valueP.value.y).first)
    val internalY = SimpleDoubleProperty(fromValuetoPixel(valueP.value.x, valueP.value.y).second)

    val valueLabel = SimpleStringProperty("(${"%.2f".format(pointer.x)},${"%.2f".format(pointer.y)})")

    lateinit var configBox: HBox
    lateinit var rect: Rectangle

    override val root = createController()

//    val controller = createController()

    init {
        pointerProperty.bindBidirectional(valueP)


        pointerProperty.onChange {
            it?.let {
                val convertedToPixels = fromValuetoPixel(it.x, it.y)
                internalX.value = convertedToPixels.first
                internalY.value = convertedToPixels.second
                valueLabel.value = "(${"%.2f".format(convertedToPixels.first)},${"%.2f".format(convertedToPixels.second)})"
            }
        }
    }

    fun createController() = stackpane {
        paddingAll = 15.0
        group {
            rect = rectangle {
                stroke = Color.BLANCHEDALMOND
                strokeWidth = 2.0
                fill = MyStyle.SEMI_OPAQUE
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
                    centerXProperty().bindBidirectional(internalX)
                    centerYProperty().bindBidirectional(internalY)
                    radius = 3.0

                    onMouseDragged = draggedEvent
                    onTouchMoved = touchEvent

                }

                // Helper function
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

/*    fun attachConfigButtons(cv: ConfigView<XYPoint>): ConfigView<XYPoint> {
        cv.root.run {
            maxWidth = (this@XYControl.dimensions)
            attachTo(this@XYControl.root)
        }
        return cv
    }*/

    private fun fromPixelToValue(x: Double, y: Double): Pair<Double, Double> {
        val nx = x.coerceIn(0.0, rect.width).mapTo(0.0, rect.width, range.first.x, range.second.x)
        val ny = y.coerceIn(0.0, rect.height).mapTo(0.0, rect.height, range.first.y, range.second.y)
        return (nx to ny)
    }

    private fun fromValuetoPixel(x: Double, y: Double): Pair<Double, Double> {
//        val nx = x.mapTo(range.first.x, range.second.x, 0.0, 150.0)
//        val ny = y.mapTo(range.first.y, range.second.y, 0.0, 150.0)
        val nx = x.mapTo(range.first.x, range.second.x, 0.0, dimensions).coerceIn(0.0,dimensions)
        val ny = y.mapTo(range.first.y, range.second.y, 0.0, dimensions).coerceIn(0.0,dimensions)
        return (nx to ny)
    }
}

class XYPoint(x_: Double, y_: Double) {
    val xProperty = SimpleDoubleProperty(x_)
    val yProperty = SimpleDoubleProperty(y_)
    var x: Double by xProperty
    var y: Double by yProperty

}