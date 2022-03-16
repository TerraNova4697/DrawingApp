package com.example.drawingapp.selectbrushcolor

import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import com.example.drawingapp.R
import com.example.drawingapp.databinding.FragmentDialogSelectBrushColorBinding

class SelectBrushColorDialog(
    private val listener: SelectBrushColorInterface,
    private val currentColor: Int
) : DialogFragment(R.layout.fragment_dialog_select_brush_color), SelectBrushColorContract.View {

    private lateinit var binding: FragmentDialogSelectBrushColorBinding
    internal lateinit var presenter: SelectBrushColorContract.Presenter

    interface SelectBrushColorInterface {
        fun onColorPicked(newColor: Int)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentDialogSelectBrushColorBinding.bind(view)

        binding.apply {
            colorPickerView.attachAlphaSlider(binding.alphaSlideBar)
            colorPickerView.attachBrightnessSlider(binding.brightnessSlide)
            colorPickerView.setInitialColor(currentColor)

            btnCancel.setOnClickListener { presenter.onCancelButtonClicked() }
            btnAccept.setOnClickListener { presenter.onAcceptButtonClicked() }
        }

        setPresenter(SelectBrushColorPresenter(this))
    }

    override fun setCurrentBrushColor() {
        binding.colorPickerView.setInitialColor(currentColor)
    }

    override fun navigateBack() {
        this.dismiss()
    }

    override fun navigateBackWithResult() {
        listener.onColorPicked(binding.colorPickerView.color)
        dismiss()
    }

    override fun setPresenter(presenter: SelectBrushColorContract.Presenter) {
        this.presenter = presenter
    }

}