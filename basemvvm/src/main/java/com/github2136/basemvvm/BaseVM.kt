package com.github2136.basemvvm

import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.github2136.util.JsonUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Created by YB on 2019/8/29
 */
abstract class BaseVM(app: Application) : AndroidViewModel(app) {
    protected val TAG = this.javaClass.name
    val mJsonUtil by lazy { JsonUtil.instance }

    // protected val jobs = mutableMapOf<UUID, Job>()
    val loadingStr = "请稍后……"

    //显示dialog
    val dialogLD = MutableLiveData<DialogData>()
    val toastLD = MutableLiveData<String>()
    val titleTextLD = MutableLiveData<String>()
    val rightBtnLD = MutableLiveData<String>()
    val handle = Handler(Looper.getMainLooper())

    fun launch(block: suspend (coroutine: CoroutineScope) -> Unit) {
        // val uuid = UUID.randomUUID()
        val job = viewModelScope.launch {
            block.invoke(this)
            // jobs.remove(uuid)
        }
        // jobs[uuid] = job
    }

    //取消请求
    open fun cancelRequest() {
        // viewModelScope.launch {
        //     for (job in jobs) {
        //         job.value.cancelAndJoin()
        //     }
        //     jobs.clear()
        // }
    }

    data class DialogData(var msg: String, var cancelable: Boolean = false, var canceledOnTouchOutside: Boolean = false)
}