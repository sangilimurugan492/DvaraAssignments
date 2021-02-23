package com.dvara.edairy

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import com.dvara.edairy.Data.entity.User
import java.io.*
import java.util.*

object Constants {
    public const val AppNewFolderName = "Dvara"
    private fun addImageToDB(
        profileId: String, userViewModel: UserViewModel, user: User
    ) {
        val folder = File(
            Environment.getExternalStorageDirectory(),
            AppNewFolderName
        )
        if (!folder.exists()) {
            folder.mkdirs()
        }
        user.apply {
            avatar = getFilePath(folder, profileId)
            avatarName = profileId
        }
        userViewModel.saveUserData(user)
    }

    private fun getFilePath(src: File, profileId: String): String? {
        var path: String? = null
        if (src.isDirectory) {
            val files = src.listFiles()
            for (file in files) {
                if (file.absoluteFile.name
                        .equals("$profileId.jpeg", ignoreCase = true)
                ) path = file.absolutePath
            }
        }
        return path
    }

    fun insertInBuildImage(
        image: Bitmap,
        profileId: String,
        context: Context,
        userViewModel: UserViewModel,
        user: User
    ) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val fileName = "$profileId.jpg"
                val relativeLocation =
                    Environment.DIRECTORY_PICTURES + File.separator + AppNewFolderName
                val resolver = context.contentResolver
                val contentValues = ContentValues()
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, relativeLocation)
                val imageUri =
                    resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                try {
                    val out =
                        resolver.openOutputStream(Objects.requireNonNull(imageUri)!!)
                    image.compress(Bitmap.CompressFormat.JPEG, 100, out)
                    out!!.flush()
                    out.close()
                    user.apply {
                        this.avatar = relativeLocation + File.separator + fileName
                        this.avatarName = profileId
                    }
                    userViewModel.saveUserData(user)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            } else {
                val folder = File(
                    Environment.getExternalStorageDirectory(),
                    AppNewFolderName
                )
                if (!folder.exists()) {
                    folder.mkdirs()
                }
                val targetPdf = folder.absolutePath + "/" + profileId + ".jpeg"
                val filePath = File(targetPdf)
                val fOut = FileOutputStream(filePath)
                val bo = image.compress(Bitmap.CompressFormat.JPEG, 100, fOut)
                Log.d("GalleryFragment::", "onActivityResult: $bo")
                fOut.flush()
                fOut.close()
                addImageToDB(profileId, userViewModel, user)
            }
        } catch (e: Exception) {
            Log.e("saveToInternalStorage()", e.message!!)
        }
    }

    fun populateImageInQ(
        context: Context,
        profileId: String,
        ivProfile: ImageView
    ) {
        val selection = MediaStore.Images.Media.DISPLAY_NAME + "=?"
        val selectionArgs = arrayOf("$profileId.jpg")
        val cursor = context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            null,
            selection,
            selectionArgs,
            null
        )
        if (cursor != null) {
            while (cursor.moveToNext()) {
                val id = cursor.getColumnIndex(MediaStore.Images.Media._ID)
                val longId = cursor.getLong(id)
                val imageUri =
                    ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, longId)
                var parcelFileDescriptor: ParcelFileDescriptor? = null
                var fileDescriptor: FileDescriptor? = null
                try {
                    parcelFileDescriptor =
                        context.contentResolver.openFileDescriptor(imageUri, "r")
                    if (parcelFileDescriptor != null && parcelFileDescriptor.fileDescriptor != null) {
                        fileDescriptor = parcelFileDescriptor.fileDescriptor
                        ivProfile.setImageBitmap(BitmapFactory.decodeFileDescriptor(fileDescriptor))
                    }
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                } finally {
                    if (parcelFileDescriptor != null) {
                        try {
                            parcelFileDescriptor.close()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }
    }

    fun setOutputFile(context: Context?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val resolver = context!!.contentResolver
            val relativeLocation =
                Environment.DIRECTORY_PICTURES + File.separator + AppNewFolderName
            val contentValues = ContentValues()
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, relativeLocation)
            val imageUri: Uri =
                resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)!!

        } else {
            val folder = File(Environment.getExternalStorageDirectory(), AppNewFolderName)
            if (!folder.exists()) {
                folder.mkdir()
            }
        }
    }

}