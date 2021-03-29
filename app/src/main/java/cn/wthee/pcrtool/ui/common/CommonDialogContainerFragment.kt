package cn.wthee.pcrtool.ui.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.databinding.FragmentContainerBinding
import cn.wthee.pcrtool.ui.character.attr.CharacterAttrFragment
import cn.wthee.pcrtool.ui.skill.SkillFragment
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.InjectorUtil
import cn.wthee.pcrtool.utils.ToastUtil
import cn.wthee.pcrtool.utils.int
import cn.wthee.pcrtool.viewmodel.CharacterAttrViewModel
import cn.wthee.pcrtool.viewmodel.ClanViewModel
import cn.wthee.pcrtool.viewmodel.SkillViewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

/**
 * 底部弹窗基类
 */
class CommonDialogContainerFragment : CommonBottomSheetDialogFragment() {

    private val FRAGMENT_TYPE = "fragment_type"
    private var type = 0
    private var uid = 0
    private var level = 0
    private var atk = 0

    companion object {
        fun loadSkillFragment(uid: Int, skillLevel: Int, atk: Int) =
            CommonDialogContainerFragment().apply {
                arguments = Bundle().apply {
                    putInt(FRAGMENT_TYPE, 1)
                    putInt(Constants.UID, uid)
                    putInt(Constants.SKILL_LEVEL, skillLevel)
                    putInt(Constants.ATK, atk)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            type = it.getInt(FRAGMENT_TYPE)
            uid = it.getInt(Constants.UID)
            level = it.getInt(Constants.SKILL_LEVEL)
            atk = it.getInt(Constants.ATK)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentContainerBinding.inflate(inflater, container, false)
        when (type) {
            1 -> {
                val attrViewModel = InjectorUtil.provideCharacterAttrViewModelFactory()
                    .create(CharacterAttrViewModel::class.java)
                val skillViewModel =
                    InjectorUtil.provideSkillViewModelFactory().create(SkillViewModel::class.java)
                skillViewModel.getCharacterSkills(0, 0, uid)
                skillViewModel.skills.observe(viewLifecycleOwner) {
                    if (it.isEmpty()) {
                        dialog?.dismiss()
                        ToastUtil.short("无技能信息~")
                    }
                }
                //技能
                if (uid / 1000000 == 0) {
                    //角色
                    val selData = mutableMapOf<String, Int>()
                    MainScope().launch {
                        attrViewModel.getCharacterInfo(uid, CharacterAttrFragment.selData)
                    }
                    attrViewModel.sumInfo.observe(viewLifecycleOwner) {
                        childFragmentManager.beginTransaction()
                            .replace(
                                R.id.container,
                                SkillFragment.getInstance(
                                    uid,
                                    2,
                                    arrayListOf(level),
                                    if (it.atk != 0.0) it.atk.int else it.magicStr.int
                                )
                            )
                            .commit()
                    }

                } else {
                    //怪物
                    val viewModel =
                        InjectorUtil.provideClanViewModelFactory().create(ClanViewModel::class.java)
                    viewModel.getBossAttr(uid)
                    viewModel.clanBossAttr.observe(viewLifecycleOwner) {
                        if (it != null) {
                            //技能
                            childFragmentManager.beginTransaction()
                                .replace(
                                    R.id.container,
                                    SkillFragment.getInstance(
                                        it.unit_id,
                                        1,
                                        it.getSkillLv(),
                                        it.attr.atk
                                    )
                                )
                                .commit()
                        }
                    }
                }
            }

            else -> {
            }

        }

        return binding.root
    }
}