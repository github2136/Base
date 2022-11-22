package com.github2136.basemvvm

import android.app.Application
import android.os.HandlerThread
import androidx.appcompat.app.AppCompatActivity
import com.github2136.util.FileUtil
import com.orhanobut.logger.*
import java.io.File

/**
 * Created by yb on 2018/11/2.
 */
open class BaseApplication : Application() {
    val activitys: ArrayList<AppCompatActivity> = ArrayList()
    open val logEnable = true
    open val saveFileEnable = false
    override fun onCreate() {
        super.onCreate()
        // logcat打印
        val prettyFormatStrategy = ChinesePrettyFormatStrategy
            .newBuilder()
            .tag("MVVM-TAG")
        Logger.addLogAdapter(object : AndroidLogAdapter(prettyFormatStrategy.build()) {
            override fun isLoggable(priority: Int, tag: String?): Boolean {
                return logEnable
            }
        })
        if (saveFileEnable) {
            setFileLog()
        }
    }

    /**
     * 日志文件保存
     */
    fun setFileLog() {
        //log日志本地保存
        val MAX_BYTES = 500 * 1024
        val folder = FileUtil.getExternalStorageProjectPath(this) + File.separator + "Log"
        val ht = HandlerThread("AndroidFileLogger.$folder")
        ht.start()
        val handler = DiskLog.WriteHandler(ht.looper, folder, MAX_BYTES)
        val logStrategy = DiskLog(handler)

        val fileFormatStrategy = CsvFormatStrategy
            .newBuilder()
            .logStrategy(logStrategy)
            .tag("FILE_LOG")
        Logger.addLogAdapter(DiskLogAdapter(fileFormatStrategy.build()))
    }

    fun addActivity(act: AppCompatActivity) {
        this.activitys.add(act)
    }

    fun removeActivity(act: AppCompatActivity) {
        if (activitys.contains(act)) {
            activitys.remove(act)
        }
    }

    fun finishAll() {
        val acts = activitys.iterator()
        while (acts.hasNext()) {
            val act = acts.next()
            if (!act.isFinishing) {
                act.finish()
            }
        }
    }
}