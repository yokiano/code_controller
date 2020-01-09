import yarden.mytools.codecontroller.domain.CodeController

fun main() {
    val controller = CodeController


    while (true) {
        val a = controller.ccDouble("test") { range = 0.0..20.0; default = 10.0 }
        println("a = ${a}")
        Thread.sleep(500)
    }
}