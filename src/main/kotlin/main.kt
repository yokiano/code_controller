import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import yarden.mytools.codecontroller.domain.CodeController

fun main() {
    val controller = CodeController

    var j = 0
//    val interval = interval1 / 2
    while (true) {

        for (i in 0..2) {
            val a = controller.ccDouble("test$i") { range = 0.0..20.0; default = i.toDouble() }
//            println("a = ${a}")
        }

        runBlocking {

            for (i in 0..3) {
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
    controller.ccPlot(num, num * num)
    delay(2000)
}