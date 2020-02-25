package yarden.mytools.codecontroller.presentation.viewimpl.tornadofx

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
import org.kodein.di.generic.instance
import org.kodein.di.tornadofx.kodein

import org.kodein.di.tornadofx.kodein
import tornadofx.*
import yarden.mytools.codecontroller.domain.CCUnitState
import yarden.mytools.codecontroller.presentation.viewimpl.tornadofx.panes.PlotPane
import javax.json.Json
import javax.json.JsonObject

class Plotter : Controller() {
    val lines = ArrayList<PlotLine>()
    var visible = false


    val maxDataPoints = 250
    fun addPlotLine(plotLine: PlotLine) {
        val driver: TornadoDriver by kodein().instance()

        if(lines.isEmpty()) {
            driver.activePanes.add(PlotPane())
            driver.reloadViews()
        }
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
                u1.item.targetPaneType.hashCode() > u2.item.targetPaneType.hashCode() -> 1
                u1.item.targetPaneType.hashCode() == u2.item.targetPaneType.hashCode() -> 0
                else -> -1
            }
        })
    }

    // Meaning that it's the first or last unit in it's pane.
    fun <T : TUnit<*>> isOneOfAType(tUnit: T): Boolean {
        return (takeAllOfType(tUnit.targetPaneType).size) == 1
    }

    fun takeAllOfType(paneType: PaneType): List<TUnitViewModel<*>> {
        return list.filter { it.item.targetPaneType == paneType }
    }
}

class TUnitViewModel<T>(unit: TUnit<T>) : ItemViewModel<TUnit<T>>(unit) {
    val valueVM = bind(TUnit<T>::valueProperty)
}

interface TUnit<T> {
    val id: String
    val value: T
    val valueProperty: SimpleObjectProperty<T>
    val targetPaneType: PaneType
    val initialValue: T

    val stateProperty: SimpleObjectProperty<CCUnitState>

    val configView: ConfigView<T>
    fun updateFromJson(jsonObject: JsonObject)
    fun convertToJson(): JsonObject


    fun dismiss() {
        println("Dismissing $id")

        stateProperty.value = CCUnitState.DEAD // stateProperty is observable to detect the dismiss event .

    }

}

class TSlider(
    override val id: String,
    override val initialValue: Double = 0.0,
    val range: ClosedRange<Double> = 0.0..1.0
) :
    TUnit<Double> {

    override val valueProperty = SimpleObjectProperty<Double>(initialValue)
    override val value: Double by valueProperty

    override val targetPaneType = PaneType.Slider
    override val stateProperty = SimpleObjectProperty<CCUnitState>(CCUnitState.LIVE)

    override val configView = ConfigView<Double>(this)
    override fun updateFromJson(jsonObject: JsonObject) {
        valueProperty.value = jsonObject.getDouble("value")
    }

    override fun convertToJson(): JsonObject {
        val jsonObject = Json.createObjectBuilder().add("value", value).build()
        return jsonObject
    }


}

class TToggle(override val id: String, override val initialValue: Boolean = false) : TUnit<Boolean> {
    override val targetPaneType = PaneType.Button
    override val stateProperty = SimpleObjectProperty<CCUnitState>(CCUnitState.LIVE)
    override val valueProperty = SimpleObjectProperty<Boolean>(initialValue)
    override val value: Boolean by valueProperty
    override val configView = ConfigView(this)
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
    override val targetPaneType = PaneType.Vector
    override val stateProperty = SimpleObjectProperty<CCUnitState>(CCUnitState.LIVE)

    override val valueProperty = SimpleObjectProperty<XYPoint>(initialValue)
    override val value: XYPoint by valueProperty
    override val configView = ConfigView(this)

    override fun updateFromJson(jsonObject: JsonObject) {
        val xy = jsonObject.getJsonObject("value")


        valueProperty.value = XYPoint(xy.getDouble("x"), xy.getDouble("y"))
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

// Represents the type of the target pane
sealed class PaneType {
    object Slider : PaneType()
    object Button : PaneType()
    object Vector : PaneType()
    object Info : PaneType()
    object Menu : PaneType()
    object Plot : PaneType()
}

