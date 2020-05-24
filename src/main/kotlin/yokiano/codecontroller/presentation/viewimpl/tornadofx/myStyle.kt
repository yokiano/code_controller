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
import tornadofx.*

class MyStyle : Stylesheet() {

    // For getting the color of the line chart symbol.
    val bgc by cssproperty<MultiValue<Paint>>("-fx-background-color;")
    val bg by cssproperty<MultiValue<Paint>>("-fx-background;")

    companion object {

        val ccWindow by cssclass()
        val controllersFlowpane by cssclass()
        val root by cssclass()


        // Helper Values
//        val defaultTextColor = Color.GRAY
        val defaultTextColor = Color.BLANCHEDALMOND
        val SEMI_OPAQUE = Color(0.3, 0.3, 0.3, 0.3)
        val ALMOST_OPAQUE = Color(0.5, 0.5, 0.5, 0.3)
        val ALMOST_TRANSPARENT = Color(0.2, 0.2, 0.2, 0.3)

        // Main Menu
        val powerButton_on by cssclass()
        val powerButton_off by cssclass()
        val orientationButton_vertical_on by cssclass()
        val orientationButton_horizontal_on by cssclass()
        val fastResizeButton_on by cssclass()
        val fastResizeButton_off by cssclass()

        // Config View
        val hideConfigOn by cssclass()
        val hideConfigOff by cssclass()

        // Refactoring View
        val refactoring_view by cssclass()
        val taskProgress by cssclass()
        val leftTextArea by cssclass()
        val rightTextArea by cssclass()
        val pathListView by cssclass()
        val greenButton by cssclass()

        // Responsive Pane
        val responsivePane by cssclass()

        // Service Bar
        val serviceBarButton by cssclass()
        val textZoomButton by cssclass()
        val zoomButtonHover by cssclass()
        val zoomButtonMinus by cssclass()
        val zoomButtonPlus by cssclass()

        // Info label
        val infoPane by cssclass()

        // Toggle
        val toggleLabel by cssclass()
        val togglesVBox by cssclass()
        val ccToggleButton by cssclass()
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

        val defaultTextSize = 10.0
        val defaultTextStyle_std = mixin {
            textFill = defaultTextColor
            fontSize = Dimension(defaultTextSize, Dimension.LinearUnits.px)
//            textAlignment = TextAlignment.CENTER
            fontWeight = FontWeight.MEDIUM
            wrapText = true
        }

        val largerTextSize = 13.0
        val defaultTextStyle_larger = mixin {
            textFill = defaultTextColor
            fontSize = Dimension(largerTextSize, Dimension.LinearUnits.px)
//            textAlignment = TextAlignment.CENTER
            fontWeight = FontWeight.MEDIUM
//            wrapText = true
        }


        // Parameters
        val serviceBarHeight = 24.px
        val refactorViewBorderColor = box(c("#444444"))

    }


