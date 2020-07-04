package cn.wthee.pcrtool.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Filter
import android.widget.Filterable
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.MainActivity
import cn.wthee.pcrtool.MainActivity.Companion.sortType
import cn.wthee.pcrtool.MainActivity.Companion.sp
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.model.CharacterBasicInfo
import cn.wthee.pcrtool.databinding.ItemCharacterBinding
import cn.wthee.pcrtool.ui.main.ContainerFragment
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.GlideUtil
import com.bumptech.glide.Glide


class CharacterAdapter(private val fragment: Fragment) :
    ListAdapter<CharacterBasicInfo, CharacterAdapter.ViewHolder>(CharacterDiffCallback()),
    Filterable {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemCharacterBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), fragment)
    }


    class ViewHolder(private val binding: ItemCharacterBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            character: CharacterBasicInfo,
            fragment: Fragment
        ) {
            binding.apply {
                //是否收藏
                val isLoved = sp.getBoolean(character.id.toString(), false)
                if (isLoved)
                    love.visibility = View.VISIBLE
                else
                    love.visibility = View.GONE
                //加载动画
                characterPic.animation =
                    AnimationUtils.loadAnimation(fragment.context, R.anim.item_character_pic)
                other.animation =
                    AnimationUtils.loadAnimation(fragment.context, R.anim.item_character_list)
                //显示排序数据
                age.setTextColor(fragment.resources.getColor(R.color.text, null))
                height.setTextColor(fragment.resources.getColor(R.color.text, null))
                weight.setTextColor(fragment.resources.getColor(R.color.text, null))
                when (sortType) {
                    Constants.SORT_AGE -> age.setTextColor(
                        fragment.resources.getColor(
                            R.color.colorPrimary,
                            null
                        )
                    )
                    Constants.SORT_HEIGHT -> height.setTextColor(
                        fragment.resources.getColor(
                            R.color.colorPrimary,
                            null
                        )
                    )
                    Constants.SORT_WEIGHT -> weight.setTextColor(
                        fragment.resources.getColor(
                            R.color.colorPrimary,
                            null
                        )
                    )
                }
                //加载网络图片
                val picUrl =
                    Constants.CHARACTER_ICON_URL + character.getAllStarId()[0] + Constants.WEPB
                GlideUtil.load(picUrl, characterPic, R.drawable.unknow, null)
                //设置位置
                positionType.background =
                    fragment.resources.getDrawable(character.getPositionIcon(), null)
                //基本信息
                name.text = character.name
                age.text = character.age
                height.text = fragment.resources.getString(R.string.height, character.height)
                weight.text = fragment.resources.getString(R.string.weight, character.weight)
                //设置共享元素名称
                characterPic.transitionName = "img_${character.id}"
                //item点击事件，查看详情
                root.setOnClickListener {
                    //避免同时点击两个
                    if (!ContainerFragment.cListClick) {
                        ContainerFragment.cListClick = true
                        Glide.with(fragment.requireContext()).pauseRequests()
                        MainActivity.currentCharaPosition = adapterPosition
                        val bundle = android.os.Bundle()
                        bundle.putSerializable("character", character)
                        val extras =
                            FragmentNavigatorExtras(
                                characterPic to characterPic.transitionName
                            )
                        root.findNavController().navigate(
                            R.id.action_containerFragment_to_characterPagerFragment,
                            bundle,
                            null,
                            extras
                        )
                    }
                }
            }
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charString = constraint.toString()
                val filterDatas = if (charString.isEmpty()) {
                    //没有过滤的内容，则使用源数据
                    currentList
                } else {
                    val filteredList = arrayListOf<CharacterBasicInfo>()
                    try {
                        currentList.forEachIndexed { _, it ->
                            if (charString == "1" && sp.getBoolean(it.id.toString(), false)) {
                                //筛选已收藏
                                filteredList.add(it)
                            } else if (charString == "0") {
                                //全部
                                filteredList.add(it)
                            } else if (it.name.contains(charString)) {
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
                submitList(results?.values as List<CharacterBasicInfo>)
            }
        }
    }
}


class CharacterDiffCallback : DiffUtil.ItemCallback<CharacterBasicInfo>() {

    override fun areItemsTheSame(
        oldItem: CharacterBasicInfo,
        newItem: CharacterBasicInfo
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: CharacterBasicInfo,
        newItem: CharacterBasicInfo
    ): Boolean {
        return oldItem == newItem
    }
}