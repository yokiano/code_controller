@file:Suppress("EXPERIMENTAL_API_USAGE", "UNUSED_VARIABLE")

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import yarden.mytools.codecontroller.domain.*
import java.time.format.DateTimeFormatter
import kotlin.math.cos
import kotlin.math.sin

fun main() {
    CodeController.ccToggleCode("Toggle1") {}
    CodeController.ccToggleCode("Toggle2") {}
    CodeController.ccToggleCode("Toggle3") {}
    CodeController.ccToggleCode("Toggle4") {}
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
                ccDouble("Slider2")
                ccDouble("Slider3")
                ccDouble("Slider4")

                val vector1 = ccVec("vector1") { setRange(30.0, 30.0, maxRange, maxRange) }
                val vector2 = ccVec("vector2") { setRange(30.0, 30.0, maxRange, maxRange) }
                val vector3 = ccVec("vector3") { setRange(30.0, 30.0, maxRange, maxRange) }
                val vector4 = ccVec("vector4") { setRange(30.0, 30.0, maxRange, maxRange) }

                ccInfo("AB", "${i * 20.5}")
                ccInfo("information", "${i - 200}")
                ccInfo("FPS", "${i}")
                ccInfo("counter", "${i * 0.34}")

                delay(100)
            }
        }
    }
}