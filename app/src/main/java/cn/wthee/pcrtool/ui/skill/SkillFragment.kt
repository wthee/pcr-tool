package cn.wthee.pcrtool.ui.skill

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.adapter.SkillAdapter
import cn.wthee.pcrtool.databinding.FragmentCharacterSkillBinding
import cn.wthee.pcrtool.ui.character.CharacterPagerFragment
import cn.wthee.pcrtool.ui.character.attr.CharacterAttrFragment
import cn.wthee.pcrtool.utils.*
import cn.wthee.pcrtool.utils.Constants.UID
import cn.wthee.pcrtool.viewmodel.CharacterAttrViewModel
import cn.wthee.pcrtool.viewmodel.SkillViewModel

/**
 * 角色技能页面
 *
 * 根据 [uid] 显示角色数据
 *
 * 页面布局 [FragmentCharacterSkillBinding]
 *
 * ViewModels [SkillViewModel]
 */
class SkillFragment : Fragment() {


    companion object {
        fun getInstance(uid: Int, type: Int, lvs: ArrayList<Int> = arrayListOf(), atk: Int = 0) =
            SkillFragment().apply {
                arguments = Bundle().apply {
                    putInt(UID, uid)
                    putInt(Constants.TYPE_SKILL, type)
                    putSerializable(Constants.CLAN_SKILL_LVS, lvs)
                    putInt(Constants.CLAN_BOSS_ATK, atk)
                }
            }
    }

    private lateinit var binding: FragmentCharacterSkillBinding
    private lateinit var adapter: SkillAdapter
    private var uid = 0
    private var type = 0
    private var lvs = arrayListOf<Int>()
    private var bossAtk = 0
    private lateinit var skillViewModel: SkillViewModel
    private val characterAttrViewModel by activityViewModels<CharacterAttrViewModel> {
        InjectorUtil.provideCharacterAttrViewModelFactory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireArguments().apply {
            uid = getInt(UID)
            type = getInt(Constants.TYPE_SKILL)
            lvs = getSerializable(Constants.CLAN_SKILL_LVS) as ArrayList<Int>
            bossAtk = getInt(Constants.CLAN_BOSS_ATK)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCharacterSkillBinding.inflate(inflater, container, false)
        init()
        setListener()
        return binding.root
    }

    private fun setListener() {
        binding.fabSkillLoop.apply {
            text = getString(R.string.skill_loop)
            icon = ResourcesUtil.getDrawable(R.drawable.ic_loop)
            setOnClickListener {
                SkillLoopDialogFragment.getInstance(CharacterPagerFragment.uid)
                    .show(parentFragmentManager, "loop")
            }
        }
        binding.fabShare.apply {
            setImageResource(R.drawable.ic_share)
            setOnClickListener {
                ShareIntentUtil.imageLong(
                    requireActivity(),
                    binding.skillList,
                    "skill_${CharacterPagerFragment.uid}.png"
                )
            }
        }
    }

    private fun init() {
        skillViewModel =
            InjectorUtil.provideSkillViewModelFactory().create(SkillViewModel::class.java)
        binding.apply {
            //技能信息
            adapter = SkillAdapter(parentFragmentManager, type)
            skillList.adapter = adapter
        }
        when (type) {
            0 -> {
                //角色技能
                var level = CharacterAttrFragment.maxLv
                var atk = 0.0
                characterAttrViewModel.sumInfo.observe(viewLifecycleOwner) {
                    atk = if (it.atk != 0.0) it.atk else it.magicStr
                    skillViewModel.getCharacterSkills(level, atk.int, uid)
                }
                characterAttrViewModel.selData.observe(viewLifecycleOwner) {
                    level = it[Constants.LEVEL] ?: level
                    skillViewModel.getCharacterSkills(level, atk.int, uid)
                }
            }
            1 -> {
                skillViewModel.getEnemySkill(lvs, bossAtk, uid)
            }
            2 -> {
                skillViewModel.getCharacterSkills(lvs[0], bossAtk, uid)
            }
        }
        skillViewModel.skills.observe(viewLifecycleOwner) {
            adapter.setSize(it.size)
            adapter.submitList(it) {
                adapter.notifyDataSetChanged()
            }
        }
    }
}