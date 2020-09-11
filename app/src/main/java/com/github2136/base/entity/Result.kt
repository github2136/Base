package com.github2136.base.entity

/**
 * Created by YB on 2020/9/4
 */
sealed class Result<out R> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val code: Int, val msg: String) : Result<Nothing>()
}

val <T> T.exhaustive: T
    get() = this