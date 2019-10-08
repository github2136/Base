package com.github2136.basemvvm

import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import com.github2136.util.CrashHandler
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.DiskLogAdapter
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import java.util.*

/**
 * Created by yb on 2018/11/2.
 */
class BaseApplication : Application() {
    private val mActivitys: ArrayList<AppCompatActivity> = ArrayList()

    override fun onCreate() {
        super.onCreate()

//        logcat打印
        val prettyFormatStrategy = PrettyFormatStrategy
                .newBuilder()
                .tag("MVVM-TAG")
        Logger.addLogAdapter(AndroidLogAdapter(prettyFormatStrategy.build()))

        //log日志本地保存
//        val MAX_BYTES = 500 * 1024
//        val folder = FileUtil.getExternalStorageProjectPath(this) + File.separator + "Log"
//        val ht = HandlerThread("AndroidFileLogger.$folder")
//        ht.start()
//        val handler = DiskLog.WriteHandler(ht.looper, folder, MAX_BYTES)
//        val logStrategy = DiskLog(handler)
//
//        val prettyFormatStrategy = CsvFormatStrategy
//            .newBuilder()
//            .logStrategy(logStrategy)
//            .tag("NATURAL_RESERVE")
//        Logger.addLogAdapter(DiskLogAdapter(prettyFormatStrategy.build()))
        CrashHandler.getInstance(this, BuildConfig.DEBUG).setCallback(object : CrashHandler.CrashHandlerCallback {
            override fun finishAll() {
                this@BaseApplication.finishAll()
            }

            override fun submitLog(deviceInfo: Map<String, String>, exception: String) {

            }
        })
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
            acts.remove()
        }
    }
}