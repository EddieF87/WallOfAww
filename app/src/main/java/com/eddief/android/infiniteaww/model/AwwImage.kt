package com.eddief.android.infiniteaww.model

import android.os.Parcelable
import androidx.room.*
import kotlinx.parcelize.Parcelize

@Entity(tableName = "aww_table")
@Parcelize
class AwwImage(
    @PrimaryKey
    val primaryUrl: String,
    val fallbackUrl: String = "",
    val thumbnail: String,
    val link: String,
    val title: String = "",
    val isVideo: Boolean,
    val name: String?,
) : Parcelable