package cn.wthee.pcrtool.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Filter
import android.widget.Filterable
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.MainActivity
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.model.EquipmentData
import cn.wthee.pcrtool.databinding.ItemEquipmentBinding
import cn.wthee.pcrtool.utils.Constants.EQUIPMENT_URL
import cn.wthee.pcrtool.utils.Constants.WEPB
import cn.wthee.pcrtool.utils.GlideUtil


class EquipmentAdapter(private val isList: Boolean) :
    ListAdapter<EquipmentData, EquipmentAdapter.ViewHolder>(EquipDiffCallback()), Filterable {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemEquipmentBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), isList)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charString = constraint.toString()
                val filterDatas = if (charString.isEmpty()) {
                    //没有过滤的内容，则使用源数据
                    currentList
                } else {
                    val filteredList = arrayListOf<EquipmentData>()
                    try {
                        currentList.forEachIndexed { _, it ->
                            if (it.equipmentName.contains(charString)) {
                                //搜索
                                filteredList.add(it)
                            }
                        }
                    } catch (e: Exception) {
                        e.message
                    }
                    filteredList
                }
                val filterResults = FilterResults()
                filterResults.values = filterDatas
                return filterResults
            }


            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                submitList(results?.values as List<EquipmentData>)
            }
        }
    }

    class ViewHolder(private val binding: ItemEquipmentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(equip: EquipmentData, isList: Boolean) {
            //设置数据
            binding.apply {
                val ctx = MyApplication.getContext()
                content.visibility = if (isList) View.VISIBLE else {
                    itemPic.animation =
                        AnimationUtils.loadAnimation(ctx, R.anim.item_scale)
                    View.GONE
                }
                //加载动画
                content.animation =
                    AnimationUtils.loadAnimation(ctx, R.anim.item_equip_list)
                //装备名称
                name.text = equip.equipmentName
                desc.text = equip.getDesc()
                //加载装备图片
                val picUrl = EQUIPMENT_URL + equip.equipmentId + WEPB
                GlideUtil.load(picUrl, itemPic, R.drawable.error, null)
                //设置共享元素
                itemPic.transitionName = "pic_${equip.equipmentId}"
                name.transitionName = "ename_${equip.equipmentId}"
                //设置点击跳转
                root.setOnClickListener {
                    MainActivity.currentEquipPosition = adapterPosition
                    val bundle = android.os.Bundle()
                    bundle.putSerializable("equip", equip)
                    val extras =
                        FragmentNavigatorExtras(
                            itemPic to itemPic.transitionName,
                            name to name.transitionName
                        )
                    root.findNavController().navigate(
                        R.id.action_containerFragment_to_equipmentDetailsFragment,
                        bundle,
                        null,
                        extras
                    )
                }
            }
        }
    }

}

class EquipDiffCallback : DiffUtil.ItemCallback<EquipmentData>() {

    override fun areItemsTheSame(
        oldItem: EquipmentData,
        newItem: EquipmentData
    ): Boolean {
        return oldItem.equipmentId == newItem.equipmentId
    }

    override fun areContentsTheSame(
        oldItem: EquipmentData,
        newItem: EquipmentData
    ): Boolean {
        return oldItem.equals(newItem)
    }
}