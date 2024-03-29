package com.github2136.base.view.activity.loadmore

import com.github2136.base.R
import com.github2136.base.ViewHolderRecyclerView
import com.github2136.base.databinding.ItemUserBinding
import com.github2136.base.divider.IDivider
import com.github2136.base.model.entity.User
import com.github2136.basemvvm.loadmore.BaseLoadMoreAdapter

/**
 * Created by YB on 2019/9/20
 */
class LoadMoreAdapter : BaseLoadMoreAdapter<User, ItemUserBinding>()
    , IDivider {
    override fun getLayoutIdByList(viewType: Int) = R.layout.item_user

    override fun onBindView(t: User, bind: ItemUserBinding, holder: ViewHolderRecyclerView, position: Int) {
        bind.user = t
        bind.executePendingBindings()
    }

    override fun getShowTxt(position: Int): String? {
        val obj = getItem(position)
        return if (obj is User) {
            obj.xxx
        } else {
            null
        }
    }
}