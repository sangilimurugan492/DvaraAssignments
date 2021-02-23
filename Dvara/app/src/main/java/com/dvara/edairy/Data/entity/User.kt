package com.dvara.edairy.Data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "user")
data class User(
    @PrimaryKey (autoGenerate = true) val id: Int,
    @ColumnInfo(name = "name") val name: String?,
    @ColumnInfo(name = "mobile") val mobile: String?,
    @ColumnInfo(name = "avatar") var avatar: String?,
    @ColumnInfo(name = "avatarName") var avatarName: String?
)
