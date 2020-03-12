import yokiano.codecontroller.domain.ScreenOrientation
import yokiano.codecontroller.presentation.common.entities.CCGuiUnit

interface GuiPresentationDriver {
    fun addUnit(ccUnit : CCGuiUnit)
    fun launchApp(initialOrientation: ScreenOrientation)
}