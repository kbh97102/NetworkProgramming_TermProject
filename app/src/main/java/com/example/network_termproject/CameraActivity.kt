package com.example.network_termproject

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Base64
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import kotlinx.android.synthetic.main.camera_layout.*
import java.io.ByteArrayOutputStream

class CameraActivity :AppCompatActivity(){


    private lateinit var cameraController: CameraController
    private var isFront = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.camera_layout)

        cameraController = CameraController(previewView = camera_previewView, lifecycleOwner = this as LifecycleOwner, this)

        cameraController.startCamera(true)

        camera_change_button.setOnClickListener {
            cameraController.apply {
                unbind()
                isFront = !isFront
                startCamera(isFront)
            }
        }
        camera_takeButton.setOnClickListener{
            if(camera_imageView.visibility == View.INVISIBLE){
                cameraController.takePicture(camera_imageView)
            }
            else{
                camera_imageView.apply {
                    setImageBitmap(null)
                    visibility = View.INVISIBLE
                }
                cameraController.startCamera(isFront)
            }
        }
        camera_ok_button.setOnClickListener {
            if(camera_imageView.visibility == View.VISIBLE){
                val bitmap = (camera_imageView.drawable as BitmapDrawable).bitmap
                val stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 80, stream)
                val encodedData = Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT)

                val intent = Intent()
                intent.putExtra("imageData", encodedData)
                setResult(RESULT_OK, intent)
                finish()
            }
        }
    }
}