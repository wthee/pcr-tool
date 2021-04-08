package cn.wthee.pcrtool.ui.character.basic

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import cn.wthee.pcrtool.MainActivity
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.view.CharacterInfoPro
import cn.wthee.pcrtool.data.view.getPositionIcon
import cn.wthee.pcrtool.databinding.FragmentCharacterBasicInfoBinding
import cn.wthee.pcrtool.ui.character.CharacterPagerFragment
import cn.wthee.pcrtool.ui.home.CharacterListFragment
import cn.wthee.pcrtool.utils.Constants.UID
import cn.wthee.pcrtool.utils.FabHelper
import cn.wthee.pcrtool.utils.InjectorUtil
import cn.wthee.pcrtool.utils.ResourcesUtil
import cn.wthee.pcrtool.utils.deleteSpace
import cn.wthee.pcrtool.viewmodel.CharacterViewModel

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
        lateinit var binding: FragmentCharacterBasicInfoBinding
        fun getInstance(uid: Int) = CharacterBasicInfoFragment().apply {
            arguments = Bundle().apply {
                putInt(UID, uid)
            }
        }
    }

    private var uid = -1
    private var isLoved = false
    private val characterViewModel by activityViewModels<CharacterViewModel> {
        InjectorUtil.provideCharacterViewModelFactory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireArguments().apply {
            uid = getInt(UID)
        }
        isLoved = CharacterListFragment.characterFilterParams.starIds.contains(
            CharacterPagerFragment.uid
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCharacterBasicInfoBinding.inflate(inflater, container, false)
        //初始化
        init()
        //初始收藏
        setLove(isLoved)
        binding.fabShare.apply {
            setOnClickListener {
                isLoved = !isLoved
                CharacterListFragment.characterFilterParams.addOrRemove(
                    CharacterPagerFragment.uid
                )
                setLove(isLoved)
            }
        }
        characterViewModel.character.observe(viewLifecycleOwner) {
            setData(it)
        }
        return binding.root
    }

    //初始化
    private fun init() {
        //添加返回fab
        FabHelper.addBackFab(MainActivity.pageLevel, true)
        //初始化数据
        characterViewModel.getCharacter(uid)
    }

    //初始化角色基本数据
    private fun setData(characterPro: CharacterInfoPro) {
        //文本数据
        binding.apply {
            catah.text = characterPro.catchCopy.deleteSpace()
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

    /**
     * 更新收藏按钮颜色
     */
    private fun setLove(isLoved: Boolean) {
        binding.fabShare.setImageResource(if (isLoved) R.drawable.ic_loved else R.drawable.ic_loved_line)
    }
}
