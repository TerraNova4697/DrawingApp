package com.example.drawingapp.selectbrushcolor

import com.example.drawingapp.baseinterfaces.BasePresenter
import com.example.drawingapp.baseinterfaces.BaseView

class SelectBrushColorContract {
    interface Presenter : BasePresenter {
        fun onViewCreated()
        fun onCancelButtonClicked()
        fun onAcceptButtonClicked()
    }
    interface View : BaseView<Presenter> {
        fun setCurrentBrushColor()
        fun navigateBack()
        fun navigateBackWithResult()
    }
}