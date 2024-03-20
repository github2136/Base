package com.github2136.basemvvm

import android.content.Context
import com.github2136.util.JsonUtil
import com.github2136.util.NetworkUtil
import com.github2136.util.SPUtil
import com.orhanobut.logger.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.PrintWriter
import java.io.StringWriter

/**
 * Created by YB on 2020/7/3
 *
 */
abstract class BaseRepository(context: Context) {
    val failedStr = "无法连接服务器"

    val networkUtil by lazy { NetworkUtil.getInstance(context) }
    val jsonUtil by lazy { JsonUtil.instance }
    val spUtil by lazy { SPUtil.getSharedPreferences(context) }

    suspend fun <T> launch(block: suspend () -> ResultRepo<T>) = withContext(Dispatchers.IO) {
        try {
            block.invoke()
        } catch (e: Exception) {
            val w = StringWriter()
            e.printStackTrace(PrintWriter(w))
            Logger.t("RepositoryException").e("$w")
            return@withContext if (e is HttpException) {
                ResultRepo.Error(e.code(), failedStr, e)
            } else {
                ResultRepo.Error(0, failedStr, e)
            }
        }
    }
}