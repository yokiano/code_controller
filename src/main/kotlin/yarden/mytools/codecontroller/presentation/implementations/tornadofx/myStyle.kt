package yarden.mytools.codecontroller.presentation.implementations.tornadofx

import javafx.geometry.Pos
import javafx.scene.control.OverrunStyle
import javafx.scene.effect.DropShadow
import javafx.scene.effect.InnerShadow
import javafx.scene.image.Image
import javafx.scene.layout.BackgroundSize
import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import javafx.scene.text.TextAlignment
import tornadofx.*
import java.io.File

class MyStyle : Stylesheet() {

    companion object {
        val labelsPane by cssclass()
        val toggleLabel by cssclass()
        val togglesVBox by cssclass()
        val toggleButton by cssclass()
        val toggleButtonOn by cssclass()
        val toggleButtonOff by cssclass()
        val sliderLabel by cssclass()
        val root by cssclass()
        val sliderTextField by cssclass()
        val someBox by cssclass()
        val sliderStyle by cssclass()
        val slidersVBox by cssclass()

        val lineChart by cssclass()
        val plotLine by cssclass()

        val textColor = Color.BLANCHEDALMOND

    }

    init {
        val defaultTextStyle = mixin {
            textFill = textColor
            fontSize = 16.px
            textAlignment = TextAlignment.CENTER
            fontWeight = FontWeight.BOLD
            wrapText = true
        }

        // for DEBUG
        // -------------- LAYOUT --------------
        someBox {
            //            borderColor += box(Color.WHITE)
        }
        // For debugging
        val colorize = mixin {
            val bcolor = Color.RED
            val color = Color.YELLOW
//            backgroundColor += bcolor
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

        root {
            //            backgroundColor += c("#5d5d5d")
            backgroundImage += javaClass.getResource("/main_view/background/2338.jpg").toURI()
            backgroundSize += BackgroundSize(10.0, 100.0, false, false, true, true)
        }

        // ------ LABELS ------ //
        labelsPane {
            //            borderColor += box(Color.BLANCHEDALMOND)
//            backgroundColor += Color(1.0,0.0,0.0,0.2)
            backgroundColor += Color.web("313131", 0.4)
        }
        // -------------- SLIDER --------------
        track {
            prefWidth = 13.px
            effect = InnerShadow(5.0, 3.0, 3.0, Color.BLACK)
            backgroundColor += c("#5d5d5d")
        }
        thumb {
            val uri = javaClass.getResource("/controls/slider/thumb2.png").toURI()
            backgroundImage += uri
            prefWidth = 35.px
            prefHeight = 71.px
            effect = DropShadow(3.0, Color.BEIGE)
        }
        sliderTextField {
            +defaultTextStyle
            prefWidth = 100.px
            backgroundColor += Color.TRANSPARENT
            alignment = Pos.CENTER
        }

        sliderLabel {
            +defaultTextStyle
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
            +defaultTextStyle
        }
        toggleLabel {
            +defaultTextStyle
        }
        toggleButton {
            backgroundColor += Color.TRANSPARENT
            textOverrun = OverrunStyle.WORD_ELLIPSIS
        }
        toggleButtonOn {
            graphic = javaClass.getResource("/controls/toggle/on.png").toURI()
//            graphic = File("/controls/toggle/on.png").toURI()

        }
        toggleButtonOff {
            graphic = javaClass.getResource("/controls/toggle/off.png").toURI()
        }

        // ------ PLOTS ------ //
        lineChart {
            backgroundColor += Color.WHITE
            textFill = Color.BLACK
        }
        chartLegend {
            backgroundColor += Color.WHITE
                chartLineSymbol {
                    visibility = FXVisibility.VISIBLE
                    baseColor = Color.BLACK
            }
        }
        chartLegendItem {
            textFill = Color.BLACK
            fontWeight = FontWeight.LIGHT
        }
        chartSeriesLine {
            fill = Color.BLACK

        }
        chartSymbol {
            fill = Color.BLACK

        }
        chartLineSymbol {
            visibility = FXVisibility.COLLAPSE
            hover {
                visibility = FXVisibility.VISIBLE
            }
        }


    }

}
