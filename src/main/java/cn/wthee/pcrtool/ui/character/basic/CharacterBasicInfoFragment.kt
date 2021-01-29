package cn.wthee.pcrtool.ui.character.basic

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.CharacterInfoPro
import cn.wthee.pcrtool.data.db.view.getPositionIcon
import cn.wthee.pcrtool.databinding.FragmentCharacterBasicInfoBinding
import cn.wthee.pcrtool.ui.home.CharacterListFragment
import cn.wthee.pcrtool.ui.home.CharacterViewModel
import cn.wthee.pcrtool.utils.*
import cn.wthee.pcrtool.utils.Constants.UID
import coil.load
import kotlinx.coroutines.launch

/**
 * 角色基本信息页面
 *
 * 根据 [uid] 显示角色数据
 *
 * 页面布局 [FragmentCharacterBasicInfoBinding]
 *
 * ViewModels [CharacterViewModel]
 */
class CharacterBasicInfoFragment : Fragment() {

    companion object {
        var isLoved = false
        lateinit var binding: FragmentCharacterBasicInfoBinding
        lateinit var characterPic: AppCompatImageView
        fun getInstance(uid: Int) = CharacterBasicInfoFragment().apply {
            arguments = Bundle().apply {
                putInt(UID, uid)
            }
        }
    }

    private var uid = -1
    private val sharedCharacterViewModel by activityViewModels<CharacterViewModel> {
        InjectorUtil.provideCharacterViewModelFactory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireArguments().apply {
            uid = getInt(UID)
        }
        lifecycleScope.launch {
            DataStoreUtil.get(Constants.SP_STAR_CHARACTER, object : DataStoreRead<String> {
                override fun read(s: String?) {
                    val starIds = DataStoreUtil.fromJson<ArrayList<Int>>(s)
                    CharacterListFragment.characterFilterParams.starIds = starIds ?: arrayListOf()
                    //是否收藏
                    isLoved =
                        CharacterListFragment.characterFilterParams.starIds.contains(uid) ?: false
                }
            })
        }
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
        })
        //初始收藏
        setLove(isLoved)
        //开始过渡动画
        parentFragment?.startPostponedEnterTransition()
        return binding.root
    }

    //初始化
    private fun init() {
        //添加返回fab
        FabHelper.addBackFab(1, true)
        characterPic = binding.characterPic
        //初始化数据
        sharedCharacterViewModel.getCharacter(uid)
        //打开页面共享元素
        binding.root.transitionName = "item_${uid}"
        //toolbar 背景
        lifecycleScope.launch {
            val picUrl =
                Constants.CHARACTER_FULL_URL + (uid + if (sharedCharacterViewModel.getR6Ids()
                        .contains(uid)
                ) 60 else 30) + Constants.WEBP
            //加载图片
            binding.characterPic.load(picUrl) {
                error(R.drawable.error)
                placeholder(R.drawable.load)
            }
        }
    }

    //点击事件
    private fun setListener() {
        binding.apply {
            //角色图片列表
            characterPic.setOnClickListener {
                CharacterPicListFragment.getInstance(uid)
                    .show(parentFragmentManager, "pic_list")
            }
            //fab点击监听
            fabLoveCbi.setOnClickListener {
                isLoved = !isLoved
                CharacterListFragment.characterFilterParams.addOrRemove(uid)
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
        val icFabColor =
            ResourcesUtil.getColor(if (isLoved) R.color.colorPrimary else R.color.alphaPrimary)

        val color = ResourcesUtil.getColor(if (isLoved) R.color.colorPrimary else R.color.text)
        binding.name.setTextColor(color)
        binding.fabLoveCbi.imageTintList = ColorStateList.valueOf(icFabColor)
    }

    //初始化角色基本数据
    private fun setData(characterPro: CharacterInfoPro) {
        //文本数据
        binding.apply {
            unitId.text = uid.toString()
            catah.text = characterPro.catchCopy
            intro.text = characterPro.getIntroText()
            if (intro.text.isEmpty()) intro.visibility = View.GONE
            trueName.text = if (characterPro.actualName.isEmpty())
                characterPro.name
            else
                characterPro.actualName
            name.text = characterPro.name
            height.text =
                resources.getString(R.string.character_heigth, characterPro.getFixedHeight())
            weight.text =
                resources.getString(R.string.character_weigth, characterPro.getFixedWeight())

            birth.text = resources.getString(
                R.string.date_m_d,
                characterPro.birthMonth,
                characterPro.birthDay
            )
            age.text = characterPro.getFixedAge()
            blood.text = resources.getString(R.string.blood, characterPro.bloodType)
            position.text = characterPro.position.toString()
            race.text = characterPro.race
            guild.text = characterPro.guild
            favorite.text = characterPro.favorite
            cv.text = characterPro.voice
            self.text = characterPro.getSelf()
            positionType.background =
                ResourcesUtil.getDrawable(getPositionIcon(characterPro.position))
            comments.text = characterPro.getCommentsText()
            roomComments.text = characterPro.getRoomCommentsText()
        }
    }
}
