package com.example.drawingapp.main

import android.content.DialogInterface
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import com.example.drawingapp.R
import com.example.drawingapp.selectbrushcolor.SelectBrushColorDialog
import com.example.drawingapp.selectbrushsize.SelectBrushSizeFragment
import com.example.drawingapp.views.DrawingView
import com.skydoves.colorpickerview.ActionMode
import com.skydoves.colorpickerview.ColorEnvelope
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.ColorPickerView
import com.skydoves.colorpickerview.kotlin.colorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import com.skydoves.colorpickerview.listeners.ColorPickerViewListener
import com.skydoves.colorpickerview.preference.ColorPickerPreferenceManager

class MainActivity : AppCompatActivity(), MainContract.View,
SelectBrushSizeFragment.SelectBrushSizeDialogInterface, SelectBrushColorDialog.SelectBrushColorInterface{

    internal lateinit var presenter: MainContract.Presenter
    private var drawingView: DrawingView? = null
    private lateinit var brushSizeDialogButton: ImageButton
    private lateinit var brushColorDialogButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawingView = findViewById<DrawingView>(R.id.drawing_view)
        brushSizeDialogButton = findViewById(R.id.ibBrushSize)
        brushColorDialogButton = findViewById(R.id.ibBrushColor)

        setPresenter(MainPresenter(this))
        presenter.onViewCreated()

        brushSizeDialogButton.setOnClickListener { presenter.onBrushSizeButtonClicked()}
        brushColorDialogButton.setOnClickListener { presenter.onBrushColorButtonClicked() }

    }

    override fun setPresenter(presenter: MainContract.Presenter) {
        this.presenter = presenter
    }

    override fun setBrushSize(size: Float) {
        drawingView?.setSizeForBrush(size)
    }

//    fun setStrokeWidth(size: Float) {
//        drawingView?.setStrokeWidth(size)
//    }

    override fun navigateToSelectBrushSizeDialog() {
        val selectBrushSizeFragment = SelectBrushSizeFragment(this, drawingView?.getBrushSize() ?: 10f)
        selectBrushSizeFragment.show(supportFragmentManager, "")
    }

    override fun setBrushColorForImageButton(color: Int) {
        brushColorDialogButton.setColorFilter(color)
    }

    override fun navigateToSelectBrushColorDialog() {
        val colorPickerDialog = ColorPickerDialog.Builder(this)
            .setTitle("Palette")
            .setPreferenceName("MyColorPickerDialog")
            .setPositiveButton("Done", object : ColorEnvelopeListener {
                override fun onColorSelected(envelope: ColorEnvelope?, fromUser: Boolean) {
                    presenter.onColorPicked(envelope?.color ?: Color.BLACK)
                }
            })
            .setNegativeButton("Cancel") { dialog, _ -> dialog?.dismiss() }
            .attachAlphaSlideBar(true)
            .attachBrightnessSlideBar(true)
            .setBottomSpace(12)

        colorPickerDialog.colorPickerView.setInitialColor(drawingView?.getColor() ?: Color.BLACK)

        colorPickerDialog.show()
    }

    override fun implementNewColor(newColor: Int) {
        brushColorDialogButton.setColorFilter(newColor)
        drawingView?.setColor(newColor)
    }

    override fun onDestroy() {
        presenter.onDestroy()
        super.onDestroy()
    }

    override fun onBrushSizePicked(brushSize: Int) {
        drawingView?.setSizeForBrush(brushSize.toFloat())
    }

    override fun onColorPicked(newColor: Int) {
        presenter.onColorPicked(newColor)
    }
}