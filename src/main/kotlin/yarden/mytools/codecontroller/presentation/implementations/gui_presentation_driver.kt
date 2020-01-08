import yarden.mytools.codecontroller.presentation.common.entities.CCGuiUnit

interface GuiPresentationDriver {
    fun addUnit(ccUnit : CCGuiUnit)
    fun launchApp()
}