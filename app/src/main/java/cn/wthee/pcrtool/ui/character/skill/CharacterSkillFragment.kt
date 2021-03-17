package cn.wthee.pcrtool.ui.character.skill

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
class CharacterSkillFragment : Fragment() {


    companion object {
        fun getInstance(uid: Int) =
            CharacterSkillFragment().apply {
                arguments = Bundle().apply {
                    putInt(UID, uid)
                }
            }

        lateinit var shareSkillList: RecyclerView
    }

    private lateinit var binding: FragmentCharacterSkillBinding
    private lateinit var adapter: SkillAdapter
    private var uid = 0
    private val sharedSkillViewModel by activityViewModels<SkillViewModel> {
        InjectorUtil.provideSkillViewModelFactory()
    }
    private val characterAttrViewModel by activityViewModels<CharacterAttrViewModel> {
        InjectorUtil.provideCharacterAttrViewModelFactory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireArguments().apply {
            uid = getInt(UID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCharacterSkillBinding.inflate(inflater, container, false)
        //延迟绘制页面
        binding.skillList.visibility = View.GONE
        binding.skillList.postDelayed({
            binding.skillList.visibility = View.VISIBLE
        }, 600L)
        init()
        return binding.root
    }

    private fun init() {
        binding.apply {
            shareSkillList = skillList
            //技能信息
            adapter = SkillAdapter()
            skillList.adapter = adapter
        }
        var level = CharacterAttrFragment.maxLv
        var atk = 0.0
        characterAttrViewModel.sumInfo.observe(viewLifecycleOwner) {
            atk = if (it.atk != 0.0) it.atk else it.magicStr
            sharedSkillViewModel.getCharacterSkills(level, atk.int, uid)
        }
        characterAttrViewModel.selData.observe(viewLifecycleOwner) {
            level = it[Constants.LEVEL] ?: level
            sharedSkillViewModel.getCharacterSkills(level, atk.int, uid)
        }
        sharedSkillViewModel.skills.observe(viewLifecycleOwner) {
            adapter.setSize(it.size)
            adapter.submitList(it) {
                adapter.notifyDataSetChanged()
            }
        }
    }

}