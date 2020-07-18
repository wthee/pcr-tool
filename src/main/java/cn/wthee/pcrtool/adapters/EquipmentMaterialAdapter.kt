package cn.wthee.pcrtool.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.model.EquipmentMaterial
import cn.wthee.pcrtool.databinding.ItemEquipmentMaterialBinding
import cn.wthee.pcrtool.ui.detail.equipment.EquipmentDropDialogFragment
import cn.wthee.pcrtool.utils.ActivityUtil
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.GlideUtil


class EquipmentMaterialAdapter() :
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
        holder.bind(getItem(position))
    }

    class ViewHolder(private val binding: ItemEquipmentMaterialBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(info: EquipmentMaterial) {
            binding.apply {
                root.animation =
                    AnimationUtils.loadAnimation(MyApplication.getContext(), R.anim.anim_scale)
                equipName.text = "${info.name}"
                equipCount.text = "x ${info.count}"
                GlideUtil.load(
                    Constants.EQUIPMENT_URL + info.id + Constants.WEBP,
                    equipIcon,
                    R.drawable.error,
                    null
                )
                //点击查看掉落地区
                root.setOnClickListener {
                    EquipmentDropDialogFragment.getInstance(info.id)
                        .show(
                            ActivityUtil.instance.currentActivity?.supportFragmentManager!!,
                            "drop"
                        )
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