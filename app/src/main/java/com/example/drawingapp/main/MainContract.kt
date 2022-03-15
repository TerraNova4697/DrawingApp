package com.example.drawingapp.main

import com.example.drawingapp.baseinterfaces.BasePresenter
import com.example.drawingapp.baseinterfaces.BaseView

class MainContract {
    interface Presenter: BasePresenter {
        fun onViewCreated()
        fun onBrushSizeButtonClicked()
        fun onBrushColorButtonClicked()
        fun onColorPicked(newColor: Int)
        fun onLoadImageButtonClicked()
        fun onUndoButtonClicked()
    }
    interface View : BaseView<Presenter> {
        fun setBrushSize(size: Float)
        fun navigateToSelectBrushSizeDialog()
        fun setBrushColorForImageButton(color: Int)
        fun navigateToSelectBrushColorDialog()
        fun implementNewColor(newColor: Int)
        fun checkPermissionAndNavigateToGallery()
        fun deleteLastPath()
    }
}