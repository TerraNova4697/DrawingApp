package com.example.drawingapp.selectbrushcolor

class SelectBrushColorPresenter(
    view: SelectBrushColorContract.View
) : SelectBrushColorContract.Presenter {

    private var view: SelectBrushColorContract.View? = view

    override fun onViewCreated() {
        view?.setCurrentBrushColor()
    }

    override fun onDestroy() {
        view = null
    }

    override fun onCancelButtonClicked() {
        view?.navigateBack()
    }

    override fun onAcceptButtonClicked() {
        view?.navigateBackWithResult()
    }
}