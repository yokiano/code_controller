import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import yarden.mytools.codecontroller.domain.CodeController
import kotlin.math.sin

fun main() {
    val controller = CodeController

    var j = 0
    while (true) {

        for (i in 0..2) {
            val a = controller.ccDouble("test$i") { range = 0.0..20.0; default = i.toDouble() }
        }

        runBlocking {

            for (i in 0..50) {
                sendNum(i.toDouble(), controller)
            }
        }

//        val interval = 1_000_000_005
//        val sj = j % interval
//        val jd = sj.toDouble()
//        if (sj == 0L) {
//            val num = (j / interval - 1).toDouble()
//            controller.ccPlot(num, num * num)
//            println("YARDEN: sending ====  $  ${num * num})")
//        }
//        j += 1
//        Thread.sleep(500)


    }
}

suspend fun sendNum(num: Double, controller: CodeController) {
//    controller.ccPlot("expo", num, (num * num).coerceAtMost(400.0),1.0)
//    controller.ccPlot("sin", num, sin(num)*300,1.0)
//    controller.ccPlot("from control", num, controller.ccDouble("for plot") { range = 0.0..400.0; default = 0.0},1.0)
    println(controller.ccVec("testing"))
    delay(500)

    controller.ccBool("new feature", true) { }
}