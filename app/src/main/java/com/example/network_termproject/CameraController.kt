package com.example.network_termproject

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import android.media.Image
import android.net.Uri
import android.util.Log
import android.view.Surface
import android.view.View
import android.widget.ImageView
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import java.io.ByteArrayOutputStream
import java.io.File
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.*

class CameraController(
        private val previewView: PreviewView,
        private val lifecycleOwner: LifecycleOwner,
        private val context: Context
) {

    private var imageCapture: ImageCapture? = null
    private lateinit var cameraProvider: ProcessCameraProvider
    private lateinit var cameraSelector: CameraSelector

    fun startCamera(isFront: Boolean) {

        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()


            val preview = Preview.Builder()
                    .build()
                    .also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }

            cameraSelector = if (isFront) {
                CameraSelector.DEFAULT_FRONT_CAMERA
            } else {
                CameraSelector.DEFAULT_BACK_CAMERA
            }

            imageCapture = ImageCapture.Builder()
                    .setTargetRotation(Surface.ROTATION_0)
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                    .setFlashMode(ImageCapture.FLASH_MODE_OFF)
                    .build()

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageCapture
                )
            } catch (exc: Exception) {
                Log.e("Camera Error", "Use case binding failed")
            }
        }, ContextCompat.getMainExecutor(context))
    }

    fun takePicture(outputDirectory : File, imageView: ImageView) {
        val imageCapture = imageCapture ?: return

        val photoFile = File(
                outputDirectory,
                "snapshot.jpg"
        )

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
        imageCapture.takePicture(
                outputOptions,
                ContextCompat.getMainExecutor(context),
                object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                        Log.d("ImageSaved", "Success")
                        cameraProvider.unbindAll()
                        imageView.setImageURI(Uri.fromFile(photoFile))
                        imageView.visibility = View.VISIBLE
                    }

                    override fun onError(exception: ImageCaptureException) {
                        Log.e("ImageSaved", "Error")
                    }
                })
    }

    fun unbind(){
        cameraProvider.unbindAll()
    }

}