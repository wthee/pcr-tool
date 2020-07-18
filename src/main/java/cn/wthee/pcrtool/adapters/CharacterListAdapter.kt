package cn.wthee.pcrtool.adapters

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Filter
import android.widget.Filterable
import androidx.core.content.edit
import androidx.core.content.res.ResourcesCompat
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
import cn.wthee.pcrtool.data.model.CharacterBasicInfo
import cn.wthee.pcrtool.data.model.FilterDataCharacter
import cn.wthee.pcrtool.databinding.ItemCharacterBinding
import cn.wthee.pcrtool.ui.main.MainPagerFragment
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.GlideUtil
import cn.wthee.pcrtool.utils.OnLoadListener
import com.bumptech.glide.Glide
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
                //是否收藏
                val isLoved = sp.getBoolean(character.id.toString(), false)
                if (isLoved)
                    content.love.visibility = View.VISIBLE
                else
                    content.love.visibility = View.GONE
                //加载动画
                root.animation =
                    AnimationUtils.loadAnimation(fragment.context, R.anim.anim_scale)
                //加载网络图片
                val picUrl =
                    Constants.CHARACTER_URL + character.getAllStarId()[1] + Constants.WEBP
                GlideUtil.loadWithListener(
                    picUrl,
                    characterPic,
                    R.drawable.error,
                    null,
                    object : OnLoadListener {
                        override fun onSuccess(bitmap: Bitmap) {
                            sp.edit {
                                putBoolean("first_click_${character.id}", false)
                            }
                        }
                    })
                //设置位置
                content.positionType.background =
                    ResourcesCompat.getDrawable(
                        fragment.resources,
                        character.getPositionIcon(),
                        null
                    )
                //基本信息
                content.name.text = character.name
                content.catah.text = character.catchCopy
                content.three.text = fragment.resources.getString(
                    R.string.character_detail,
                    character.age,
                    character.height,
                    character.weight,
                    character.position
                )
                //设置共享元素名称
                characterPic.transitionName = "img_${character.id}"
                content.info.transitionName = "content_${character.id}"
                //item点击事件，查看详情
                root.setOnClickListener {
                    //避免同时点击两个
                    if (!MainPagerFragment.cListClick) {
                        MainPagerFragment.cListClick = true
                        canBack = false
                        Glide.with(fragment.requireContext()).pauseRequests()
                        MainActivity.currentCharaPosition = adapterPosition
                        val bundle = Bundle()
                        bundle.putSerializable("character", character)
                        val extras =
                            FragmentNavigatorExtras(
                                characterPic to characterPic.transitionName,
                                content.info to content.info.transitionName
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
//                root.setOnLongClickListener {
//                    return@setOnLongClickListener true
//                }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val param: FilterDataCharacter = Gson().fromJson(
                    constraint.toString(),
                    object : TypeToken<FilterDataCharacter>() {}.type
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
                        if (param.hasPositionFilter()) {
                            if ((!param.positon1 && data.position in 0..300)
                                || (!param.positon2 && data.position in 301..600)
                                || (!param.positon3 && data.position >= 601)
                            ) {
                                filteredList.remove(data)
                            }
                        }
                        //攻击类型筛选
                        if (param.hasAtkFilter()) {
                            if ((!param.atkPhysical && data.atkType == 1) || (!param.atkMagic && data.atkType == 2)) {
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