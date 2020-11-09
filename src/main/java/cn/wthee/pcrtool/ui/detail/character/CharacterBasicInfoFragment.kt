package cn.wthee.pcrtool.ui.detail.character

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import cn.wthee.pcrtool.MainActivity
import cn.wthee.pcrtool.MainActivity.Companion.sp
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.database.view.CharacterInfoPro
import cn.wthee.pcrtool.database.view.getPositionIcon
import cn.wthee.pcrtool.databinding.FragmentCharacterBasicInfoBinding
import cn.wthee.pcrtool.ui.main.CharacterListFragment
import cn.wthee.pcrtool.ui.main.CharacterViewModel
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.InjectorUtil
import cn.wthee.pcrtool.utils.ObjectAnimatorHelper
import cn.wthee.pcrtool.utils.ResourcesUtil
import coil.load
import com.google.android.material.appbar.AppBarLayout
import kotlin.math.abs


class CharacterBasicInfoFragment : Fragment() {

    companion object {
        var isLoved = false
        fun getInstance(uid: Int): CharacterBasicInfoFragment {
            val fragment = CharacterBasicInfoFragment()
            val bundle = Bundle()
            bundle.putInt("uid", uid)
            fragment.arguments = bundle
            return fragment
        }
    }

    private var uid = -1
    private var urls = arrayListOf<String>()
    private lateinit var binding: FragmentCharacterBasicInfoBinding
    private val sharedCharacterViewModel by activityViewModels<CharacterViewModel> {
        InjectorUtil.provideCharacterViewModelFactory()
    }
    private val sharedCharacterAttrViewModel by activityViewModels<CharacterAttrViewModel> {
        InjectorUtil.providePromotionViewModelFactory()
    }
    private val sharedSkillViewModel by activityViewModels<CharacterSkillViewModel> {
        InjectorUtil.provideCharacterSkillViewModelFactory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireArguments().let {
            uid = it.getInt("uid")
        }
        isLoved = sp.getBoolean(uid.toString(), false)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCharacterBasicInfoBinding.inflate(inflater, container, false)
        //设置共享元素
        binding.root.transitionName = "item_${uid}"
        //toolbar 背景
        val picUrl =
            Constants.CHARACTER_URL + (uid + if (CharacterListFragment.r6Ids.contains(id)) 60 else 30) + Constants.WEBP
        //角色图片
        binding.characterPic.load(picUrl) {
            error(R.drawable.error)
            placeholder(R.drawable.load)
            listener(
                onStart = {
                    MainActivity.canBack = true
                    parentFragment?.startPostponedEnterTransition()
                }
            )
        }
        //开始动画
        ObjectAnimatorHelper.enter(object : ObjectAnimatorHelper.OnAnimatorListener {
            override fun prev(view: View) {
                view.visibility = View.GONE
            }

            override fun start(view: View) {
                view.visibility = View.VISIBLE
            }

            override fun end(view: View) {
                sharedCharacterAttrViewModel.getMaxRankAndRarity(uid)
                MainActivity.canBack = true
            }
        }, binding.fabLoveCbi, binding.basicInfo)
        //点击事件
        setListener()
        //初始化数据
        sharedCharacterViewModel.getCharacter(uid)
        sharedCharacterViewModel.character.observe(viewLifecycleOwner, {
            setData(it)
            urls = it.getAllUrl()
        })
        setHasOptionsMenu(true)
        //初始收藏
        setLove(isLoved)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.root.layoutTransition.setAnimateParentHierarchy(false);
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
                CharacterPicDialogFragment
                    .getInstance(urls)
                    .show(parentFragmentManager, "pic")
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
                uid.toString(),
                isLoved
            )
        }

        val ic = if (isLoved) R.drawable.ic_loved else R.drawable.ic_love
        binding.rightIcon.setImageResource(ic)

        val icFabColor =
            resources.getColor(if (isLoved) R.color.colorPrimary else R.color.alphaPrimary, null)

        val color = ResourcesUtil.getColor(if (isLoved) R.color.colorPrimary else R.color.text)
        binding.name.setTextColor(color)
        binding.nameExtra.setTextColor(color)
        binding.fabLoveCbi.imageTintList = ColorStateList.valueOf(icFabColor)
    }

    //初始化角色基本数据
    private fun setData(characterPro: CharacterInfoPro) {
        //文本数据
        binding.apply {
            unitId.text = uid.toString()
            toolTitle.text =
                if (characterPro.actualName.isEmpty())
                    characterPro.name
                else
                    characterPro.actualName
            catah.text = characterPro.catchCopy
            name.text = characterPro.getNameF()
            nameExtra.text = characterPro.getNameL()
            three.text = requireActivity().resources.getString(
                R.string.character_detail,
                characterPro.age,
                characterPro.height,
                characterPro.weight,
                characterPro.position
            )
            intro.text = characterPro.getIntroText()
            if (intro.text.isEmpty()) intro.visibility = View.GONE
            birth.text = requireActivity().resources.getString(
                R.string.birth,
                characterPro.birthMonth,
                characterPro.birthDay
            )
            blood.text =
                requireActivity().resources.getString(R.string.blood, characterPro.bloodType)
            race.text = characterPro.race
            guide.text = characterPro.guild
            favorite.text = characterPro.favorite
            cv.text = characterPro.voice
            self.text = characterPro.getSelf()
            positionType.background =
                ResourcesUtil.getDrawable(getPositionIcon(characterPro.position))
            comments.text = characterPro.getCommentsText()
        }
    }
}
