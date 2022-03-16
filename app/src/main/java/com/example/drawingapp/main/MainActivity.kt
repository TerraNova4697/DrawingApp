package com.example.drawingapp.main

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.drawingapp.R
import com.example.drawingapp.selectbrushcolor.SelectBrushColorDialog
import com.example.drawingapp.selectbrushsize.SelectBrushSizeFragment
import com.example.drawingapp.views.DrawingView
import com.skydoves.colorpickerview.ColorEnvelope
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception

class MainActivity : AppCompatActivity(), MainContract.View,
SelectBrushSizeFragment.SelectBrushSizeDialogInterface, SelectBrushColorDialog.SelectBrushColorInterface{

    internal lateinit var presenter: MainContract.Presenter
    private var drawingView: DrawingView? = null
    private lateinit var flDrawingView: FrameLayout
    private lateinit var brushSizeDialogButton: ImageButton
    private lateinit var brushColorDialogButton: ImageButton
    private lateinit var loadImageButton: ImageButton
    private lateinit var ibUndo: ImageButton
    private lateinit var ibSavaImage: ImageButton
    private lateinit var ivBackground: ImageView
    private val requestPermission: ActivityResultLauncher<Array<String>> = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){ permissions ->
        permissions.entries.forEach {
            val permissionName = it.key
            val isGranted = it.value

            if (isGranted) {
                val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                openGalleryLauncher.launch(pickIntent)
            } else {
                if (permissionName == Manifest.permission.READ_EXTERNAL_STORAGE) {
                    Toast.makeText(this, "You denied permission", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    private val openGalleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == RESULT_OK && it.data != null) {
            Log.d("MyTag", "Data loading")
            ivBackground.setImageURI(it.data?.data)
        }
    }
    private var customProgressDialog : Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        flDrawingView = findViewById(R.id.drawing_view_container)
        drawingView = findViewById<DrawingView>(R.id.drawing_view)
        brushSizeDialogButton = findViewById(R.id.ibBrushSize)
        brushColorDialogButton = findViewById(R.id.ibBrushColor)
        loadImageButton = findViewById(R.id.ibLoadImage)
        ivBackground = findViewById(R.id.iv_background)
        ibUndo = findViewById(R.id.ibUndo)
        ibSavaImage = findViewById(R.id.ib_image_save)
        setPresenter(MainPresenter(this))
        presenter.onViewCreated()

        brushSizeDialogButton.setOnClickListener { presenter.onBrushSizeButtonClicked() }
        brushColorDialogButton.setOnClickListener { presenter.onBrushColorButtonClicked() }
        loadImageButton.setOnClickListener { presenter.onLoadImageButtonClicked() }
        ibUndo.setOnClickListener { presenter.onUndoButtonClicked() }
        ibSavaImage.setOnClickListener { presenter.onSaveImageClicked() }

    }

    override fun setPresenter(presenter: MainContract.Presenter) {
        this.presenter = presenter
    }

    override fun setBrushSize(size: Float) {
        drawingView?.setSizeForBrush(size)
    }

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

    override fun deleteLastPath() {
        drawingView?.undoPath()
    }

    override fun saveImageOnDevice() {
        if (isReadStorageAllowed()) {
            lifecycleScope.launch {
                saveBitmapFile(getBitmapFromView(flDrawingView))
            }
        }
    }

    private fun getBitmapFromView(view: View) : Bitmap {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val bgDrawable = view.background
        if (bgDrawable != null) {
            bgDrawable.draw(canvas)
        } else {
            canvas.drawColor(Color.WHITE)
        }
        view.draw(canvas)
        return bitmap
    }

    private suspend fun saveBitmapFile(bitmap: Bitmap?): String {
        var result = ""
        withContext(Dispatchers.IO) {
            if (bitmap != null) {
                try {
                    val bytes = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.PNG, 70, bytes)
                    val file = File(externalCacheDir?.absolutePath.toString() +
                            File.separator + "DrawingApp_" + System.currentTimeMillis()/1000 + ".png")
                    val fOutputStream = FileOutputStream(file)
                    fOutputStream.write(bytes.toByteArray())
                    fOutputStream.close()
                    result = file.absolutePath
                    runOnUiThread {
                        if (result.isNotEmpty()) {
                            Toast.makeText(this@MainActivity, "File saved successfully: $result", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(this@MainActivity, "Something went wrong while saving the file", Toast.LENGTH_LONG).show()
                        }
                        presenter.onImageSaved()
                    }
                }
                catch (e: Exception) {
                    result = ""
                    e.printStackTrace()
                }
            }
        }
        return result
    }

    override fun checkPermissionAndNavigateToGallery() {
        requestStoragePermission()
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

    private fun isReadStorageAllowed(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestStoragePermission() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
        )) {
            showRationaleDialog("DrawingApp", "DrawingApp needs to access your External Storage")
        } else {
            requestPermission.launch(arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ))
        }
    }

    private fun showRationaleDialog(title: String, message: String) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle(title)
            .setMessage(message)
            .setPositiveButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    override fun showProgressDialog() {
        customProgressDialog = Dialog(this@MainActivity)
        customProgressDialog?.setContentView(R.layout.dialog_custom_progress)
        customProgressDialog?.show()
    }

    override fun cancelProgressDialog() {
        if (customProgressDialog != null) {
            customProgressDialog?.dismiss()
            customProgressDialog = null
        }
    }
}