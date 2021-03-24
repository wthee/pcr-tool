package cn.wthee.pcrtool.ui.skill

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.adapter.SkillAdapter
import cn.wthee.pcrtool.data.model.int
import cn.wthee.pcrtool.databinding.FragmentCharacterSkillBinding
import cn.wthee.pcrtool.ui.character.attr.CharacterAttrFragment
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.Constants.UID
import cn.wthee.pcrtool.utils.InjectorUtil
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
        fun getInstance(uid: Int, type: Int) =
            SkillFragment().apply {
                arguments = Bundle().apply {
                    putInt(UID, uid)
                    putInt(Constants.TYPE_SKILL, type)
                }
            }

        lateinit var shareSkillList: RecyclerView
    }

    private lateinit var binding: FragmentCharacterSkillBinding
    private lateinit var adapter: SkillAdapter
    private var uid = 0
    private var type = 0
    private lateinit var skillViewModel: SkillViewModel
    private val characterAttrViewModel by activityViewModels<CharacterAttrViewModel> {
        InjectorUtil.provideCharacterAttrViewModelFactory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireArguments().apply {
            uid = getInt(UID)
            type = getInt(Constants.TYPE_SKILL)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCharacterSkillBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    private fun init() {
        skillViewModel =
            InjectorUtil.provideSkillViewModelFactory().create(SkillViewModel::class.java)
        binding.apply {
            shareSkillList = skillList
            //技能信息
            adapter = SkillAdapter(parentFragmentManager)
            skillList.adapter = adapter
        }
        if (type == 0) {
            //延迟绘制页面
            binding.skillList.visibility = View.GONE
            binding.skillList.postDelayed({
                binding.skillList.visibility = View.VISIBLE
            }, 600L)
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
        } else {
            //fixme enemy skill
            var level = 70
            var atk = 100
            skillViewModel.getCharacterSkills(level, atk, uid)
        }

        skillViewModel.skills.observe(viewLifecycleOwner) {
            adapter.setSize(it.size)
            adapter.submitList(it) {
                adapter.notifyDataSetChanged()
            }
        }
    }

}