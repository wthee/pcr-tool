package cn.wthee.pcrtool.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.data.view.GachaInfo
import cn.wthee.pcrtool.databinding.ItemGachaBinding


class GachaHistoryAdapter(
    private val manager: FragmentManager
) :
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
                //卡池名
                gachaName.text = gacha.gacha_name
                //角色图片
                val adapter = GachaListAdapter(manager)
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
            }
        }
    }

}

private class GachaDiffCallback : DiffUtil.ItemCallback<GachaInfo>() {

    override fun areItemsTheSame(
        oldItem: GachaInfo,
        newItem: GachaInfo
    ): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: GachaInfo,
        newItem: GachaInfo
    ): Boolean {
        return oldItem.gacha_id == newItem.gacha_id
    }
}