package cn.wthee.pcrtool.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.database.view.PvpCharacterData
import cn.wthee.pcrtool.databinding.ItemCommonBinding
import cn.wthee.pcrtool.ui.main.CharacterListFragment.Companion.r6Ids
import cn.wthee.pcrtool.ui.tool.pvp.ToolPvpFragment
import cn.wthee.pcrtool.ui.tool.pvp.ToolPvpService
import cn.wthee.pcrtool.utils.Constants.UNIT_ICON_URL
import cn.wthee.pcrtool.utils.Constants.WEBP
import cn.wthee.pcrtool.utils.ResourcesUtil
import cn.wthee.pcrtool.utils.ToastUtil
import coil.Coil
import coil.request.ImageRequest
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


class PvpCharacterAdapter(
    private val isFloatWindow: Boolean,
    private val activity: Activity
) :
    ListAdapter<PvpCharacterData, PvpCharacterAdapter.ViewHolder>(PvpDiffCallback()) {
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
        holder.bind(getItem(position)!!)
    }

    inner class ViewHolder(private val binding: ItemCommonBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: PvpCharacterData) {
            //设置数据
            binding.apply {
                if (isFloatWindow) {
                    name.visibility = View.GONE
                }
                //名称
                name.text = if (data.position == 999) "未选择" else data.position.toString()
                //加载图片
                if (data.unitId == 0) {
                    //默认
                    val drawable = ResourcesUtil.getDrawable(R.drawable.unknow_gray)
                    pic.setImageDrawable(drawable)
                } else {
                    //角色
                    var id = data.unitId
                    id += if (r6Ids.contains(id)) 60 else 30
                    val picUrl = UNIT_ICON_URL + id + WEBP
                    val coil = Coil.imageLoader(activity.applicationContext)
                    val request = ImageRequest.Builder(activity.applicationContext)
                        .data(picUrl)
                        .build()
                    MainScope().launch {
                        pic.setImageDrawable(coil.execute(request).drawable)
                    }
                }
                //设置点击事件
                root.setOnClickListener {
                    ToolPvpFragment.selects.apply {
                        val empty =
                            PvpCharacterData(
                                0,
                                999
                            )
                        //选择完毕
                        if (size == 5 && !contains(empty) && !contains(data)) {
                            ToastUtil.short("已选择五名角色，无法继续添加！")
                            return@setOnClickListener
                        }
                        //点击选择新角色
                        if (!contains(data)) {
                            //添加角色
                            add(data)
                            //移除待定角色
                            if (contains(empty)) {
                                remove(empty)
                            }
                        } else {
                            //已选择，再次点击则移除角色
                            remove(data)
                            add(empty)
                        }
                        //按位置排序
                        sortByDescending { it.position }
                        //更新列表
                        try {
                            ToolPvpFragment.selectedAdapter.apply {
                                submitList(ToolPvpFragment.selects) {
                                    notifyDataSetChanged()
                                }
                            }
                        } catch (e: Exception) {

                        }
                        try {
                            ToolPvpService.selectedAdapter.apply {
                                submitList(ToolPvpFragment.selects) {
                                    notifyDataSetChanged()
                                }
                            }
                        } catch (e: Exception) {

                        }

                    }
                }
            }
        }
    }
}

class PvpDiffCallback : DiffUtil.ItemCallback<PvpCharacterData>() {

    override fun areItemsTheSame(
        oldItem: PvpCharacterData,
        newItem: PvpCharacterData
    ): Boolean {
        return oldItem.unitId == newItem.unitId
    }

    override fun areContentsTheSame(
        oldItem: PvpCharacterData,
        newItem: PvpCharacterData
    ): Boolean {
        return oldItem.unitId == newItem.unitId
    }
}
