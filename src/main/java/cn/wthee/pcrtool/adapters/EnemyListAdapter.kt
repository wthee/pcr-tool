package cn.wthee.pcrtool.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Filter
import android.widget.Filterable
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.MainActivity
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.database.entity.EnemyData
import cn.wthee.pcrtool.databinding.ItemCommonBinding
import cn.wthee.pcrtool.ui.detail.enemy.EnemyDialogFragment
import cn.wthee.pcrtool.utils.Constants.UNIT_ICON_SHADOW_URL
import cn.wthee.pcrtool.utils.Constants.UNIT_ICON_URL
import cn.wthee.pcrtool.utils.Constants.WEBP
import coil.load


class EnemyListAdapter(
    private val fragmentManager: FragmentManager
) :
    ListAdapter<EnemyData, EnemyListAdapter.ViewHolder>(EnemyDiffCallback()), Filterable {
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

    @Suppress("UNCHECKED_CAST")
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charString = constraint.toString()
                val filterDatas = if (charString.isEmpty()) {
                    //没有过滤的内容，则使用源数据
                    currentList
                } else {
                    val filteredList = arrayListOf<EnemyData>()
                    try {
                        currentList.forEachIndexed { _, it ->
                            if (it.unit_name.contains(charString)) {
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
                submitList(results?.values as List<EnemyData>)
            }
        }
    }

    inner class ViewHolder(private val binding: ItemCommonBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(enemyData: EnemyData) {
            //设置数据
            binding.apply {
                val ctx = MyApplication.context
                //加载动画
                root.animation =
                    AnimationUtils.loadAnimation(ctx, R.anim.anim_scale_alpha)
                //名称
                name.text = enemyData.unit_name
                //加载图片
                val picUrl = if (enemyData.unit_id < 600101) {
                    UNIT_ICON_URL + enemyData.prefab_id
                } else {
                    UNIT_ICON_SHADOW_URL + enemyData.getTruePrefabId()
                } + WEBP
                pic.load(picUrl) {
                    error(R.drawable.unknow_gray)
                    placeholder(R.drawable.load_mini)
                }
                //设置点击跳转
                root.setOnClickListener {
                    MainActivity.currentEquipPosition = adapterPosition
                    EnemyDialogFragment.getInstance(enemyData).show(fragmentManager, "enemy")
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
