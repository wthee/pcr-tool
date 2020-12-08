package cn.wthee.pcrtool.ui.detail.character.basic

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import cn.wthee.pcrtool.MainActivity
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.CharacterInfoPro
import cn.wthee.pcrtool.data.db.view.getPositionIcon
import cn.wthee.pcrtool.databinding.FragmentCharacterBasicInfoBinding
import cn.wthee.pcrtool.ui.detail.character.CharacterPagerFragment
import cn.wthee.pcrtool.ui.home.CharacterListFragment
import cn.wthee.pcrtool.ui.home.CharacterViewModel
import cn.wthee.pcrtool.utils.*
import coil.load
import com.google.android.material.transition.Hold

/**
 * 角色基本信息页面
 */
class CharacterBasicInfoFragment : Fragment() {

    companion object {
        var isLoved = false
        lateinit var binding: FragmentCharacterBasicInfoBinding

        @Volatile
        private var instance: CharacterBasicInfoFragment? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: CharacterBasicInfoFragment().also { instance = it }
            }
    }

    private var uid = -1
    private var urls = arrayListOf<String>()
    private val sharedCharacterViewModel by activityViewModels<CharacterViewModel> {
        InjectorUtil.provideCharacterViewModelFactory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        uid = CharacterPagerFragment.uid
        isLoved = CharacterListFragment.characterFilterParams.starIds.contains(uid)
        exitTransition = Hold()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCharacterBasicInfoBinding.inflate(inflater, container, false)
        //初始化
        init()
        //点击事件
        setListener()
        sharedCharacterViewModel.character.observe(viewLifecycleOwner, {
            setData(it)
            urls = it.getAllUrl()
        })
        setHasOptionsMenu(true)
        //初始收藏
        setLove(isLoved)
        return binding.root
    }

    //初始化
    private fun init() {
        //初始化数据
        sharedCharacterViewModel.getCharacter(uid)
        //设置共享元素
        binding.root.transitionName = "item_${uid}"
        //toolbar 背景
        val picUrl =
            Constants.CHARACTER_URL + (uid + if (CharacterListFragment.r6Ids.contains(uid)) 60 else 30) + Constants.WEBP
        //角色图片
        binding.characterPic.transitionName = picUrl
        binding.characterPic.load(picUrl) {
            error(R.drawable.error)
            placeholder(R.drawable.load)
            listener(
                onStart = {
                    MainActivity.canBack = true
                    parentFragment?.startPostponedEnterTransition()
                    postponeEnterTransition()
                    //添加返回fab
                    FabHelper.addBackFab()
                },
                onSuccess = { _, _ ->
                    startPostponedEnterTransition()
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
                MainActivity.canBack = true
            }
        }, binding.fabLoveCbi, binding.basicInfo)
    }


    //点击事件
    private fun setListener() {
        binding.apply {
            characterPic.setOnClickListener {
                try {
                    val bundle = Bundle()
                    bundle.putStringArrayList("urls", urls)
                    val extras =
                        FragmentNavigatorExtras(
                            it to it.transitionName
                        )
                    findNavController().navigate(
                        R.id.action_characterPagerFragment_to_characterPicListFragment,
                        bundle,
                        null,
                        extras
                    )
                    //移除旧的单例，避免viewpager2重新添加fragment时异常
                    parentFragmentManager.beginTransaction()
                        .remove(getInstance())
                        .commit()
                } catch (e: Exception) {

                }

            }
            //fab点击监听
            fabLoveCbi.setOnClickListener {
                isLoved = !isLoved
                setLove(isLoved)
            }
            //角色编号
            unitId.setOnLongClickListener {
                ToastUtil.short(unitId.text.toString())
                return@setOnLongClickListener true
            }
        }
    }

    //设置收藏
    private fun setLove(isLoved: Boolean) {
        if (isLoved)
            CharacterListFragment.characterFilterParams.add(uid)
        else
            CharacterListFragment.characterFilterParams.remove(uid)

        val icFabColor =
            ResourcesUtil.getColor(if (isLoved) R.color.colorPrimary else R.color.alphaPrimary)

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
