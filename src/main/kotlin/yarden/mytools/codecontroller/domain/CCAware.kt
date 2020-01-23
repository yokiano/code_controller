@file:Suppress("unused")

package yarden.mytools.codecontroller.domain

import kotlinx.coroutines.ExperimentalCoroutinesApi

interface CCAware {

    @ExperimentalCoroutinesApi
    val controller : CodeController
}

@ExperimentalCoroutinesApi
fun CCAware.ccDouble (id: String, initCode : CCDouble.() -> Unit = {}) : Double = controller.ccDouble(id, initCode)
@ExperimentalCoroutinesApi
fun CCAware.ccBool (id: String, initCode : CCBool.() -> Unit = {}) : Boolean = controller.ccBool(id, initCode)
fun CCAware.ccPlot (id : String, x : Double, y: Double,howMany: Double = 1.0) = controller.ccPlot(id,x,y,howMany)