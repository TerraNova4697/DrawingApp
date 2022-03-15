package com.example.drawingapp.selectbrushsize

class SelectBrushPresenter(
    view: SelectBrushContract.View
): SelectBrushContract.Presenter {

    private var view: SelectBrushContract.View? = view

    override fun onViewCreated(currentBrushSize: Float) {
        view?.setCurrentBrushSizeAndSeekBar(currentBrushSize.toInt())
    }

    override fun onDestroy() {
        view = null
    }

    override fun onSizeChanged(progress: Int) {
        view?.setCurrentBrushSizeAndSeekBar(progress)
    }

    override fun onAcceptButtonClicked(brushSize: Int) {
        view?.navigateBackWithResult(brushSize)
    }

    override fun onCancelButtonClicked() {
        view?.navigateBack()
    }

}