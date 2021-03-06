package cn.wthee.pcrtool.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.view.PvpCharacterData
import cn.wthee.pcrtool.databinding.ItemCommonBinding
import cn.wthee.pcrtool.ui.tool.pvp.PvpFragment.Companion.r6Ids
import cn.wthee.pcrtool.ui.tool.pvp.PvpIconFragment.Companion.selectedAdapter
import cn.wthee.pcrtool.ui.tool.pvp.PvpIconFragment.Companion.selects
import cn.wthee.pcrtool.ui.tool.pvp.PvpService
import cn.wthee.pcrtool.utils.Constants.UNIT_ICON_URL
import cn.wthee.pcrtool.utils.Constants.WEBP
import cn.wthee.pcrtool.utils.ResourcesUtil
import cn.wthee.pcrtool.utils.ToastUtil
import coil.Coil
import coil.request.ImageRequest
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

/**
 * 竞技场角色列表适配器，[floatWindow] 判断是否为悬浮窗
 *
 * 列表项布局 [ItemCommonBinding]
 *
 * 列表项数据 [PvpCharacterData]
 */
class PvpIconAdapter(
    private val floatWindow: Boolean
) : ListAdapter<PvpCharacterData, PvpIconAdapter.ViewHolder>(PvpDiffCallback()) {

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
        val item = getItem(position)
        holder.bind(item)
    }

    inner class ViewHolder(private val binding: ItemCommonBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: PvpCharacterData) {
            //设置数据
            binding.apply {
                if (floatWindow) {
                    name.visibility = View.GONE
                    val params = pic.layoutParams as LinearLayout.LayoutParams
                    params.width = RecyclerView.LayoutParams.WRAP_CONTENT
                    params.height = RecyclerView.LayoutParams.WRAP_CONTENT
                    pic.layoutParams = params
                }
                //名称
                name.text = if (data.position == 999) "未选择" else data.position.toString()

                //加载图片
                var id = data.unitId
                id += if (r6Ids.contains(id)) 60 else 30
                val picUrl = UNIT_ICON_URL + id + WEBP
                if (data.unitId == 0) {
                    //默认
                    val drawable = ResourcesUtil.getDrawable(R.drawable.unknown_gray)
                    pic.setImageDrawable(drawable)
                } else {
                    val coil = Coil.imageLoader(MyApplication.context)
                    val request = ImageRequest.Builder(MyApplication.context)
                        .data(picUrl)
                        .placeholder(R.drawable.unknown_gray)
                        .error(R.drawable.unknown_gray)
                        .build()
                    MainScope().launch {
                        pic.setImageDrawable(coil.execute(request).drawable)
                    }
                }

                //设置点击事件
                root.setOnClickListener {
                    selects.apply {
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
                            selectedAdapter.apply {
                                submitList(selects) {
                                    notifyDataSetChanged()
                                }
                            }
                        } catch (e: Exception) {

                        }
                        //更新悬浮窗列表
                        try {
                            PvpService.selectedAdapter.apply {
                                submitList(selects) {
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
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: PvpCharacterData,
        newItem: PvpCharacterData
    ): Boolean {
        return oldItem.unitId == newItem.unitId
    }
}
