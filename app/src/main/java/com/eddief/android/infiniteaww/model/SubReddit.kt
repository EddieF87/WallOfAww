package com.eddief.android.infiniteaww.model

import androidx.annotation.IdRes

data class SubReddit(@IdRes val pref: Int, val sub: String, val default: Boolean = false)