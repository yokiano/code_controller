@file:Suppress("unused", "EXPERIMENTAL_API_USAGE")

package yokiano.codecontroller.domain

interface CCAware {

}

// Controllers Panes
fun CCAware.ccDouble(id: String, fallBack: Double = 0.0, initCode: CCDouble.() -> Unit = {}): Double =
    CodeController.ccDouble(id, fallBack, initCode)

fun CCAware.ccBool(id: String, fallBack: Boolean = true, initCode: CCBool.() -> Unit = {}): Boolean =
    CodeController.ccBool(id, fallBack, initCode)

fun CCAware.ccToggleCode(id: String, on: Boolean, f: () -> Unit) = CodeController.ccToggleCode(id, on, f)
fun CCAware.ccVec2(id: String, fallBack: Pair<Double, Double> = Pair(0.0, 0.0), initCode: CCVec.() -> Unit = {}) =
    CodeController.ccVec2(id, fallBack, initCode)

// Information/Debugging Panes
fun CCAware.ccPlot(id: String, x: Double, y: Double, howOften: Double = 1.0, howMany: Int = Int.MAX_VALUE) =
    CodeController.ccPlot(id, x, y, howOften, howMany)

fun CCAware.ccInfo(id: String, info: String, howMany: Double = 1.0) = CodeController.ccInfo(id, info, howMany)

