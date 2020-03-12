@file:Suppress("EXPERIMENTAL_API_USAGE", "UNUSED_VARIABLE")

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import yokiano.codecontroller.domain.*
import java.time.format.DateTimeFormatter
import kotlin.math.sin

fun main() {
    MyClass().go()
    while (true) {
        // use any controller like below from anywhere in the code
        CodeController.ccToggleCode("Toggle Something") {
            /* Do Something */
        }
    }
}

// When extending CCAware you have access to all controllers without referring to the CodeController object as seen below
class MyClass : CCAware {

    fun go() {
        runBlocking {
            for (i in 0..100) {
                val iSin = sin(i.toDouble() * 0.1)
                ccPlot("my trace", i.toDouble(), iSin)

                val maxRange = ccDouble("MAX RANGE") {
                    range = 30.0..100.0
                } // few optional (but sometimes essential) configuration parameters are available in the configuration block.

                val vector = ccVec("vector2") { setRange(30.0, 30.0, maxRange, maxRange) }

                ccInfo("i", "$i")

                delay(100)
            }
        }
    }
}
