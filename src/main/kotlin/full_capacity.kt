@file:Suppress("EXPERIMENTAL_API_USAGE", "UNUSED_VARIABLE")

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import yokiano.codecontroller.domain.*
import java.time.format.DateTimeFormatter
import kotlin.math.cos
import kotlin.math.sin

fun main() {
    CodeController.ccToggleCode("Toggle1") {}
    CodeController.ccToggleCode("Toggle2") {}
    CodeController.ccToggleCode("Toggle3") {}
    CodeController.ccBool("Toggle4") {}
    CodeController.ccVec2("vec main") {}
    MyClass2().go()
}

// When extending CCAware you have access to all controllers without referring to the CodeController object as seen below
class MyClass2 : CCAware {
    fun go() {
        runBlocking {
            for (i in 0..100) {
                val iSin = sin(i.toDouble() * 0.1)
                val iCos = cos(i.toDouble() * 0.1)
                ccPlot("my sin", i.toDouble(), iSin)
                ccPlot("my cos", i.toDouble(), iCos)

                val maxRange = ccDouble("Slider1") {
                    range = 30.0..100.0
                } // few optional (but sometimes essential) configuration parameters are available in the configuration block.
                val test = ccDouble("Slider1") { range = 20.0..25.0}

                ccDouble("Slider2")
                ccDouble("Slider3")
//                ccDouble("Slider4") { range = 0.0..3.0 }
                ccDouble("Slider4") { range = (0.0..3.0).apply {  } }

                ccInfo("AB", "${i * 20.5}")
                ccInfo("information", "${i - 200}")
                ccInfo("FPS", "${i}")
                ccInfo("counter", "${i * 0.34}")

                val vector1 = ccVec2("vector1") { setRange(30.0, 30.0, 100.0, 100.0) }
//                val vector2 = ccVec2("vector2") { setRange(30.0, 30.0, maxRange, maxRange) }
//                val vector3 = ccVec2("vector3") { setRange(30.0, 30.0, maxRange, maxRange) }
//                val vector4 = ccVec2("vector4") { setRange(30.0, 30.0, maxRange, maxRange) }

                delay(100)
            }
        }
    }
}