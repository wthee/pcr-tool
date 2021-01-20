package cn.wthee.pcrtool.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.databinding.ItemCommonBinding
import cn.wthee.pcrtool.utils.Constants
import coil.load

/**
 * 角色图标列表适配器
 *
 * 列表项布局 [ItemCommonBinding]
 *
 * 列表项数据 [Int] unit_id
 */
class IconListAdapter() : ListAdapter<Int, IconListAdapter.ViewHolder>(GachaListDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemCommonBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemCommonBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(uid: Int) {
            //设置数据
            binding.apply {
                val itemParams = root.layoutParams
                itemParams.width = RecyclerView.LayoutParams.WRAP_CONTENT
                root.layoutParams = itemParams
                //角色图片
                var picId = 0
                var pagerId = 0
                if (uid / 10000 == 3) {
                    //item 转 unit
                    picId = uid % 10000 * 100 + 11
                    pagerId = uid % 10000 * 100 + 1
                } else {
                    picId = uid + 30
                    pagerId = uid
                }

                val picUrl = Constants.UNIT_ICON_URL + picId + Constants.WEBP
                pic.load(picUrl) {
                    placeholder(R.drawable.unknown_gray)
                    error(R.drawable.unknown_gray)
                }
                //角色名
                name.visibility = View.GONE
                pic.setOnClickListener {
                    val bundle = Bundle()
                    bundle.putInt(Constants.UID, pagerId)
                    root.findNavController().navigate(
                        R.id.action_global_characterPagerFragment,
                        bundle,
                        null,
                        null
                    )
                }
            }
        }
    }

}

private class GachaListDiffCallback : DiffUtil.ItemCallback<Int>() {

    override fun areItemsTheSame(
        oldItem: Int,
        newItem: Int
    ): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: Int,
        newItem: Int
    ): Boolean {
        return oldItem == newItem
    }
}