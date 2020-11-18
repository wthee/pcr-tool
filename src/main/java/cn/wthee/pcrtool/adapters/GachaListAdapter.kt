package cn.wthee.pcrtool.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.databinding.ItemCommonBinding
import cn.wthee.pcrtool.enums.PageType
import cn.wthee.pcrtool.ui.common.CommonBottomSheetFragment
import cn.wthee.pcrtool.utils.Constants
import coil.load


class GachaListAdapter(
    private val manager: FragmentManager
) : ListAdapter<UnitData, GachaListAdapter.ViewHolder>(GachaListDiffCallback()) {
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
        fun bind(data: UnitData) {
            //设置数据
            binding.apply {
                val itemParams = root.layoutParams
                itemParams.width = RecyclerView.LayoutParams.WRAP_CONTENT
                root.layoutParams = itemParams
                //角色图片
                val picUrl = Constants.UNIT_ICON_URL + (data.id + 30) + Constants.WEBP
                pic.load(picUrl) {
                    placeholder(R.drawable.unknow_gray)
                    error(R.drawable.error)
                }
                //角色名
                name.visibility = View.GONE
//                name.text = data.name
//                name.setTextColor(ResourcesUtil.getColor(R.color.colorPrimary))
                pic.setOnClickListener {
                    CommonBottomSheetFragment.getInstance(data.id, PageType.CAHRACTER_SKILL).show(
                        manager,
                        "skill"
                    )
                }
            }
        }
    }

}

private class GachaListDiffCallback : DiffUtil.ItemCallback<UnitData>() {

    override fun areItemsTheSame(
        oldItem: UnitData,
        newItem: UnitData
    ): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: UnitData,
        newItem: UnitData
    ): Boolean {
        return oldItem.id == newItem.id
    }
}

class UnitData(
    val id: Int,
    val name: String
)