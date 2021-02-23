package com.dvara.edairy

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.dvara.edairy.Data.AppDatabase
import com.dvara.edairy.Data.dao.UserDao
import com.dvara.edairy.Data.entity.User
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class FetchUserActivity : AppCompatActivity() {
//    private lateinit var bindings: ActivityFindUserBinding
    private lateinit var userViewModel: UserViewModel
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_user)
        val dao: UserDao = AppDatabase.getInstance(application).userDao()
        val repository = UserRepository(dao)
        val factory = UserViewModelFactory(repository)
        userViewModel = ViewModelProvider(this, factory).get(UserViewModel::class.java)
        Utility.checkPermission(this)

        if (supportActionBar != null) {
            supportActionBar!!.setHomeButtonEnabled(true)
        }

        val etMobile = findViewById<EditText>(R.id.et_mobile_number)
        val mcView = findViewById<MaterialCardView>(R.id.mcview)
        val tvName = findViewById<TextView>(R.id.tv_name)
        val tvMobile = findViewById<TextView>(R.id.tv_mobile_number)
        val ivPhoto = findViewById<ImageView>(R.id.iv_photo)
        val btSubmit = findViewById<MaterialButton>(R.id.bt_submit)

        btSubmit.setOnClickListener{
            if (checkWhetherStringEmpty(etMobile))
            CoroutineScope(Dispatchers.IO).launch {
                val user = repository.getUserByMobile(etMobile.text.toString())
                if (user != null) {
                    mcView.visibility = View.VISIBLE
                    tvName.text = "NAME : ${user.name}"
                    tvMobile.text = "MOBILE NUMBER : ${user.mobile}"
                    populatePhoto(ivPhoto, user)
                } else {
                    Snackbar.make(tvName, "Wrong mobile number", Snackbar.LENGTH_LONG).show()
                }
            }
            else {
                Snackbar.make(tvName, "Please Enter mobile number", Snackbar.LENGTH_LONG).show()
            }
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id: Int = item.getItemId()
        return when (id) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun populatePhoto(ivProfile : ImageView, user : User) {
        val folderIPath: String
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Constants.populateImageInQ(this, user.avatarName!!, ivProfile)
        } else {
            val folder = File(
                Environment.getExternalStorageDirectory(),
                Constants.AppNewFolderName
            )
            folderIPath = folder.absolutePath + "/" + user.avatarName + ".jpg"
            Picasso.with(this).load(File(folderIPath)).into(ivProfile, object : Callback {
                override fun onSuccess() {
                    Log.d("ImageViewAdapter::", "onSuccess: ")
                }

                override fun onError() {
                    Log.d("ImageViewAdapter::", "onError: ")
                }
            })
        }
    }


    private fun checkWhetherStringEmpty(editText: EditText) : Boolean {
        if (editText.text.isNullOrEmpty()) {
            return false
        }
        return true
    }

}