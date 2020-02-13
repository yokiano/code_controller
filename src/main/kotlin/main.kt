import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import yarden.mytools.codecontroller.domain.CodeController
import java.time.format.DateTimeFormatter
import kotlin.math.sin

fun main() {
    val controller = CodeController

    var j = 0
    while (true) {

        for (i in 0..2) {
            val a = controller.ccDouble("test$i") { range = 0.0..20.0; default = i.toDouble() }
        }

        runBlocking {

            for (i in 300..100000) {
                sendNum(i.toDouble(), controller)
            }
        }


    }
}

suspend fun sendNum(num: Double, controller: CodeController) {
    controller.ccPlot("test2",num, sin(num), howMany = 100)

    val bounder = 2.0
    val rangy = bounder to bounder*2
    controller.ccVec("vector2",1.0 to 1.0) { setRange(-2.0,5.0,-4.0,8.0) }
//    val a = controller.ccVec("testing") { setRange(30.0,30.0,100.0,100.0)}
        delay(50)

    controller.ccToggleCode("new feature") { }
}