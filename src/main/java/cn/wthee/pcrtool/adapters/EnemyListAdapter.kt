package cn.wthee.pcrtool.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.MainActivity
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.model.EnemyData
import cn.wthee.pcrtool.databinding.ItemEnemyBinding
import cn.wthee.pcrtool.utils.Constants.UNIT_ICON_URL
import cn.wthee.pcrtool.utils.Constants.WEBP
import cn.wthee.pcrtool.utils.GlideUtil


class EnemyListAdapter() :
    PagingDataAdapter<EnemyData, EnemyListAdapter.ViewHolder>(EnemyDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemEnemyBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position)!!)
    }

    @Suppress("UNCHECKED_CAST")
//    override fun getFilter(): Filter {
//        return object : Filter() {
//            override fun performFiltering(constraint: CharSequence?): FilterResults {
//                val charString = constraint.toString()
//                val filterDatas = if (charString.isEmpty()) {
//                    //没有过滤的内容，则使用源数据
//
//                } else {
//                    val filteredList = arrayListOf<EnemyData>()
//                    try {
//                        currentList?.forEachIndexed { _, it ->
//                            if (it.name.contains(charString)) {
//                                //搜索
//                                filteredList.add(it)
//                            }
//                        }
//                    } catch (e: Exception) {
//                        e.message
//                    }
//                    filteredList
//                }
//                val filterResults = FilterResults()
//                filterResults.values = filterDatas
//                return filterResults
//            }
//
//
//            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
//                submitList(results?.values as PagedList<EnemyData>)
//            }
//        }
//    }

    inner class ViewHolder(private val binding: ItemEnemyBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(enemyData: EnemyData) {
            //设置数据
            binding.apply {
                val ctx = MyApplication.getContext()
                //加载动画
                root.animation =
                    AnimationUtils.loadAnimation(ctx, R.anim.anim_scale_alpha)
                //名称
                name.text = enemyData.unit_name
                //加载图片
                val picUrl = UNIT_ICON_URL + enemyData.unit_id + WEBP
                GlideUtil.load(picUrl, itemPic, R.drawable.error, null)
                //设置点击跳转
                root.setOnClickListener {
                    MainActivity.currentEquipPosition = adapterPosition
//                    EquipmentDetailsFragment.getInstance(enemyData, true).show(
//                        ActivityUtil.instance.currentActivity?.supportFragmentManager!!,
//                        "details"
//                    )
                }
            }
        }
    }
}

private class EnemyDiffCallback : DiffUtil.ItemCallback<EnemyData>() {

    override fun areItemsTheSame(
        oldItem: EnemyData,
        newItem: EnemyData
    ): Boolean {
        return oldItem.unit_id == newItem.unit_id
    }

    override fun areContentsTheSame(
        oldItem: EnemyData,
        newItem: EnemyData
    ): Boolean {
        return oldItem.unit_id == newItem.unit_id
    }
}
