package com.example.drawingapp.selectbrushsize

import com.example.drawingapp.baseinterfaces.BasePresenter
import com.example.drawingapp.baseinterfaces.BaseView

class SelectBrushContract {
    interface Presenter: BasePresenter {
        fun onViewCreated(currentBrushSize: Float)
        fun onSizeChanged(progress: Int)
        fun onCancelButtonClicked()
        fun onAcceptButtonClicked(brushSize: Int)
    }
    interface View: BaseView<Presenter> {
        fun setCurrentBrushSizeAndSeekBar(currentBrushSize: Int)
        fun navigateBack()
        fun navigateBackWithResult(brushSize: Int)
    }
}