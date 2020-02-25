package yarden.mytools.codecontroller.presentation.viewimpl.tornadofx

import javafx.geometry.Pos
import javafx.scene.control.OverrunStyle
import javafx.scene.effect.DropShadow
import javafx.scene.effect.InnerShadow
import javafx.scene.layout.BackgroundRepeat
import javafx.scene.layout.BackgroundSize
import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import javafx.scene.text.TextAlignment
import tornadofx.*

class MyStyle : Stylesheet() {

    companion object {
        val mainView by cssclass()
        val controllersFlowpane by cssclass()
        // Global
        val root by cssclass()
//                val textColor = Color.GRAY
        val textColor = Color.BLANCHEDALMOND

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

        configViewStyle()

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
            borderColor += box(Color.WHITE)
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

        mainView {
            //            backgroundColor += c("#5d5d5d")
            backgroundImage += javaClass.getResource("/main_view/background/black_paper.png").toURI()
//            backgroundSize += BackgroundSize(400.0, 400.0, false, false, true, false)
//            unsafe("-fx-background-size", "auto")


        }

        // ------ MAIN MENU ------ //
        hideConfig {
            backgroundColor += Color.TRANSPARENT
            textOverrun = OverrunStyle.WORD_ELLIPSIS
        }
        hideConfigOn {
            graphic = javaClass.getResource("/main_menu/hide_config_controls_on.png").toURI()

        }
        Companion.hideConfigOff {
            graphic = javaClass.getResource("/main_menu/hide_config_controls_off.png").toURI()
        }

        // ------ LABELS ------ //
        infoPane {
            //            borderColor += box(Color.BLANCHEDALMOND)
//            backgroundColor += Color(1.0,0.0,0.0,0.2)
            backgroundColor += Color.web("313131", 0.4)
        }

        // -------------- CONTROLS SCROLL PANE --------------
        scrollPane {
            unsafe("-fx-background", raw("transparent"))
            unsafe("-fx-background-color", raw("transparent"))
        }

        splitPane {
            unsafe("-fx-background", raw("transparent"))
            unsafe("-fx-background-color", raw("transparent"))

        }
        splitPaneDivider {
            prefWidth = 3.px

            hover {
                fill = textColor
                unsafe("-fx-background", raw("red"))
                unsafe("-fx-background-color", raw("red"))
            }
        }

        // -------------- SLIDER --------------
        track {
            prefWidth = 13.px
            effect = InnerShadow(5.0, 3.0, 3.0, Color.BLACK)
            backgroundColor += c("#5d5d5d")
        }
        slider {
            thumb {
                val uri = javaClass.getResource("/controls/slider/hthumb.png").toURI()
                backgroundImage += uri
                prefWidth = 41.px
                prefHeight = 20.px
                effect = DropShadow(3.0, Color.BEIGE)

            }
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
        }
        toggleButtonOff {
            graphic = javaClass.getResource("/controls/toggle/off.png").toURI()
        }

        // ------ PLOTS ------ //
        lineChart {
            //            backgroundColor += Color.WHITE
//            textFill = Color.BLACK
        }

        axis {
            hover {
                backgroundColor += Color.RED
                +colorize
            }
        }
        axisLabel {
            hover {
                backgroundColor += Color.RED
                +colorize
            }
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

        chartLineSymbol {
            visibility = FXVisibility.COLLAPSE
            hover {
                visibility = FXVisibility.VISIBLE
            }
        }


    }

    fun configViewStyle() {
        configButton {
            backgroundColor += Color.TRANSPARENT
        }

    }
}
