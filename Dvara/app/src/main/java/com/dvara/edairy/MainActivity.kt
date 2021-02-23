package com.dvara.edairy

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.dvara.edairy.Data.AppDatabase
import com.dvara.edairy.Data.dao.UserDao
import com.dvara.edairy.Data.entity.User
import com.dvara.edairy.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {
    private val REQUEST_CAMERA = 0
//    private val CROP_INTENT = 1
    var currentPhotoPath: String? = null
    var imageBitMap: Bitmap? = null
    private lateinit var bindings: ActivityMainBinding
    private lateinit var userViewModel: UserViewModel
    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindings = DataBindingUtil.setContentView(this, R.layout.activity_main)
        val dao:UserDao = AppDatabase.getInstance(application).userDao()
        val repository = UserRepository(dao)
        val factory = UserViewModelFactory(repository)
        userViewModel = ViewModelProvider(this, factory).get(UserViewModel::class.java)
        bindings.userViewModel = userViewModel
        bindings.lifecycleOwner = this
        Utility.checkPermission(this)

        bindings.btTakePicture.setOnClickListener{
            cameraIntent()
        }

        bindings.btSubmit.setOnClickListener{
            if(checkWhetherStringEmpty(bindings.etName) && checkWhetherStringEmpty(bindings.etMobileNumber) && imageBitMap != null) {
                val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
                val user = User(0,bindings.etName.text.toString(), bindings.etMobileNumber.text.toString(), "", "")
                Constants.insertInBuildImage(imageBitMap!!, timeStamp, this, bindings.userViewModel as UserViewModel, user)
            } else {
                Snackbar.make(bindings.btSubmit, "Please Enter all fields", Snackbar.LENGTH_LONG).show()
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id: Int = item.getItemId()
        return when (id) {
            R.id.fetch -> {
                val intent = Intent(this, FetchUserActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    private fun checkWhetherStringEmpty(editText: EditText) : Boolean {
        if (editText.text.isNullOrEmpty()) {
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE -> if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                finish()
            }
        }
    }

    private fun cameraIntent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            // Ensure that there's a camera activity to handle the intent
            if (takePictureIntent.resolveActivity(packageManager) != null) {
                // Create the File where the photo should go
                var photoFile: File? = null
                try {
                    photoFile = createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                }
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    val photoURI = FileProvider.getUriForFile(
                        this,
                        "com.dvara.edairy", photoFile
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_CAMERA)
                }
            }
        } else {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, REQUEST_CAMERA)
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File? {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir =
            getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",  /* suffix */
            storageDir /* directory */
        )

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.absolutePath
        return image
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) onCaptureImageResult(data!!)
//            if(requestCode == CROP_INTENT)
        }
    }

//    private fun performCrop(data : Intent?) {
//        if (data != null) {
//
//        }
//    }

    private fun onCaptureImageResult(data: Intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && currentPhotoPath != null) {
            imageBitMap = BitmapFactory.decodeFile(currentPhotoPath, BitmapFactory.Options())
        } else {
            imageBitMap = data.extras!!["data"] as Bitmap?
        }
    }

}