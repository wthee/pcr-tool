package cn.wthee.pcrtool.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.MainActivity
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.view.CharacterInfo
import cn.wthee.pcrtool.data.view.getPositionIcon
import cn.wthee.pcrtool.databinding.ItemCharacterBinding
import cn.wthee.pcrtool.ui.home.CharacterListFragment
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.Constants.UID
import cn.wthee.pcrtool.utils.Constants.UNIT_NAME
import cn.wthee.pcrtool.utils.Constants.UNIT_NAME_EX
import cn.wthee.pcrtool.utils.ResourcesUtil
import coil.load

/**
 * 角色列表适配器
 *
 * 列表项布局 [ItemCharacterBinding]
 *
 * 列表项数据 [CharacterInfo]
 */
class CharacterListAdapter(private val callback: CallBack) :
    PagingDataAdapter<CharacterInfo, CharacterListAdapter.ViewHolder>(CharacterDiffCallback()) {

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
            val isLoved =
                CharacterListFragment.characterFilterParams.starIds.contains(character.id)
            binding.apply {
                name.setTextColor(ResourcesUtil.getColor(if (isLoved) R.color.colorPrimary else R.color.text))
                //加载动画
                root.animation =
                    AnimationUtils.loadAnimation(root.context, R.anim.anim_list_item)
                //加载网络图片
                var id = character.id
                id += if (character.r6Id != 0) 60 else 30
                val picUrl = Constants.CHARACTER_FULL_URL + id + Constants.WEBP
                characterPic.load(picUrl) {
                    error(R.drawable.error)
                    placeholder(R.drawable.load)
                    listener(
                        onStart = {
                            startEnter()
                        }
                    )
                }
                //角色位置
                positionType.background =
                    ResourcesUtil.getDrawable(getPositionIcon(character.position))
                //基本信息
                name.text = character.getNameF()
                nameExtra.text = character.getNameL()
                three.text = root.context.resources.getString(
                    R.string.character_detail,
                    character.getFixedAge(),
                    character.getFixedHeight(),
                    character.getFixedWeight(),
                    character.position
                )
                //设置共享元素名称
                root.transitionName = "item_${character.id}"
                //点击跳转
                root.setOnClickListener {
                    if (MainActivity.canClick) {
                        MainActivity.canClick = false
                        MainActivity.currentCharaPosition = absoluteAdapterPosition
                        MainActivity.pageLevel = 1
                        val bundle = Bundle()
                        bundle.putInt(UID, character.id)
                        bundle.putString(UNIT_NAME, character.getNameF())
                        bundle.putString(UNIT_NAME_EX, character.getNameL())
                        val extras =
                            FragmentNavigatorExtras(
                                root to root.transitionName
                            )
                        root.findNavController().navigate(
                            R.id.action_characterListFragment_to_characterPagerFragment,
                            bundle,
                            null,
                            extras
                        )
                    }
                }
                //长按事件
                binding.root.setOnLongClickListener {
                    //收藏或取消
                    CharacterListFragment.characterFilterParams.apply {
                        addOrRemove(character.id)
                    }
                    notifyItemChanged(
                        absoluteAdapterPosition
                    )
                    return@setOnLongClickListener true
                }
            }
        }

        private fun startEnter() {
            if (absoluteAdapterPosition == MainActivity.currentCharaPosition) {
                callback.todo()
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