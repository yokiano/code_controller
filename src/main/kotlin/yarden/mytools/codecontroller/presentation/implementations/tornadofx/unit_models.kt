package yarden.mytools.codecontroller.presentation.implementations.tornadofx

import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import tornadofx.ItemViewModel
import tornadofx.Vector2D
import tornadofx.getValue
import tornadofx.sortWith
import yarden.mytools.codecontroller.domain.CCUnitState

class Plotter {
    val lines = ArrayList<PlotLine>()
    var visible = false

    fun addPlotLine(plotLine: PlotLine) {
        lines.add(plotLine)
    }
}

class PlotLine(val id: String) {
    val dataPointListProperty = SimpleListProperty<Vector2D>(FXCollections.observableArrayList())
    val dataPointsList by dataPointListProperty

    var state = CCUnitState.NEW

    fun add(vec: Vector2D) {
        dataPointsList.add(vec)
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
    val initialValue : T
}

class TSlider(override val id: String, override val initialValue: Double = 0.0, val range: ClosedRange<Double> = 0.0..1.0) :
    TUnit<Double> {

    override val valueProperty = SimpleObjectProperty<Double>(initialValue)
    override val value: Double by valueProperty

    override val controlType = TType.Slider
}

class TToggle(override val id: String, override val initialValue: Boolean = false) : TUnit<Boolean> {
    override val controlType = TType.Toggle
    override val valueProperty = SimpleObjectProperty<Boolean>(initialValue)
    override val value: Boolean by valueProperty
}

class TXYControl(override val id: String, override val initialValue: Vector2D = Vector2D.ZERO) : TUnit<Vector2D> {
    override val controlType = TType.XYControl
    override val valueProperty = SimpleObjectProperty<Vector2D>(initialValue)
    override val value: Vector2D by valueProperty
}

sealed class TType {
    object Slider : TType()
    object Toggle : TType()
    object XYControl : TType()
}
