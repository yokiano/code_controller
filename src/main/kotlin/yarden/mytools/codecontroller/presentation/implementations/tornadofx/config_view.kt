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

class ConfigView<T>(private val unit: TUnit<T>) : View() {

    private val globalConf = GlobalConfig.config

    private val keyString = "${unit.id}_${unit.controlType.javaClass.simpleName}"

    override val root = flowpane {

        alignment = Pos.CENTER
        button("",ImageView("/config_view/save.png")) {
            tooltip("Save value for later. The value will be loaded automatically in the next run")
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
            tooltip("Load and apply the last saved value. If there is no saved value, default value will be applied")
            addClass(MyStyle.loadButton,MyStyle.configButton)
            action {
                loadFromConfigFile()
            }
        }
        button("",ImageView("/config_view/delete.png")) {
            tooltip("Reset the saved value")
            addClass(MyStyle.deleteButton,MyStyle.configButton)
            action {
                with(globalConf) {
                    remove(keyString)
                    save()
                }
            }
        }
        button("",ImageView("/config_view/dismiss.png")) {
            tooltip("Remove the controller from the screen and replace the controller invocation (ccXX()) in the source code with the current value")
            addClass(MyStyle.dismissButton,MyStyle.configButton)
            action {
                unit.dismiss()

                // Clearing config file after disposal.
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
