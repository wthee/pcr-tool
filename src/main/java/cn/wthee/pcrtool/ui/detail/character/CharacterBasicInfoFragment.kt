package cn.wthee.pcrtool.ui.detail.character

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.transition.TransitionInflater
import cn.wthee.pcrtool.MainActivity.Companion.sp
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.model.CharacterBasicInfo
import cn.wthee.pcrtool.databinding.FragmentCharacterBasicInfoBinding
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.GlideUtil
import cn.wthee.pcrtool.utils.ObjectAnimatorHelper
import com.google.android.material.appbar.AppBarLayout
import javax.inject.Singleton
import kotlin.math.abs

@Singleton
class CharacterBasicInfoFragment : Fragment() {

    companion object {
        var isLoved = false

        fun getInstance(characterInfo: CharacterBasicInfo): CharacterBasicInfoFragment {
            val fragment = CharacterBasicInfoFragment()
            val bundle = Bundle()
            bundle.putSerializable("character", characterInfo)
            fragment.arguments = bundle
            return fragment
        }
    }

    private lateinit var character: CharacterBasicInfo
    private lateinit var binding: FragmentCharacterBasicInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            character = it.getSerializable("character") as CharacterBasicInfo
        }
        isLoved = sp.getBoolean(character.id.toString(), false)
        sharedElementEnterTransition =
            TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        sharedElementReturnTransition =
            TransitionInflater.from(context).inflateTransition(android.R.transition.move)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCharacterBasicInfoBinding.inflate(inflater, container, false)
        //设置共享元素
        binding.characterPic.transitionName = "img_${character.id}"

        //加载图片
        loadImages()
        //点击事件
        setListener()
        //初始化数据
        setData()
        setHasOptionsMenu(true)
        return binding.root
    }

    //加载图片
    private fun loadImages() {
        //toolbar 背景
        GlideUtil.load(
            Constants.CHARACTER_URL + character.getAllStarId()[1] + Constants.WEBP,
            binding.characterPic,
            R.drawable.error,
            parentFragment
        )
        //icon
        GlideUtil.load(
            Constants.CHARACTER_ICON_URL + character.getAllStarId()[0] + Constants.WEBP,
            binding.icon1,
            R.drawable.unknow,
            null
        )
        parentFragment?.startPostponedEnterTransition()
        ObjectAnimatorHelper.alpha(binding.fab, binding.toolbar)
    }

    //点击事件
    private fun setListener() {
        binding.apply {
            toolbar.setNavigationOnClickListener { view ->
                view.findNavController().navigateUp()
            }
            toolbar.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_love -> {
                        isLoved = !isLoved
                        setLove(isLoved)
                        true
                    }
                    else -> false
                }
            }
            //toolbar 展开折叠监听
            val menu = toolbar.menu
            val shareMenu = menu.getItem(0)
            appBar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
                when {
                    //展开
                    verticalOffset == 0 -> {
                        shareMenu.isVisible = false
                        binding.layoutToolbar.setCollapsedTitleTextColor(
                            resources.getColor(
                                R.color.colorAlpha,
                                null
                            )
                        )

                    }
                    abs(verticalOffset) >= appBarLayout!!.totalScrollRange -> {
                        shareMenu.setIcon(if (isLoved) R.drawable.ic_loved else R.drawable.ic_love)
                        shareMenu.isVisible = true
                        binding.layoutToolbar.setCollapsedTitleTextColor(
                            resources.getColor(
                                R.color.colorPrimary,
                                null
                            )
                        )
                    }
                    else -> {
                        if (shareMenu.icon.alpha != 0) shareMenu.isVisible = false
                        binding.layoutToolbar.setCollapsedTitleTextColor(
                            resources.getColor(
                                R.color.colorAlpha,
                                null
                            )
                        )
                    }
                }
            })
            //fab点击监听
            fab.setOnClickListener {
                isLoved = !isLoved
                setLove(isLoved)
            }
            setLove(isLoved)
        }
    }

    private fun setLove(isLoved: Boolean) {
        sp.edit {
            putBoolean(
                character.id.toString(),
                isLoved
            )
        }

        val ic = if (isLoved) R.drawable.ic_loved else R.drawable.ic_love
        val menu = binding.toolbar.menu
        val shareMenu = menu.getItem(0)
        shareMenu.setIcon(ic)

        val icFabColor = if (isLoved) resources.getColor(
            R.color.blue,
            null
        ) else resources.getColor(R.color.alphaPrimary, null)
        binding.fab.imageTintList = ColorStateList.valueOf(icFabColor)

    }

    //初始化角色基本数据
    private fun setData() {
        binding.apply {
//            layoutToolbar.title = character.name
            toolbar.title = character.name
//                if (character.actualName.isEmpty())
//                    character.name
//                else
//                    character.actualName
            catah.text = character.catchCopy
            name.text = character.getNameF()
            character.getNameL().apply {
                if (this.isNotEmpty()) {
                    lastName.text = this
                } else {
                    lineEx.visibility = View.GONE
                }
            }
            age.text = character.age
            height.text =
                requireActivity().resources.getString(R.string.height, character.height)
            weight.text =
                requireActivity().resources.getString(R.string.weight, character.weight)
            birth.text = requireActivity().resources.getString(
                R.string.birth,
                character.birthMonth,
                character.birthDay
            )
            blood.text =
                requireActivity().resources.getString(R.string.blood, character.bloodType)
            race.text = character.race
            guide.text = character.guild
            favorite.text = character.favorite
            cv.text = character.voice
            self.text = character.getSelf()
            position.text = character.position.toString()
            positionType.background =
                resources.getDrawable(
                    character.getPositionIcon(),
                    null
                )
            loveSelfText.text = character.getLoveSelfText()
        }

    }
}
