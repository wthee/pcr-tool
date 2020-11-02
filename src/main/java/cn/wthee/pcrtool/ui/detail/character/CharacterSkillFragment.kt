package cn.wthee.pcrtool.ui.detail.character

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import cn.wthee.pcrtool.adapters.SkillAdapter
import cn.wthee.pcrtool.databinding.FragmentCharacterSkillBinding
import cn.wthee.pcrtool.utils.InjectorUtil


class CharacterSkillFragment : Fragment() {

    companion object {
        fun getInstance(id: Int, isDialog: Boolean = false): CharacterSkillFragment {
            val fragment = CharacterSkillFragment()
            val bundle = Bundle()
            bundle.putInt("id", id)
            bundle.putBoolean("isDialog", isDialog)
            fragment.arguments = bundle
            return fragment
        }
    }

    private lateinit var binding: FragmentCharacterSkillBinding
    private lateinit var adapter: SkillAdapter
    private var unitId = 0
    private var isDialog = false

    private val sharedSkillViewModel by activityViewModels<CharacterSkillViewModel> {
        InjectorUtil.provideCharacterSkillViewModelFactory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireArguments().let {
            unitId = it.getInt("id")
            isDialog = it.getBoolean("isDialog")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCharacterSkillBinding.inflate(inflater, container, false)

        binding.apply {
            //技能信息
            adapter = SkillAdapter()
            recycler.adapter = adapter

            //点击查看
            fabSkillLoop.setOnClickListener {
                CharacterSkillLoopDialogFragment().show(parentFragmentManager, "loop")
            }
        }
        //以悬浮窗显示时
        if (isDialog) {
            sharedSkillViewModel.getCharacterSkills(unitId)
        }
        sharedSkillViewModel.skills.observe(viewLifecycleOwner, {
            adapter.submitList(it)
        })
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.root.layoutTransition.setAnimateParentHierarchy(false);
    }
}