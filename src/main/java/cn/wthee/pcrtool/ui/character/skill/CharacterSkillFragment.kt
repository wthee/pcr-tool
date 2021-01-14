package cn.wthee.pcrtool.ui.character.skill

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import cn.wthee.pcrtool.adapter.SkillAdapter
import cn.wthee.pcrtool.databinding.FragmentCharacterSkillBinding
import cn.wthee.pcrtool.utils.Constants.UID
import cn.wthee.pcrtool.utils.InjectorUtil
import cn.wthee.pcrtool.utils.ShareIntentUtil

/**
 * 角色技能页面
 */

private const val DIALOG = "isDialog"

class CharacterSkillFragment : Fragment() {


    companion object {
        fun getInstance(uid: Int) =
            CharacterSkillFragment().apply {
                arguments = Bundle().apply {
                    putInt(UID, uid)
                }
            }
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
        binding.root.visibility = View.GONE
        binding.root.postDelayed({
            binding.root.visibility = View.VISIBLE
        }, 500L)
        init()
        sharedSkillViewModel.skills.observe(viewLifecycleOwner, {
            adapter.submitList(it)
        })
        //分享长图
        binding.skillShare.setOnClickListener {
            ShareIntentUtil.imageLong(requireActivity(), binding.skillList, "skill_${uid}.png")
        }
        return binding.root
    }

    private fun init() {
        binding.apply {
            //技能信息
            adapter = SkillAdapter()
            skillList.adapter = adapter
        }
        sharedSkillViewModel.getCharacterSkills(uid)
    }

}