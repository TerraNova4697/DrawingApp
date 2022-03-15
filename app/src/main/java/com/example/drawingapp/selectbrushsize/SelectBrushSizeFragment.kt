package com.example.drawingapp.selectbrushsize

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.SeekBar
import androidx.fragment.app.DialogFragment
import com.example.drawingapp.R
import com.example.drawingapp.databinding.FragmentDialogSelectBrushSizeBinding

class SelectBrushSizeFragment(
    private val listener: SelectBrushSizeDialogInterface,
    private val currentBrushSize: Float
): DialogFragment(R.layout.fragment_dialog_select_brush_size), SelectBrushContract.View {

    private lateinit var binding: FragmentDialogSelectBrushSizeBinding
    internal lateinit var presenter: SelectBrushContract.Presenter

    interface SelectBrushSizeDialogInterface{
        fun onBrushSizePicked(brushSize: Int)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentDialogSelectBrushSizeBinding.bind(view)
        binding.apply {
            seekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    presenter.onSizeChanged(progress)
                }
                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })
            btnCancel.setOnClickListener { presenter.onCancelButtonClicked() }
            btnAccept.setOnClickListener { presenter.onAcceptButtonClicked(seekBar.progress) }
        }

        setPresenter(SelectBrushPresenter(this))
        presenter.onViewCreated(currentBrushSize)
    }

    override fun setCurrentBrushSizeAndSeekBar(currentBrushSize: Int) {
        binding.apply {
            seekBar.progress = currentBrushSize
            val brushSize = currentBrushSize
//            val brushSize = TypedValue.applyDimension(
//                TypedValue.COMPLEX_UNIT_DIP,
//                currentBrushSize.toFloat(),
//                resources.displayMetrics
//            )
            val params = brush.layoutParams
            params.width = brushSize
            params.height = brushSize
            brush.layoutParams = params
            tvBrushSize.text = currentBrushSize.toString()
        }
    }

    override fun navigateBackWithResult(brushSize: Int) {
        listener.onBrushSizePicked(brushSize)
        this.dismiss()
    }

    override fun navigateBack() {
        this.dismiss()
    }

    override fun setPresenter(presenter: SelectBrushContract.Presenter) {
        this.presenter = presenter
    }

    override fun onDestroy() {
        presenter.onDestroy()
        super.onDestroy()
    }

}