@file:Suppress("unused", "EXPERIMENTAL_API_USAGE")

package yarden.mytools.codecontroller.domain

interface CCAware {

    val controller: CodeController
}

// Controllers
fun CCAware.ccDouble(id: String, fallBack: Double = 0.0, initCode: CCDouble.() -> Unit = {}): Double =
    controller.ccDouble(id, fallBack, initCode)

fun CCAware.ccBool(id: String, fallBack: Boolean = true, initCode: CCBool.() -> Unit = {}): Boolean =
    controller.ccBool(id, fallBack, initCode)

fun CCAware.ccToggleCode(id: String, on: Boolean, f: () -> Unit) = controller.ccToggleCode(id, on, f)
fun CCAware.ccVec(id: String, fallBack: Pair<Double, Double> = Pair(0.0, 0.0), initCode: CCVec.() -> Unit = {}) =
    controller.ccVec(id, fallBack, initCode)

// Information/Debugging  Panes
fun CCAware.ccPlot(id: String, x: Double, y: Double, howOften: Double = 1.0, howMany: Int = Int.MAX_VALUE) =
    controller.ccPlot(id, x, y, howOften, howMany)

fun CCAware.ccInfo(id: String, info: String, howMany: Double = 1.0) = controller.ccInfo(id, info, howMany)

