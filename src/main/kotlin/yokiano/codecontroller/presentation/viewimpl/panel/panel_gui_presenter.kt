// Experimental. Commented to remove dependency in openrndr library

package yokiano.codecontroller.presentation.viewimpl.panel
//
//import yokiano.codecontroller.domain.CCType
//import kotlinx.coroutines.channels.Channel
//import kotlinx.coroutines.channels.sendBlocking
//import org.openrndr.Program
//import org.openrndr.launch
//import org.openrndr.panel.ControlManager
//import org.openrndr.panel.elements.*
//import org.openrndr.panel.layout
//import org.openrndr.panel.style.*
//
////import org.openrndr.panel.style.styleSheet
//import org.openrndr.panel.styleSheet
//import yokiano.codecontroller.presentation.common.entities.CCGuiSlider
//import yokiano.codecontroller.presentation.common.entities.CCGuiToggle
//import yokiano.codecontroller.presentation.common.entities.CCGuiUnit
//
//class PanelGuiPresenter(
//    private val outChannel: Channel<CCGuiUnit>,
//    private val inChannel: Channel<CCGuiUnit>
//) : Program() {
//
//    private val guiUnits: ArrayList<CCGuiUnit> = ArrayList()
//
//    override fun setup() {
//        launch {
//            for (newElement in inChannel) {
//                guiUnits.add(newElement)
//                constructUI()
//            }
//        }
//    }
//
//    private fun sendEvent(guiUnit: CCGuiUnit) {
//        // TODO - instead of sending modified classes, send a "command" to the yokiano.codecontroller.domain layer (code_controller.kt) to make an operation on the local repository
//        outChannel.sendBlocking(guiUnit)
//    }
//
//    override fun draw() {
//
//    }
//
//    private fun constructUI() {
//        extensions.clear()
//        extend(ControlManager()) {
//
//            val myFontSize = 20.px
//            styleSheet((has type "toggle")  ) {
//                fontSize = myFontSize
//            }
//            styleSheet(has type "slider") {
//                fontSize = myFontSize
//            }
//
//            styleSheet( has type "body") {
//
//            }
//
//
//            layout {
//
//                for (ccUnit in guiUnits.filter { it.ccType == CCType.DOUBLE }) {
//                    slider {
//                        val ccSlider = ccUnit as CCGuiSlider
//                        label = ccSlider.id
//                        value = ccSlider.default
//                        range = Range(ccSlider.range.start, ccSlider.range.endInclusive)
//                        events.valueChanged.subscribe {
//                            sendEvent(ccSlider.apply {
//                                this.value = it.newValue
//                            })
//                        }
//                    }
//                }
//
//                for (ccUnit in guiUnits.filter { it.ccType == CCType.BOOL }) {
//                    toggle {
//                        val ccToggle = ccUnit as CCGuiToggle
//                        label = ccToggle.id
//                        value = ccToggle.value
//                        events.valueChanged.subscribe {
//                            sendEvent(ccToggle.apply {
//                                value = !value
//                            })
//                        }
//                    }
//                }
//
//
//            }
//
//        }
//    }
//
//
//}
//
//
