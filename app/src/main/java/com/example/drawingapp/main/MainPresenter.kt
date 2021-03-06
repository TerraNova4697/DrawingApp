package com.example.drawingapp.main

import android.graphics.Color


class MainPresenter(
    view: MainContract.View
): MainContract.Presenter {

    private var view: MainContract.View? = view

    override fun onViewCreated() {
        view?.setBrushSize(10f)
        view?.setBrushColorForImageButton(Color.BLACK)
    }

    override fun onBrushSizeButtonClicked() {
        view?.navigateToSelectBrushSizeDialog()
    }

    override fun onBrushColorButtonClicked() {
        view?.navigateToSelectBrushColorDialog()
    }

    override fun onColorPicked(newColor: Int) {
        view?.implementNewColor(newColor)
    }

    override fun onLoadImageButtonClicked() {
        view?.checkPermissionAndNavigateToGallery()
    }

    override fun onUndoButtonClicked() {
        view?.deleteLastPath()
    }

    override fun onSaveImageClicked() {
        view?.showProgressDialog()
        view?.saveImageOnDevice()
    }

    override fun onDestroy() {
        this.view = null
    }

    override fun onImageSaved() {
        view?.cancelProgressDialog()
    }
}