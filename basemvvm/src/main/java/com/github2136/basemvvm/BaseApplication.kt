package com.github2136.basemvvm

import android.app.Application
import android.os.HandlerThread
import androidx.appcompat.app.AppCompatActivity
import com.github2136.util.CrashHandler
import com.github2136.util.FileUtil
import com.orhanobut.logger.*
import java.io.File
import java.util.*

/**
 * Created by yb on 2018/11/2.
 */
open class BaseApplication : Application() {
    private val mActivitys: ArrayList<AppCompatActivity> = ArrayList()

    override fun onCreate() {
        super.onCreate()

//        logcat打印
        val prettyFormatStrategy = PrettyFormatStrategy
            .newBuilder()
            .tag("MVVM-TAG")
        Logger.addLogAdapter(AndroidLogAdapter(prettyFormatStrategy.build()))

        CrashHandler.getInstance(this, BuildConfig.DEBUG).setCallback(object : CrashHandler.CrashHandlerCallback {
            override fun finishAll() {
                this@BaseApplication.finishAll()
            }

            override fun submitLog(deviceInfo: Map<String, String>, exception: String) {

            }
        })
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
        this.mActivitys.add(act)
    }

    fun removeActivity(act: AppCompatActivity) {
        if (mActivitys.contains(act)) {
            mActivitys.remove(act)
        }
    }

    fun finishAll() {
        val acts = mActivitys.iterator()
        while (acts.hasNext()) {
            val act = acts.next()
            if (!act.isFinishing) {
                act.finish()
            }
        }
    }
}