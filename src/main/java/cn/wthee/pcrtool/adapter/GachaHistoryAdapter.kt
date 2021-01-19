package cn.wthee.pcrtool.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.GachaInfo
import cn.wthee.pcrtool.databinding.ItemGachaBinding
import cn.wthee.pcrtool.utils.intArrayList


class GachaHistoryAdapter :
    ListAdapter<GachaInfo, GachaHistoryAdapter.ViewHolder>(GachaDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemGachaBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemGachaBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(gacha: GachaInfo) {
            //设置数据
            binding.apply {
                root.animation =
                    AnimationUtils.loadAnimation(MyApplication.context, R.anim.anim_list_item)
                //卡池名
                subTitle.text = gacha.gacha_name
                //角色图片
                val adapter = IconListAdapter()
                icons.adapter = adapter
                adapter.submitList(gacha.unitIds.intArrayList())
                //起止日期
                title.text = "${gacha.start_time.subSequence(0, 10)} ~ ${
                    gacha.end_time.subSequence(
                        0,
                        10
                    )
                }"
                //卡池描述
                gachaDesc.text = gacha.getDesc()
                if (gacha.unitIds.intArrayList().contains(0)) {
                    gachaDesc.visibility = View.VISIBLE
                    icons.visibility = View.GONE
                } else {
                    gachaDesc.visibility = View.GONE
                    icons.visibility = View.VISIBLE
                }
            }
        }
    }

}

private class GachaDiffCallback : DiffUtil.ItemCallback<GachaInfo>() {

    override fun areItemsTheSame(
        oldItem: GachaInfo,
        newItem: GachaInfo
    ): Boolean {
        return oldItem.gacha_id == newItem.gacha_id
    }

    override fun areContentsTheSame(
        oldItem: GachaInfo,
        newItem: GachaInfo
    ): Boolean {
        return oldItem == newItem
    }
}