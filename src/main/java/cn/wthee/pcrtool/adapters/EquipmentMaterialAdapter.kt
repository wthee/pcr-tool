package cn.wthee.pcrtool.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.database.view.EquipmentMaterial
import cn.wthee.pcrtool.databinding.FragmentEquipmentDetailsBinding
import cn.wthee.pcrtool.databinding.ItemEquipmentMaterialBinding
import cn.wthee.pcrtool.ui.detail.equipment.EquipmentDetailsFragment
import cn.wthee.pcrtool.utils.Constants
import coil.load
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.textview.MaterialTextView
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


class EquipmentMaterialAdapter(
    private val partentBinding: FragmentEquipmentDetailsBinding,
    private val behavior: BottomSheetBehavior<View>
) :
    ListAdapter<EquipmentMaterial, EquipmentMaterialAdapter.ViewHolder>(MaterialDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemEquipmentMaterialBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.apply {
            bind(getItem(position), partentBinding, behavior)
            itemView.findViewById<MaterialTextView>(R.id.equip_name)
                .setTextColor(
                    ResourcesCompat.getColor(
                        MyApplication.getContext().resources,
                        if (position == EquipmentDetailsFragment.materialClickPosition)
                            R.color.red
                        else
                            R.color.text,
                        null
                    )
                )
        }
    }

    inner class ViewHolder(private val binding: ItemEquipmentMaterialBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            info: EquipmentMaterial,
            partentBinding: FragmentEquipmentDetailsBinding,
            behavior: BottomSheetBehavior<View>
        ) {
            binding.apply {
                equipName.text = "${info.name}"
                equipCount.text = "x ${info.count}"

                equipIcon.load(Constants.EQUIPMENT_URL + info.id + Constants.WEBP) {
                    error(R.drawable.unknow_gray)
                    placeholder(R.drawable.load_mini)
                }
                //点击查看掉落地区
                root.setOnClickListener {
                    EquipmentDetailsFragment.materialClickPosition = adapterPosition
                    notifyDataSetChanged()
                    partentBinding.progressBar.visibility = View.VISIBLE
                    //掉落地区
                    MainScope().launch {
                        val data = EquipmentDetailsFragment.viewModel.getDropInfos(info.id)
                        val adapter = EquipmentDropAdapter()
                        partentBinding.drops.adapter = adapter
                        adapter.submitList(data) {
                            behavior.state = BottomSheetBehavior.STATE_EXPANDED
                            partentBinding.progressBar.visibility = View.INVISIBLE
                        }
                        partentBinding.drops.setItemViewCacheSize(50)
                    }
                }
            }
        }
    }

}

private class MaterialDiffCallback : DiffUtil.ItemCallback<EquipmentMaterial>() {

    override fun areItemsTheSame(
        oldItem: EquipmentMaterial,
        newItem: EquipmentMaterial
    ): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: EquipmentMaterial,
        newItem: EquipmentMaterial
    ): Boolean {
        return oldItem == newItem
    }
}