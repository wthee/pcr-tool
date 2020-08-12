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
import cn.wthee.pcrtool.databinding.FragmentEquipmentDetailsBinding
import cn.wthee.pcrtool.databinding.ItemEquipmentMaterialBinding
import cn.wthee.pcrtool.ui.detail.equipment.EquipmentDetailsFragment
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.GlideUtil
import com.bumptech.glide.Glide
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


class EquipmentMaterialAdapter(private val partentBinding: FragmentEquipmentDetailsBinding) :
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
        holder.bind(getItem(position), partentBinding)
    }

    class ViewHolder(private val binding: ItemEquipmentMaterialBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(info: EquipmentMaterial, partentBinding: FragmentEquipmentDetailsBinding) {
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
                    //掉落地区
                    MainScope().launch {
                        val data = EquipmentDetailsFragment.viewModel.getDropInfos(info.id)
                        val adapter = EquipmentDropAdapter()
                        partentBinding.drops.adapter = adapter
                        adapter.submitList(data)
                        partentBinding.drops.setItemViewCacheSize(50)
                        //滑动时暂停glide加载
                        partentBinding.drops.addOnScrollListener(object :
                            RecyclerView.OnScrollListener() {
                            override fun onScrollStateChanged(
                                recyclerView: RecyclerView,
                                newState: Int
                            ) {
                                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                                    Glide.with(root.context).resumeRequests()
                                } else {
                                    Glide.with(root.context).pauseRequests()
                                }
                            }
                        })
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