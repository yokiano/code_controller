import javafx.scene.control.ContextMenu
import javafx.scene.control.Control
import javafx.stage.Modality
import javafx.stage.StageStyle
import tornadofx.*
import tornadofx.Stylesheet.Companion.tooltip
import yokiano.codecontroller.presentation.viewimpl.tornadofx.ControllersConfig
import yokiano.codecontroller.presentation.viewimpl.tornadofx.RefactoringHandler
import yokiano.codecontroller.presentation.viewimpl.tornadofx.TUnit
import java.lang.Exception

class ConfigView<T>(private val unit: TUnit<T>) {

    private val globalConf = ControllersConfig.config

    private val keyString = "${unit.id}_${unit.targetPaneType.javaClass.simpleName}"

    val itemsLambda: ContextMenu.() -> Unit = {
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

            RefactoringHandler(unit).openModal(stageStyle = StageStyle.DECORATED, block = true, resizable = true)

        }

    }

    fun loadFromConfigFile() {
        try {

            globalConf.jsonObject(keyString)?.let {
                runLater {
                    unit.updateFromJson(it)
                }
            }
        } catch (e: Exception) {
            println("Error while parsing the json from configuration file.")
        }

    }
}
