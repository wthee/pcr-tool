package cn.wthee.pcrtool.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.database.view.GachaInfo
import cn.wthee.pcrtool.databinding.ItemGachaBinding
import cn.wthee.pcrtool.enums.PageType
import cn.wthee.pcrtool.ui.common.CommonBottomSheetFragment
import coil.load


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

        fun bind(gacha: GachaInfo) {
            //设置数据
            binding.apply {
                //卡池名
                gachaName.text = gacha.gacha_name
                //角色图片
                gachaPic0.load(gacha.getAllUrls()[0]) {
                    placeholder(R.drawable.load)
                    error(R.drawable.error)
                }
                if (gacha.getAllUrls().size > 1) {
                    gachaPic1.visibility = View.VISIBLE
                    gachaPic1.load(gacha.getAllUrls()[1]) {
                        placeholder(R.drawable.load)
                        error(R.drawable.error)
                    }
                }
                //角色名
                gachaTitle0.text = gacha.getNames()[0]
                if (gacha.getNames().size > 1) {
                    gachaTitle1.visibility = View.VISIBLE
                    gachaTitle1.text = gacha.getNames()[1]
                }
                //起止日期
                gachaDate.text = "- ${gacha.start_time.subSequence(0, 10)} ~ ${
                    gacha.end_time.subSequence(
                        0,
                        10
                    )
                }"
                //卡池描述
                gachaDesc.text = gacha.getDesc()
                //点击查看角色技能
                gachaPic0.setOnClickListener {
                    CommonBottomSheetFragment(gacha.getIds()[0], PageType.CAHRACTER_SKILL).show(
                        manager,
                        "skill"
                    )
                }
                gachaPic1.setOnClickListener {
                    CommonBottomSheetFragment(gacha.getIds()[1], PageType.CAHRACTER_SKILL).show(
                        manager,
                        "skill"
                    )
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
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: GachaInfo,
        newItem: GachaInfo
    ): Boolean {
        return oldItem.gacha_id == newItem.gacha_id
    }
}