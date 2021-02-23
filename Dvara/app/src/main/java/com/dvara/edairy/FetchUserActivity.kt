package com.dvara.edairy

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.dvara.edairy.Data.AppDatabase
import com.dvara.edairy.Data.dao.UserDao
import com.dvara.edairy.Data.entity.User
import com.dvara.edairy.databinding.ActivityFindUserBinding
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class FetchUserActivity : AppCompatActivity() {
    private lateinit var bindings: ActivityFindUserBinding
    private lateinit var userViewModel: UserViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindings = DataBindingUtil.setContentView(this, R.layout.activity_find_user)
        val dao: UserDao = AppDatabase.getInstance(application).userDao()
        val repository = UserRepository(dao)
        val factory = UserViewModelFactory(repository)
        userViewModel = ViewModelProvider(this, factory).get(UserViewModel::class.java)
        bindings.userViewModel = userViewModel
        bindings.lifecycleOwner = this
        Utility.checkPermission(this)

        if (supportActionBar != null) {
            supportActionBar!!.setHomeButtonEnabled(true)
        }

        bindings.btSubmit.setOnClickListener{
            if (checkWhetherStringEmpty(bindings.etMobileNumber))
            CoroutineScope(Dispatchers.IO).launch {
                val user = repository.getUserByMobile(bindings.etMobileNumber.text.toString()).value
                if (user != null) {
                    bindings.mcview.visibility = View.VISIBLE
                    bindings.tvName.text = "NAME : ${user.name}"
                    bindings.tvMobileNumber.text = "MOBILE NUMBER : ${user.mobile}"
                    populatePhoto(bindings.ivPhoto, user)
                } else {
                    Snackbar.make(bindings.btSubmit, "Wrong mobile number", Snackbar.LENGTH_LONG).show()
                }
            }
            else {
                Snackbar.make(bindings.btSubmit, "Please Enter mobile number", Snackbar.LENGTH_LONG).show()
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