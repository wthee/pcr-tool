package cn.wthee.pcrtool.ui.character.skill

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.adapter.SkillAdapter
import cn.wthee.pcrtool.databinding.FragmentCharacterSkillBinding
import cn.wthee.pcrtool.utils.Constants.UID
import cn.wthee.pcrtool.utils.InjectorUtil

/**
 * 角色技能页面
 *
 * 根据 [uid] 显示角色数据
 *
 * 页面布局 [FragmentCharacterSkillBinding]
 *
 * ViewModels [CharacterSkillViewModel]
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
    private val sharedSkillViewModel by activityViewModels<CharacterSkillViewModel> {
        InjectorUtil.provideCharacterSkillViewModelFactory()
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
        sharedSkillViewModel.getCharacterSkills(uid)
        sharedSkillViewModel.skills.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }

}