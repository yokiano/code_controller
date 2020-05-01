@file:Suppress("UNUSED_VARIABLE")

package yokiano.codecontroller.presentation.viewimpl.tornadofx

import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.control.OverrunStyle
import javafx.scene.effect.DropShadow
import javafx.scene.effect.InnerShadow
import javafx.scene.layout.BackgroundSize
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import javafx.scene.text.FontWeight
import javafx.scene.text.TextAlignment
import tornadofx.*

class MyStyle : Stylesheet() {

    // For getting the color of the line chart symbol.
    val bgc by cssproperty<MultiValue<Paint>>("-fx-background-color;")
    val bg by cssproperty<MultiValue<Paint>>("-fx-background;")

    companion object {
        val mainView by cssclass()
        val controllersFlowpane by cssclass()
        val root by cssclass()



        // Helper Values
//        val textColor = Color.GRAY
        val textColor = Color.BLANCHEDALMOND
        val SEMI_OPAQUE = Color(0.3,0.3,0.3,0.3)
        val ALMOST_OPAQUE = Color(0.5,0.5,0.5,0.3)
        val ALMOST_TRANSPARENT = Color(0.2,0.2,0.2,0.3)

        // Main Menu
        val powerButton_on by cssclass()
        val powerButton_off by cssclass()
        val orientationButton_vertical_on by cssclass()
        val orientationButton_horizontal_on by cssclass()
        val fastResizeButton_on by cssclass()
        val fastResizeButton_off by cssclass()

        // Config View
        val configButton by cssclass()
        val saveButton by cssclass()
        val loadButton by cssclass()
        val dismissButton by cssclass()
        val deleteButton by cssclass()
        val hideConfig by cssclass()
        val hideConfigOn by cssclass()
        val hideConfigOff by cssclass()


        // Info label
        val infoPane by cssclass()

        // Toggle
        val toggleLabel by cssclass()
        val togglesVBox by cssclass()
        val toggleButton by cssclass()
        val toggleButtonOn by cssclass()
        val toggleButtonOff by cssclass()

        // Sliders
        val sliderLabel by cssclass()
        val sliderTextField by cssclass()
        val sliderStyle by cssclass()
        val slidersVBox by cssclass()

        //Debug
        val someBox by cssclass()

        //Plots
        val lineChart by cssclass()
        val plotLine by cssclass()
    }

