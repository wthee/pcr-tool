package cn.wthee.pcrtool.ui.detail.character

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.edit
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import cn.wthee.pcrtool.MainActivity.Companion.canBack
import cn.wthee.pcrtool.MainActivity.Companion.sp
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.database.view.CharacterBasicInfo
import cn.wthee.pcrtool.databinding.FragmentCharacterBasicInfoBinding
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.InjectorUtil
import cn.wthee.pcrtool.utils.ObjectAnimatorHelper
import coil.load
import com.google.android.material.appbar.AppBarLayout
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.HttpUrl
import kotlin.math.abs


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
    private lateinit var viewModel: CharacterAttrViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireArguments().let {
            character = it.getSerializable("character") as CharacterBasicInfo
        }
        isLoved = sp.getBoolean(character.id.toString(), false)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCharacterBasicInfoBinding.inflate(inflater, container, false)
        //设置共享元素
        binding.root.transitionName = "item_${character.id}"
        //开始动画
        ObjectAnimatorHelper.enter(object : ObjectAnimatorHelper.OnAnimatorListener {
            override fun prev(view: View) {
                view.visibility = View.GONE
            }

            override fun start(view: View) {
                view.visibility = View.VISIBLE
            }

            override fun end(view: View) {
                viewModel.getMaxRankAndRarity(character.id)
                CharacterSkillFragment.viewModel.getCharacterSkills(character.id)
            }
        }, binding.fabLoveCbi, binding.basicInfo)

        //加载图片
        loadImages()
        //点击事件
        setListener()
        //初始化数据
        setData()
        setHasOptionsMenu(true)
        //获取viewModel
        viewModel = InjectorUtil.providePromotionViewModelFactory()
            .create(CharacterAttrViewModel::class.java)
        //初始收藏
        setLove(isLoved)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.root.layoutTransition.setAnimateParentHierarchy(false);
    }

    //加载图片
    private fun loadImages() {
        //toolbar 背景
        val picUrl =
            HttpUrl.get(Constants.CHARACTER_URL + character.getAllStarId()[1] + Constants.WEBP)
        //角色图片
        binding.characterPic.load(picUrl) {
            error(R.drawable.error)
            placeholder(R.drawable.load)
            listener(
                onStart = {
                    parentFragment?.startPostponedEnterTransition()
                }
            )
        }
        lifecycleScope.launch {
            delay(resources.getInteger(R.integer.item_anim_fast).toLong())
            canBack = true
        }
    }

    //点击事件
    private fun setListener() {
        binding.apply {
            toolbar.setNavigationOnClickListener { view ->
                view.findNavController().navigateUp()
            }
            rightIcon.setOnClickListener {
                isLoved = !isLoved
                setLove(isLoved)
            }
            characterPic.setOnClickListener {
                CharacterPicDialogFragment.getInstance(character).show(parentFragmentManager, "pic")
            }
            //toolbar 展开折叠监听
            appBar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
                when {
                    //展开
                    verticalOffset == 0 -> {
                        rightIcon.visibility = View.GONE
                        binding.toolTitle.setTextColor(
                            resources.getColor(
                                R.color.colorAlpha,
                                null
                            )
                        )
                    }
                    abs(verticalOffset) >= appBarLayout!!.totalScrollRange -> {
                        rightIcon.setImageResource(if (isLoved) R.drawable.ic_loved else R.drawable.ic_love)
                        rightIcon.visibility = View.VISIBLE
                        binding.toolTitle.setTextColor(
                            resources.getColor(
                                R.color.colorPrimary,
                                null
                            )
                        )
                    }
                    else -> {
                        if (rightIcon.visibility == View.VISIBLE) rightIcon.visibility = View.GONE
                        binding.toolTitle.setTextColor(
                            resources.getColor(
                                R.color.colorAlpha,
                                null
                            )
                        )
                    }
                }
            })

            //fab点击监听
            fabLoveCbi.setOnClickListener {
                isLoved = !isLoved
                setLove(isLoved)
            }

        }
    }

    //设置收藏
    private fun setLove(isLoved: Boolean) {
        sp.edit {
            putBoolean(
                character.id.toString(),
                isLoved
            )
        }

        val ic = if (isLoved) R.drawable.ic_loved else R.drawable.ic_love
        binding.rightIcon.setImageResource(ic)

        val icFabColor =
            resources.getColor(if (isLoved) R.color.colorPrimary else R.color.alphaPrimary, null)

        binding.name.setTextColor(
            ResourcesCompat.getColor(
                resources,
                if (isLoved) R.color.colorPrimary else R.color.text,
                null
            )
        )

        binding.fabLoveCbi.imageTintList = ColorStateList.valueOf(icFabColor)

    }

    //初始化角色基本数据
    private fun setData() {
        binding.apply {
            toolTitle.text =
                if (character.actualName.isEmpty())
                    character.name
                else
                    character.actualName
            catah.text = character.catchCopy
            name.text = character.name
//            character.getNameL().apply {
//                if (this.isNotEmpty()) {
//                    lastName.text = this
//                } else {
//                    lineEx.visibility = View.GONE
//                }
//            }

            three.text = requireActivity().resources.getString(
                R.string.character_detail,
                character.age,
                character.height,
                character.weight,
                character.position
            )
            comment.text = character.getFixedComment()
            if (comment.text.isEmpty()) comment.visibility = View.GONE
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
            positionType.background =
                ResourcesCompat.getDrawable(
                    resources,
                    character.getPositionIcon(),
                    null
                )
            loveSelfText.text = character.getLoveSelfText()
        }
    }
}
