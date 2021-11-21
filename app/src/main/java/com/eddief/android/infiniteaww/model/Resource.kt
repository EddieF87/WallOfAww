package com.eddief.android.infiniteaww.model

sealed class Resource<out T: Any> {
    data class Success<out T: Any>(val data: T): Resource<T>()
    data class Error<out T: Any>(val data: T, val exception: Throwable): Resource<T>()
}