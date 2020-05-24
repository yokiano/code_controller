@file:Suppress("EXPERIMENTAL_API_USAGE", "UNUSED_VARIABLE")

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import yokiano.codecontroller.domain.*
import kotlin.math.cos
import kotlin.math.sin

fun main() {
    CodeController.ccBool("Toggle1") {}
    CodeController.ccBool("Toggle2") {}
    CodeController.ccBool("Toggle3") {}
    Pair(0.0,0.0)
    MyClass2().go()
}

// When extending CCAware you have access to all controllers without referring to the CodeController object as seen below
class MyClass2 : CCAware {
    fun go() {
        runBlocking {
            for (i in 0..1000) {
                val iSin = sin(i.toDouble() * 0.1)
                val iCos = cos(i.toDouble() * 0.1)
                ccPlot("my sin", i.toDouble(), iSin)
                ccPlot("my cos", i.toDouble(), iCos)

                val maxRange = ccDouble("Slider1") {
                    range = 30.0..100.0
                } // few optional (but sometimes essential) configuration parameters are available in the configuration block.
                ccDouble("Slider2",3.0,1.0..12.0)

                val vector2 = ccVec2("XY Control1") { setRange(30.0, 30.0, 70.0, 70.0) }
                val vector3 = ccVec2("XY Control2") { setRange(30.0, 30.0, maxRange, maxRange) }
                ccInfo("AB", "${i * 20.5}")
                ccInfo("counter", "${i * 0.34}")
                ccInfo("XY1.x", "${vector2.first}")

                delay(500)
            }
        }
    }
}