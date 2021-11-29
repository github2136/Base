package com.github2136.basemvvm

/**
 * Created by YB on 2020/9/4
 */
sealed class ResultRepo<out T> {
    data class Success<T>(val data: T) : ResultRepo<T>()
    data class Error(val code: Int, val msg: String, val e: Throwable? = null) : ResultRepo<Nothing>()
    data class Unauthorized(val code: Int, val msg: String, val e: Throwable? = null) : ResultRepo<Nothing>()
}