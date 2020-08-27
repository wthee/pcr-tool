package cn.wthee.pcrtool.adapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Filter
import android.widget.Filterable
import androidx.core.content.edit
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.MainActivity
import cn.wthee.pcrtool.MainActivity.Companion.canBack
import cn.wthee.pcrtool.MainActivity.Companion.sp
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.model.FilterCharacter
import cn.wthee.pcrtool.data.model.entity.CharacterBasicInfo
import cn.wthee.pcrtool.databinding.ItemCharacterBinding
import cn.wthee.pcrtool.ui.main.CharacterListFragment
import cn.wthee.pcrtool.ui.main.MainPagerFragment
import cn.wthee.pcrtool.utils.Constants
import coil.load
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


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
                (binding.root.parent as? ViewGroup)?.doOnPreDraw {
                    // Parent has been drawn. Start transitioning!
                    fragment.startPostponedEnterTransition()
                }
                //是否收藏
                val isLoved = sp.getBoolean(character.id.toString(), false)
                content.name.setTextColor(
                    ResourcesCompat.getColor(
                        fragment.resources,
                        if (isLoved) R.color.colorPrimary else R.color.text,
                        null
                    )
                )
                //加载动画
                root.animation =
                    AnimationUtils.loadAnimation(fragment.context, R.anim.anim_translate_y)
                //加载网络图片
                val picUrl = Constants.CHARACTER_URL + character.getAllStarId()[1] + Constants.WEBP
                characterPic.load(picUrl) {
                    error(R.drawable.error)
                    placeholder(R.drawable.load)
                }
                //角色位置
                content.positionType.background =
                    ResourcesCompat.getDrawable(
                        fragment.resources,
                        character.getPositionIcon(),
                        null
                    )
                //基本信息
                content.name.text = character.name
                content.three.text = fragment.resources.getString(
                    R.string.character_detail,
                    character.age,
                    character.height,
                    character.weight,
                    character.position
                )
                //设置共享元素名称
                characterPic.transitionName = "img_${character.id}"
                root.setOnClickListener {
                    //避免同时点击两个
                    if (!MainPagerFragment.cListClick) {
                        MainPagerFragment.cListClick = true
                        canBack = false
                        MainActivity.currentCharaPosition = adapterPosition
                        val bundle = Bundle()
                        bundle.putSerializable("character", character)
//                        //共享元素过渡
//                        val imageLoader = Coil.imageLoader(MyApplication.getContext())
//                        val key = characterPic.metadata?.memoryCacheKey
//                        val toStart = key != null && imageLoader.memoryCache[key] != null
//                        if (toStart) {
//                            fragment.startPostponedEnterTransition()
//                        }
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
                //长按事件
                binding.root.setOnLongClickListener {
//                    OnTouchUtil.addEffect(root)
                    val isLoved = sp.getBoolean(character.id.toString(), false)
                    sp.edit {
                        putBoolean(
                            character.id.toString(),
                            !isLoved
                        )
                    }
                    CharacterListFragment.characterList.adapter?.notifyItemChanged(adapterPosition)
                    return@setOnLongClickListener true
                }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val param: FilterCharacter = Gson().fromJson(
                    constraint.toString(),
                    object : TypeToken<FilterCharacter>() {}.type
                )
                val filterDatas = if (constraint == null) {
                    //没有过滤的内容，则使用源数据
                    currentList
                } else {
                    val filteredList = currentList.toMutableList()
                    filteredList.toHashSet().forEachIndexed { index, data ->
                        if (!param.all) {
                            //过滤非收藏角色
                            if (!sp.getBoolean(data.id.toString(), false)) {
                                filteredList.remove(data)
                            }
                        }
                        //位置筛选
                        if (param.positon != 0) {
                            val notInPositon = param.positon == 1 && data.position in 301..999
                                    || param.positon == 2 && (data.position in 0..300 || data.position in 601..9999)
                                    || param.positon == 3 && data.position in 0..600
                            if (notInPositon) {
                                filteredList.remove(data)
                            }
                        }
                        //攻击类型筛选
                        if (param.atk != 0) {
                            if (param.atk != data.atkType) {
                                filteredList.remove(data)
                            }
                        }
                        //公会筛
                        if (param.guild != "全部") {
                            if (param.guild != data.guild) {
                                filteredList.remove(data)
                            }
                        }
                    }
                    filteredList
                }
                val filterResults = FilterResults()
                filterResults.values = filterDatas
                filterResults.count = filterDatas.size
                return filterResults
            }


            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                submitList(results?.values as List<CharacterBasicInfo>)
                sp.edit {
                    putInt(Constants.SP_COUNT_CHARACTER, results.count)
                }
                MainPagerFragment.tabLayout.getTabAt(0)?.text = results.count.toString()
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