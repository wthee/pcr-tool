package cn.wthee.pcrtool.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.model.EquipmentDropInfo
import cn.wthee.pcrtool.databinding.ItemEquipmentDropBinding
import com.bumptech.glide.Glide


class EquipmentDropAdapter :
    ListAdapter<EquipmentDropInfo, EquipmentDropAdapter.ViewHolder>(DropDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemEquipmentDropBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(private val binding: ItemEquipmentDropBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(info: EquipmentDropInfo) {
            binding.apply {
                quest.text = info.getName()
                //地图难度
                questNum.text =  when(info.areaId / 1000){
                    11 -> "N"
                    12 -> "H"
                    13 -> "VH"
                    else-> ""
                } + "-" +  info.getNum()
                //颜色
                val color = when(info.areaId / 1000){
                    11 -> R.color.color_map_n
                    12 ->  R.color.color_map_h
                    13 ->  R.color.color_map_vh
                    else-> R.color.color_map_n
                }
                questNum.setTextColor(ResourcesCompat.getColor(MyApplication.getContext().resources, color, null))
                val adapter = EquipmentDropDetailAdapter(info.eid)
                drops.adapter = adapter
                adapter.submitList(info.odds)
                drops.setItemViewCacheSize(20)
                //滑动时暂停glide加载
                drops.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
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

private class DropDiffCallback : DiffUtil.ItemCallback<EquipmentDropInfo>() {

    override fun areItemsTheSame(
        oldItem: EquipmentDropInfo,
        newItem: EquipmentDropInfo
    ): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: EquipmentDropInfo,
        newItem: EquipmentDropInfo
    ): Boolean {
        return oldItem == newItem
    }
}