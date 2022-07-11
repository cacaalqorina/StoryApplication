package com.annisaalqorina.submissionstory.main

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.annisaalqorina.submissionstory.R
import com.annisaalqorina.submissionstory.api.ApiConfig
import com.annisaalqorina.submissionstory.databinding.ActivityAddStoryBinding
import com.annisaalqorina.submissionstory.reduceFileImage
import com.annisaalqorina.submissionstory.response.FileUploadResponse
import com.annisaalqorina.submissionstory.rotateBitmap
import com.annisaalqorina.submissionstory.uriToFile
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class AddStoryActivity : AppCompatActivity() {

    companion object {
        const val CAMERA_X_RESULT = 200

        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }

    private lateinit var activityAddStoryBinding: ActivityAddStoryBinding

    private var token = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityAddStoryBinding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(activityAddStoryBinding.root)

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        token = intent.getStringExtra(MainActivity.STR_TOKEN).toString()
        Log.d(ContentValues.TAG, "onCreate: $token")

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = resources.getString(R.string.add_story)

        setClick()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun setClick() {
        activityAddStoryBinding.apply {
            btnCameraX.setOnClickListener { startCameraX() }
            btnAlbum.setOnClickListener { startGallery() }
            btnPost.setOnClickListener {
                if (etDesc.text.isNotEmpty()) {
                    uploadImage()
                }
            }
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = ACTION_GET_CONTENT
        intent.type = "image/*"

        val chooser = Intent.createChooser(intent, resources.getString(R.string.choose_a_image))
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, this)

            getFile = myFile

            activityAddStoryBinding.viewImage.setImageURI(selectedImg)
        }
    }

    private fun startCameraX() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private var getFile: File? = null
    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile = it.data?.getSerializableExtra("picture") as File
            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean

            getFile = myFile
            val result = rotateBitmap(
                BitmapFactory.decodeFile(getFile?.path),
                isBackCamera
            )

            activityAddStoryBinding.viewImage.setImageBitmap(result)
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun uploadImage() {
        activityAddStoryBinding.progressBar.visibility = View.VISIBLE
        if (getFile != null) {
            val file = reduceFileImage(getFile as File)

            val description = activityAddStoryBinding.etDesc.text.toString().toRequestBody("text/plain".toMediaType())
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile
            )

            val service = ApiConfig.getMyApi().addStory(("Bearer $token"), imageMultipart, description)

            service.enqueue(object : Callback<FileUploadResponse> {
                override fun onResponse(
                    call: Call<FileUploadResponse>,
                    response: Response<FileUploadResponse>
                ) {
                    activityAddStoryBinding.progressBar.visibility = View.GONE
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody != null && !responseBody.error) {
                            Toast.makeText(
                                this@AddStoryActivity,
                                responseBody.message,
                                Toast.LENGTH_LONG
                            ).show()
                            setResult(MainActivity.IS_SUCCESS, intent)
                            finish()
                        }
                    } else {
                        Toast.makeText(
                            this@AddStoryActivity,
                            response.message(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }

                override fun onFailure(call: Call<FileUploadResponse>, t: Throwable) {
                    activityAddStoryBinding.progressBar.visibility = View.GONE
                    Toast.makeText(
                        this@AddStoryActivity,
                        resources.getString(R.string.failed_instance),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        } else {
            activityAddStoryBinding.progressBar.visibility = View.GONE
            Toast.makeText(
                this@AddStoryActivity,
                resources.getString(R.string.please_to_choose_image),
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}