package com.github2136.base.model.entity

/**
 * Created by YB on 2020/9/4
 */
sealed class ResultRepo<out R> {
    data class Success<T>(val data: T) : ResultRepo<T>()
    data class Error(val code: Int, val msg: String, val e: Throwable? = null) : ResultRepo<Nothing>()
}