    init {

        val defaultTextStyle_ = mixin {
            textFill = textColor
            fontSize = 10.px
            textAlignment = TextAlignment.CENTER
            fontWeight = FontWeight.MEDIUM
            wrapText = true
        }

        text {
            +defaultTextStyle_
        }
        label {
            +defaultTextStyle_
        }
        contextMenu {
            backgroundColor += c("#222222")
        }

        // for DEBUG
        // -------------- LAYOUT --------------
        someBox {
            borderColor += box(Color.WHITE)
        }
        // For debugging
        val colorize = mixin {
            val bcolor = Color.RED
            val color = Color.YELLOW
            backgroundColor += bcolor
            textFill = color
            barFill = color
            highlightFill = color
            promptTextFill = color
            tickLabelFill = color
            highlightTextFill = color
//            fill = color
            accentColor = color
            baseColor = color
            alternativeColumnFillVisible = true
            alternativeRowFillVisible = true
        }

        mainView {
            //            backgroundColor += c("#5d5d5d")
            backgroundImage += javaClass.getResource("/main_view/background/black_paper.png").toURI()
//            backgroundSize += BackgroundSize(400.0, 400.0, false, false, true, false)
//            unsafe("-fx-background-size", "auto")
        }

        // ------ MAIN MENU ------ //

        // Orientation Button
        powerButton_on {
            graphic = javaClass.getResource("/main_menu/power_on.png").toURI()
        }
        powerButton_off {
            graphic = javaClass.getResource("/main_menu/power_off.png").toURI()
        }

        orientationButton_vertical_on {
            graphic = javaClass.getResource("/main_menu/vertical_on.png").toURI()
        }
        orientationButton_horizontal_on {
            graphic = javaClass.getResource("/main_menu/horizontal_on.png").toURI()
        }
        // Fast resize button
        fastResizeButton_on {
            graphic = javaClass.getResource("/main_menu/fast_resize_on.png").toURI()
        }
        Companion.fastResizeButton_off {
            graphic = javaClass.getResource("/main_menu/fast_resize_off.png").toURI()
        }



        // ------ LABELS ------ //
        infoPane {
            //            borderColor += box(Color.BLANCHEDALMOND)
//            backgroundColor += Color(1.0,0.0,0.0,0.2)
            backgroundColor += Color.web("313131", 0.4)
        }

        // -------------- GENERAL PANE ATTRIBUTES (SCROLL PANE / SPLIT PANE) --------------
        scrollPane {
            unsafe("-fx-background", raw("transparent"))
            unsafe("-fx-background-color", raw("transparent"))
        }

        scrollPane {

            thumb {
                backgroundColor += SEMI_OPAQUE

                hover {
//                    backgroundColor += ALMOST_OPAQUE
                }

            }
            track {
                backgroundColor += ALMOST_TRANSPARENT
            }

            incrementArrow and incrementButton and decrementArrow and decrementButton  {
//                visibility = FXVisibility.HIDDEN
            }
        }
        scrollBar {
            backgroundColor += Color.TRANSPARENT


        }

        splitPane {
            unsafe("-fx-background", raw("transparent"))
            unsafe("-fx-background-color", raw("transparent"))

        }
        splitPaneDivider {
            prefWidth = 1.px
        }


        // -------------- SLIDER --------------

        slider {
            thumb {
                val uri = javaClass.getResource("/controls/slider/hthumb.png").toURI()
                backgroundImage += uri
                backgroundSize += BackgroundSize(1.0,1.0,true ,true, true,false)
                prefWidth = 29.px
                prefHeight = 14.px
                effect = DropShadow(3.0, Color.BEIGE)

            }
            track {
                prefWidth = 13.px
                effect = InnerShadow(5.0, 3.0, 3.0, Color.BLACK)
                backgroundColor += c("#5d5d5d")
            }
        }
        sliderTextField {
            +defaultTextStyle_
            prefWidth = 100.px
            backgroundColor += Color.TRANSPARENT
            alignment = Pos.CENTER

        }

        sliderLabel {

        }

//            // ** SLIDER BUTTONS **  ---- currently deprecated.
//        button {
//            fontWeight = FontWeight.BOLD
//            backgroundImage += javaClass.getResource("/controllers/button/normal.png").toURI()
//            and(pressed) {
//                backgroundImage += javaClass.getResource("/controllers/button/pressed.png").toURI()
//            }
//        }


        // -------------- TOGGLE BUTTON --------------\
        label {
        }
        toggleLabel {
        }
        toggleButton {
            backgroundColor += Color.TRANSPARENT
            textOverrun = OverrunStyle.WORD_ELLIPSIS
        }
        toggleButtonOn {
            graphic = javaClass.getResource("/controls/toggle/toggle_on.png").toURI()
        }
        toggleButtonOff {
            graphic = javaClass.getResource("/controls/toggle/toggle_off.png").toURI()
        }

        // ------ PLOTS ------ //
        lineChart {
            //            backgroundColor += Color.WHITE
//            textFill = Color.BLACK
//            hover {
//                this.selections[] and chartLineSymbol {
//                    visibility = FXVisibility.VISIBLE
//                }
//            }
        }



        chartLegend {
            backgroundColor += Color.TRANSPARENT
            orientation = Orientation.VERTICAL
                chartLineSymbol {
                    visibility = FXVisibility.VISIBLE
//                    backgroundColor = bgc.value
//                    unsafe("-fx-background-color", raw("-fx-background-color"))
    //                baseColor = Color.BLACK
//                    println("a = ${a}")
                }

            label {
                +defaultTextStyle_
                backgroundColor += Color.TRANSPARENT
                chartLegendSymbol {
//                    backgroundColor += Color.RED
//                    fill = c("red")
                }
                child("*") {
//                    backgroundColor +=
                }


            }
        }
        chartLegendItem {
            textFill = Color.TRANSPARENT
        }

        chartLineSymbol {
            visibility = FXVisibility.COLLAPSE

        }


    }


}
