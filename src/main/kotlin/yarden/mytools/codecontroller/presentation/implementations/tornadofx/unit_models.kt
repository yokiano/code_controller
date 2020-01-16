package yarden.mytools.codecontroller.presentation.implementations.tornadofx

import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import tornadofx.ItemViewModel
import tornadofx.Vector2D
import tornadofx.getValue
import tornadofx.sortWith

class ChartSeriesViewModel(chartSeries : ChartSeries) : ItemViewModel<ChartSeries>(chartSeries) {
    val dataPointlistVM = bind(ChartSeries::dataPointsList)
    var visible = false

}

class ChartSeries {
    val dataPointListProperty = SimpleListProperty<Vector2D>(FXCollections.observableArrayList())
    val dataPointsList by dataPointListProperty

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
    override val value = initialValue

    override val controlType = TType.Slider
}

class TToggle(override val id: String, override val initialValue: Boolean = false) : TUnit<Boolean> {
    override val controlType = TType.Toggle
    override val valueProperty = SimpleObjectProperty<Boolean>(initialValue)
    override val value: Boolean by valueProperty
}


sealed class TType {
    object Slider : TType()
    object Toggle : TType()
}
