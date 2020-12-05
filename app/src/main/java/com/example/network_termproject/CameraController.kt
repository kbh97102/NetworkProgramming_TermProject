package com.example.network_termproject

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import java.io.ByteArrayOutputStream
import java.io.File
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

    fun startCamera(isFront : Boolean) {

        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()


            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

            if (isFront){
                cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
            }else{
                cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            }
            imageCapture = ImageCapture.Builder().build()
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

    fun takePicture(imageView: ImageView) {
        val imageCapture = imageCapture ?: return

        imageCapture.takePicture(ContextCompat.getMainExecutor(context), object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                cameraProvider.unbindAll()
                val yBuffer = image.planes[0].buffer // Y
                val vuBuffer = image.planes[2].buffer // VU

                val ySize = yBuffer.remaining()
                val vuSize = vuBuffer.remaining()

                val nv21 = ByteArray(ySize + vuSize)

                yBuffer.get(nv21, 0, ySize)
                vuBuffer.get(nv21, ySize, vuSize)

                val yuvImage = YuvImage(nv21, ImageFormat.NV21, image.width, image.height, null)
                val out = ByteArrayOutputStream()
                yuvImage.compressToJpeg(Rect(0, 0, yuvImage.width, yuvImage.height), 50, out)
                val imageBytes = out.toByteArray()
                val bitmap =  BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                imageView.apply {
                    setImageBitmap(bitmap)
                    visibility = View.VISIBLE
                }
            }
        })
    }

    fun unbind(){
        cameraProvider.unbindAll()
    }

}