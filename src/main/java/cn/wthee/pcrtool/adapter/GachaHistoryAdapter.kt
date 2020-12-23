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
                    AnimationUtils.loadAnimation(MyApplication.context, R.anim.anim_translate_y)
                //卡池名
                gachaName.text = gacha.gacha_name
                //角色图片
                val adapter = GachaListAdapter()
                gachaIcons.adapter = adapter
                adapter.submitList(gacha.getUnits())
                //起止日期
                gachaDate.text = "${gacha.start_time.subSequence(0, 10)} ~ ${
                    gacha.end_time.subSequence(
                        0,
                        10
                    )
                }"
                //卡池描述
                gachaDesc.text = gacha.getDesc()
                if (gacha.getUnits().contains(0)) {
                    gachaDesc.visibility = View.VISIBLE
                    gachaIcons.visibility = View.GONE
                } else {
                    gachaDesc.visibility = View.GONE
                    gachaIcons.visibility = View.VISIBLE
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