    init {


/*
        text {
            +defaultTextStyle_std
        }
*/
        label {
            +defaultTextStyle_std
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

        ccWindow {
            backgroundImage += javaClass.getResource("/main_view/background/black_paper.png").toURI()
//            backgroundSize += BackgroundSize(400.0, 400.0, false, false, true, false)
//            unsafe("-fx-background-size", "auto")
        }

        // ------ MAIN MENU ------ //
        //<editor-fold desc="Main Menu>>>>>>>>>>>">
        // Orientation Button
        powerButton_on {
            graphic = javaClass.getResource("/main_menu/30px/power_on.png").toURI()
        }
        powerButton_off {
            graphic = javaClass.getResource("/main_menu/30px/power_off.png").toURI()
        }

        orientationButton_vertical_on {
            graphic = javaClass.getResource("/main_menu/30px/vertical_on.png").toURI()
        }
        orientationButton_horizontal_on {
            graphic = javaClass.getResource("/main_menu/30px/horizontal_on.png").toURI()
        }
        // Fast resize button
        fastResizeButton_on {
            graphic = javaClass.getResource("/main_menu/30px/fast_resize_on.png").toURI()
        }
        Companion.fastResizeButton_off {
            graphic = javaClass.getResource("/main_menu/30px/fast_resize_off.png").toURI()
        }
        //</editor-fold>

        // -------------- CONTEXT MENU --------------
        contextMenu {
            backgroundColor += c("#222222")
        }


        // -------------- REFACTORING VIEW --------------
        //<editor-fold desc="REFACTORING VIEW>>>>>>>>>>>>">


        refactoring_view {
            padding = box(30.px, 20.px, 20.px, 20.px)

            text {
                +defaultTextStyle_larger
            }

            corner {
                backgroundColor += Color.TRANSPARENT
            }

            textArea {
                textFill = Color.WHITE
                fontSize = (defaultTextSize * 1.3).px
                alignment = Pos.TOP_LEFT
                borderColor += refactorViewBorderColor
                backgroundColor += Color.TRANSPARENT
                content {
                    backgroundColor += Color.TRANSPARENT

                }

            }

            rightTextArea {
                highlightFill = c("#00aa0088")
                highlightTextFill = defaultTextColor
            }
            leftTextArea {
                highlightFill = c("#aa000088")
                highlightTextFill = defaultTextColor
            }

            val buttonMixin = mixin {
                backgroundColor += ALMOST_TRANSPARENT
                borderColor += refactorViewBorderColor
                +defaultTextStyle_std

                and(hover) {
                    backgroundColor += ALMOST_OPAQUE
                }

                and(pressed) {
                    backgroundColor += ALMOST_TRANSPARENT
                }
            }
            button {
                +buttonMixin
            }


            greenButton {
                backgroundColor += c("#00aa0044")


                and(hover) {
                    backgroundColor += c("#00aa0033")
                }
            }

            toggleButton {
                +buttonMixin
            }

            tooltip {
                fontSize = (defaultTextSize * 1.2).px
            }
        }

        pathListView {
            backgroundColor += Color.TRANSPARENT

            listCell {
                backgroundColor += Color.TRANSPARENT
                and(selected) {
                    backgroundColor += ALMOST_OPAQUE
                }
            }

            thumb {
                backgroundColor += SEMI_OPAQUE
            }
        }

        taskProgress {
            track {
                backgroundColor += Color.TRANSPARENT
            }
            accentColor = c("#00ff0022")
            backgroundColor += Color.TRANSPARENT
        }

        checkBox {
            +defaultTextStyle_std
            box {
                backgroundColor += Color.TRANSPARENT
                borderColor += refactorViewBorderColor
            }
        }

        //</editor-fold>


        // ========================================================================================================================================================================
        // ========================================== PANES ====================================================================================================================
        // ========================================================================================================================================================================
        // ------ RESPONSIVE PANE ------ //
        responsivePane {
            padding = box(0.px, 0.px, serviceBarHeight, 0.px)
        }

        // ------ SERVICE BAR ------ //
        //<editor-fold desc="Service Bar >>>>>>>>>">
        zoomButtonHover {
            +defaultTextStyle_std
            backgroundColor += c("#5d5d5d66")
        }
        serviceBarButton {
            +defaultTextStyle_std
            backgroundColor += Color.TRANSPARENT
            maxHeight = serviceBarHeight
        }
        zoomButtonPlus {
            graphic = javaClass.getResource("/service_bar/plus.png").toURI()
        }
        zoomButtonMinus {
            graphic = javaClass.getResource("/service_bar/minus.png").toURI()
        }
        //</editor-fold>

        // ------ INFO PANE ------ //
        //<editor-fold desc="Info Pane >>>>>">
        infoPane {
            //            borderColor += box(Color.BLANCHEDALMOND)
//            backgroundColor += Color(1.0,0.0,0.0,0.2)
            backgroundColor += Color.web("313131", 0.4)
            padding = box(5.px, 5.px, serviceBarHeight, 5.px)
        }
        //</editor-fold>-

        // -------------- GENERAL PANE ATTRIBUTES (SCROLL PANE / SPLIT PANE) --------------
        //<editor-fold desc="Scroll and Split Panes >>>>>>>>>>>>>>>>">
        scrollPane {
            unsafe("-fx-background", raw("transparent"))
            unsafe("-fx-background-color", raw("transparent"))
        }

        scrollPane {

            thumb {
                backgroundColor += SEMI_OPAQUE
            }
            track {
                backgroundColor += ALMOST_TRANSPARENT
            }

            incrementArrow and incrementButton and decrementArrow and decrementButton {
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
        //</editor-fold>
        // ========================================================================================================================================================================


        // ========================================================================================================================================================================
        // ========================================== CONTROLS ====================================================================================================================
        // ========================================================================================================================================================================
        // -------------- SLIDER --------------
        //<editor-fold desc="Slider >>>>>>>>>>>>>">
        sliderStyle {
            thumb {
                val uri = javaClass.getResource("/controls/slider/hthumb.png").toURI()
                backgroundImage += uri
                backgroundSize += BackgroundSize(1.0, 1.0, true, true, true, false)
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
            +defaultTextStyle_std
            backgroundColor += ALMOST_TRANSPARENT
//            backgroundColor += Color.TRANSPARENT
            alignment = Pos.CENTER_RIGHT

        }

//            // ** SLIDER BUTTONS **  ---- currently deprecated.
//        button {
//            fontWeight = FontWeight.BOLD
//            backgroundImage += javaClass.getResource("/controllers/button/normal.png").toURI()
//            and(pressed) {
//                backgroundImage += javaClass.getResource("/controllers/button/pressed.png").toURI()
//            }
//        }
        //</editor-fold>

        // -------------- TOGGLE BUTTON --------------\
        //<editor-fold desc="Toggle >>>>>>>>>>>>>>>">
        label {
        }
        toggleLabel {
        }
        ccToggleButton {
            backgroundColor += Color.TRANSPARENT
            textOverrun = OverrunStyle.WORD_ELLIPSIS
        }
        toggleButtonOn {
            graphic = javaClass.getResource("/controls/toggle/toggle_on.png").toURI()
        }
        toggleButtonOff {
            graphic = javaClass.getResource("/controls/toggle/toggle_off.png").toURI()
        }
        //</editor-fold>

        // ------ PLOTS ------ //
        //<editor-fold desc="Plot Pane >>>>>>>>>>>">
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
            }

            label {
                +defaultTextStyle_std
                backgroundColor += Color.TRANSPARENT
                chartLegendSymbol {
                }
            }
        }
        chartLegendItem {
            textFill = Color.TRANSPARENT
        }

        chartLineSymbol {
            visibility = FXVisibility.COLLAPSE

        }
        //</editor-fold>
        // ========================================================================================================================================================================

    }


}
