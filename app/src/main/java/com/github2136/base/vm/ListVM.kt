package com.github2136.base.vm

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.github2136.base.adapter.ListAdapter
import com.github2136.base.adapter.ListMultipleAdapter
import com.github2136.base.entity.User
import com.github2136.base.executor
import com.github2136.basemvvm.BaseVM
import java.util.*

/**
 * Created by YB on 2019/9/23
 */
class ListVM(app: Application) : BaseVM(app) {
    val adapterLD = MutableLiveData<ListMultipleAdapter>()

    fun setData() {
        executor.submit {
            val data = mutableListOf<User>()
            val r = Random().nextInt()
            for (i in 0 until 10) {
                data.add(User("name $i $r", "$r"))
            }
            Thread.sleep(1000)
            handle.post {
                adapterLD.value = ListMultipleAdapter(data)
            }
        }
    }
}