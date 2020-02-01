import javafx.geometry.Pos
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.Priority
import tornadofx.*
import yarden.mytools.codecontroller.presentation.implementations.tornadofx.GlobalConfig
import yarden.mytools.codecontroller.presentation.implementations.tornadofx.MyStyle
import yarden.mytools.codecontroller.presentation.implementations.tornadofx.TUnit
import java.awt.Color
import java.lang.Exception

/*
open class Savable<T>(id: String, val type: TType) {

    val valueToSave = SimpleObjectProperty<T>()
    val configView = ConfigView(id,this)
}
*/

class ConfigView<T>(unitId: String, private val unit: TUnit<T>) : View() {

    private val globalConf = GlobalConfig.config

    private val keyString = "${unitId}_${unit.controlType.javaClass.simpleName}"

    override val root = flowpane {

        alignment = Pos.CENTER
        button("",ImageView("/config_view/save.png")) {
            addClass(MyStyle.saveButton,MyStyle.configButton)

            action {
                with(globalConf) {
                    val jsonObject = unit.convertToJson()
                    set(keyString to jsonObject)
                    save()
                }
            }
        }
        button("",ImageView("/config_view/load.png")) {
            addClass(MyStyle.loadButton,MyStyle.configButton)
            action {
                loadFromConfigFile()
            }
        }
        button("",ImageView("/config_view/delete.png")) {
            addClass(MyStyle.deleteButton,MyStyle.configButton)
            action {
                with(globalConf) {
                    remove(keyString)
                    save()
                }
            }
        }
//        button("Set Default")
    }

    fun loadFromConfigFile() {
        try {
            globalConf.jsonObject(keyString)?.let {

                unit.updateFromJson(it)
            }
        } catch (e: Exception) {
            println("Error while parsing the json from configuration file.")
        }

    }
}
