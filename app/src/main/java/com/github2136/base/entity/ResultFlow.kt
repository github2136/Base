package com.github2136.base.entity

/**
 * Created by YB on 2020/9/4
 */
sealed class ResultFlow<out R> {
    data class Success<out T>(val data: T) : ResultFlow<T>()
    data class Error(val code: Int, val msg: String, val e: Throwable? = null) : ResultFlow<Nothing>()
}

val <T> T.exhaustive: T
    get() = this