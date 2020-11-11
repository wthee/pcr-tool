package cn.wthee.pcrtool.adapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.MainActivity
import cn.wthee.pcrtool.MainPagerFragment
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.view.CharacterInfo
import cn.wthee.pcrtool.data.view.getPositionIcon
import cn.wthee.pcrtool.databinding.ItemCharacterBinding
import cn.wthee.pcrtool.ui.main.CharacterListFragment
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.ResourcesUtil
import coil.load


class CharacterPageAdapter(
    private val fragment: Fragment
) : PagingDataAdapter<CharacterInfo, CharacterPageAdapter.ViewHolder>(CharacterDiffCallback()) {

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
        holder.bind(getItem(position)!!)
    }

    inner class ViewHolder(private val binding: ItemCharacterBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            character: CharacterInfo
        ) {
            //是否收藏
            val isLoved = CharacterListFragment.characterfilterParams.starIds.contains(character.id)

            binding.apply {
                (binding.root.parent as? ViewGroup)?.doOnPreDraw {
                    // Parent has been drawn. Start transitioning!
                    fragment.startPostponedEnterTransition()
                }
                name.setTextColor(ResourcesUtil.getColor(if (isLoved) R.color.colorPrimary else R.color.text))
                //加载动画
                root.animation =
                    AnimationUtils.loadAnimation(fragment.context, R.anim.anim_translate_y)
                //加载网络图片
                var id = character.id
                id += if (CharacterListFragment.r6Ids.contains(id)) 60 else 30
                val picUrl = Constants.CHARACTER_URL + id + Constants.WEBP
                characterPic.load(picUrl) {
                    error(R.drawable.error)
                    placeholder(R.drawable.load)
                }
                //角色位置
                positionType.background =
                    ResourcesUtil.getDrawable(getPositionIcon(character.position))
                //基本信息
                name.text = character.getNameF()
                nameExtra.text = character.getNameL()
                three.text = fragment.resources.getString(
                    R.string.character_detail,
                    character.getFixedAge(),
                    character.getFixedHeight(),
                    character.getFixedWeight(),
                    character.position
                )
                //设置共享元素名称
                itemCharacter.transitionName = "item_${character.id}"
                root.setOnClickListener {
                    //避免同时点击两个
                    if (!MainPagerFragment.cListClick) {
                        MainPagerFragment.cListClick = true
                        MainActivity.canBack = false
                        MainActivity.currentCharaPosition = absoluteAdapterPosition
                        val bundle = Bundle()
                        bundle.putInt("uid", character.id)
                        val extras =
                            FragmentNavigatorExtras(
                                itemCharacter to itemCharacter.transitionName
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
                    //收藏或取消
                    CharacterListFragment.characterfilterParams.apply {
                        if (starIds.contains(character.id))
                            remove(character.id)
                        else
                            add(character.id)
                    }
                    CharacterListFragment.characterList.adapter?.notifyItemChanged(
                        absoluteAdapterPosition
                    )
                    return@setOnLongClickListener true
                }
            }
        }
    }
}

class CharacterDiffCallback : DiffUtil.ItemCallback<CharacterInfo>() {

    override fun areItemsTheSame(
        oldItem: CharacterInfo,
        newItem: CharacterInfo
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: CharacterInfo,
        newItem: CharacterInfo
    ): Boolean {
        return oldItem == newItem
    }
}
