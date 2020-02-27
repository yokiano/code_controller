import javafx.geometry.Pos
import javafx.scene.control.ContextMenu
import javafx.scene.control.Control
import javafx.scene.image.ImageView
import tornadofx.*
import tornadofx.Stylesheet.Companion.tooltip
import yarden.mytools.codecontroller.presentation.viewimpl.tornadofx.GlobalConfig
import yarden.mytools.codecontroller.presentation.viewimpl.tornadofx.MyStyle
import yarden.mytools.codecontroller.presentation.viewimpl.tornadofx.TUnit
import java.lang.Exception

/*
open class Savable<T>(id: String, val type: TType) {

    val valueToSave = SimpleObjectProperty<T>()
    val configView = ConfigView(id,this)
}
*/

class ConfigView<T>(private val unit: TUnit<T>)  {

    private val globalConf = GlobalConfig.config

    private val keyString = "${unit.id}_${unit.targetPaneType.javaClass.simpleName}"

    val itemsLambda : ContextMenu.() -> Unit = {
        item("Save Value").action {
            with(globalConf) {
                val jsonObject = unit.convertToJson()
                set(keyString to jsonObject)
                save()

            }
        }
        item("Load Value").action {
            loadFromConfigFile()
        }
        item("Delete Saved Value").action {
            with(globalConf) {
                remove(keyString)
                save()
            }
        }
        item("Remove and Refactor").action {
            unit.dismiss()

            // Clearing config file after disposal.
            with(globalConf) {
                remove(keyString)
                save()
            }
        }

    }

    fun attachToControl(control: Control) {
        control.contextmenu(itemsLambda)
            // might be goot to save descriptions for documentation.
//            tooltip("Save value for later. The value will be loaded automatically in the next run")
//            tooltip("Load and apply the last saved value. If there is no saved value, default value will be applied")
//            tooltip("Remove the controller from the screen and replace the controller invocation (ccXX()) in the source code with the current value")
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
