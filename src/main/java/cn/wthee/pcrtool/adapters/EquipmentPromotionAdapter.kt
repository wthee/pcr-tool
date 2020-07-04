package cn.wthee.pcrtool.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.MainActivity
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.model.EquipmentData
import cn.wthee.pcrtool.databinding.ItemEquipmentPromotionBinding
import cn.wthee.pcrtool.ui.detail.equipment.EquipmentDetailsDialogFragment
import cn.wthee.pcrtool.utils.ActivityUtil
import cn.wthee.pcrtool.utils.Constants.EQUIPMENT_URL
import cn.wthee.pcrtool.utils.Constants.WEPB
import cn.wthee.pcrtool.utils.GlideUtil


class EquipmentPromotionAdapter(private val repeat: MutableMap<Int, Int>) :
    ListAdapter<EquipmentData, EquipmentPromotionAdapter.ViewHolder>(EquipDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemEquipmentPromotionBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), repeat)
        holder.inList.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                //模拟父控件的点击
                holder.itemView.performClick()
            }
            return@setOnTouchListener false
        }
    }

    class ViewHolder(private val binding: ItemEquipmentPromotionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val inList = binding.attrs
        fun bind(equip: EquipmentData, counts: MutableMap<Int, Int>) {
            //设置数据
            binding.apply {
                //装备名称
                name.text = equip.equipmentName
                count.text = MyApplication.getContext().resources.getString(
                    R.string.equip_count,
                    counts[equip.equipmentId]
                )
                //加载装备图片
                val picUrl = EQUIPMENT_URL + equip.equipmentId + WEPB
                GlideUtil.load(picUrl, itemPic, R.drawable.error, null)
                //显示星级
                val stars = arrayOf(star0, star1, star2, star3, star4)
                when (equip.promotionLevel) {
                    2 -> stars[0].visibility = View.VISIBLE
                    3 -> {
                        stars[0].visibility = View.VISIBLE
                        stars[1].visibility = View.VISIBLE
                        stars[2].visibility = View.VISIBLE
                    }
                    4, 5 -> {
                        stars.forEach {
                            it.visibility = View.VISIBLE
                        }
                    }
                }
                //属性词条
                val adapter = EquipmentAttrAdapter()
                attrs.adapter = adapter
                val lm = LinearLayoutManager(MyApplication.getContext())
                lm.orientation = LinearLayoutManager.VERTICAL
                attrs.layoutManager = lm
                adapter.submitList(equip.getAttrs())
                itemPic.transitionName = "pic1_${equip.equipmentId}"
                //设置点击跳转
                root.setOnClickListener {
                    MainActivity.currentEquipPosition = adapterPosition
//                    val bundle = android.os.Bundle()
//                    bundle.putSerializable("equip", equip)
                    EquipmentDetailsDialogFragment.getInstance(equip).show(
                        ActivityUtil.instance.currentActivity?.supportFragmentManager!!,
                        "details"
                    )
//                    binding.apply {
//                        val extras =
//                            FragmentNavigatorExtras(
//                                itemPic to itemPic.transitionName
//                            )
//                        root.findNavController().navigate(
//                            R.id.action_characterPagerFragment_to_equipmentDetailsFragment,
//                            bundle,
//                            null,
//                            extras
//                        )
//                    }
                }
            }
        }
    }

}