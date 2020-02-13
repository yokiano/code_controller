package yarden.mytools.codecontroller.presentation.implementations.tornadofx

import ConfigView
import XYPoint
import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance
import tornadofx.*
import yarden.mytools.codecontroller.domain.CCUnitState
import javax.json.Json
import javax.json.JsonObject

class Plotter {
    val lines = ArrayList<PlotLine>()
    var visible = false

    val maxDataPoints = 250
    fun addPlotLine(plotLine: PlotLine) {
        lines.add(plotLine)
    }
}


// class that binds the units list to the dynamic ViewModel
class UnitsListViewModel(unitList: UnitList) : ItemViewModel<UnitList>(unitList) {
    val listVM = bind(UnitList::list)
}

// holds the list of units
class UnitList {
    private val listP = SimpleListProperty<TUnitViewModel<*>>(FXCollections.observableArrayList())
    val list: ObservableList<TUnitViewModel<*>> by listP

    fun sort() {
        list.sortWith(Comparator { u1, u2 ->
            when {
                u1.item.controlType.hashCode() > u2.item.controlType.hashCode() -> 1
                u1.item.controlType.hashCode() == u2.item.controlType.hashCode() -> 0
                else -> -1
            }
        })
    }
}

class TUnitViewModel<T>(unit: TUnit<T>) : ItemViewModel<TUnit<T>>(unit) {
    val valueVM = bind(TUnit<T>::valueProperty)
}

interface TUnit<T> {
    val id: String
    val value: T
    val valueProperty: SimpleObjectProperty<T>
    val controlType: TType
    val initialValue: T

    val configView: ConfigView<T>
    fun updateFromJson(jsonObject: JsonObject)
    fun convertToJson(): JsonObject

}

class TSlider(
    override val id: String,
    override val initialValue: Double = 0.0,
    val range: ClosedRange<Double> = 0.0..1.0
) :
    TUnit<Double> {

    override val valueProperty = SimpleObjectProperty<Double>(initialValue)
    override val value: Double by valueProperty

    override val controlType = TType.Slider

    override val configView = ConfigView<Double>(id, this)
    override fun updateFromJson(jsonObject: JsonObject) {
        valueProperty.value = jsonObject.getDouble("value")
    }

    override fun convertToJson(): JsonObject {
        val jsonObject = Json.createObjectBuilder().add("value", value).build()
        return jsonObject
    }
}

class TToggle(override val id: String, override val initialValue: Boolean = false) : TUnit<Boolean> {
    override val controlType = TType.Toggle
    override val valueProperty = SimpleObjectProperty<Boolean>(initialValue)
    override val value: Boolean by valueProperty
    override val configView = ConfigView(id, this)
    override fun updateFromJson(jsonObject: JsonObject) {
        valueProperty.value = jsonObject.getBoolean("value")
    }

    override fun convertToJson(): JsonObject {
        val jsonObject = Json.createObjectBuilder().add("value", value).build()
        return jsonObject
    }
}

class TXYControl(
    override val id: String,
    val range: Pair<XYPoint, XYPoint> = Pair(XYPoint(0.0, 0.0), XYPoint(1.0, 1.0)),
    override val initialValue: XYPoint = XYPoint(range.first.x, range.first.y)
) : TUnit<XYPoint> {
    override val controlType = TType.XYControl
    override val valueProperty = SimpleObjectProperty<XYPoint>(initialValue)
    override val value: XYPoint by valueProperty
    override val configView = ConfigView(id, this)

    override fun updateFromJson(jsonObject: JsonObject) {
        val xy = jsonObject.getJsonObject("value")


        valueProperty.value = XYPoint(xy.getDouble("x"),xy.getDouble("y"))
    }

    override fun convertToJson(): JsonObject {
        return Json.createObjectBuilder().add(
            "value", Json.createObjectBuilder()
                .add("x", value.x)
                .add("y", value.y)
        )
            .build()
    }
}

class PlotLine(val id: String, override val kodein: Kodein) : KodeinAware {
    val dataPointListProperty = SimpleListProperty<Vector2D>(FXCollections.observableArrayList())
    val dataPointsList by dataPointListProperty

    val tornadoDriver: TornadoDriver by instance()

    var state = CCUnitState.NEW
    fun add(vec: Vector2D) {
        dataPointsList.add(vec)
        if (dataPointsList.size > tornadoDriver.plotter.maxDataPoints) dataPointsList.removeAt(0) // Constrains the size of of the plotter. TODO - configurable?
    }
}

class TInfoLabel(val id: String) {
    val valueProperty = SimpleStringProperty()
    val value: String by valueProperty

    var state = CCUnitState.NEW
}

sealed class TType {
    object Slider : TType()
    object Toggle : TType()
    object XYControl : TType()
    object InfoLabel : TType()
}

