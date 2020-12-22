package cn.wthee.pcrtool.ui.detail.character.skill

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.adapter.SkillAdapter
import cn.wthee.pcrtool.databinding.FragmentCharacterSkillBinding
import cn.wthee.pcrtool.ui.home.CharacterViewModel
import cn.wthee.pcrtool.utils.Constants.UID
import cn.wthee.pcrtool.utils.InjectorUtil
import cn.wthee.pcrtool.utils.ToolbarUtil
import kotlinx.coroutines.launch

/**
 * 角色技能页面
 */

private const val DIALOG = "isDialog"

class CharacterSkillFragment : Fragment() {


    companion object {
        fun getInstance(uid: Int, isDialog: Boolean = false) =
            CharacterSkillFragment().apply {
                arguments = Bundle().apply {
                    putInt(UID, uid)
                    putBoolean(DIALOG, isDialog)
                }
            }
    }

    private lateinit var binding: FragmentCharacterSkillBinding
    private lateinit var adapter: SkillAdapter
    private var uid = 0
    private var isDialog = false
    private val sharedCharacterViewModel by activityViewModels<CharacterViewModel> {
        InjectorUtil.provideCharacterViewModelFactory()
    }
    private val sharedSkillViewModel by activityViewModels<CharacterSkillViewModel> {
        InjectorUtil.provideCharacterSkillViewModelFactory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireArguments().apply {
            uid = getInt(UID)
            isDialog = getBoolean(DIALOG)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCharacterSkillBinding.inflate(inflater, container, false)

        init()
        //以悬浮窗显示时
        if (isDialog) {
            setDialogLayout()
        }

        sharedSkillViewModel.skills.observe(viewLifecycleOwner, {
            adapter.submitList(it)
        })

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

    private fun setDialogLayout() {
        lifecycleScope.launch {
            binding.layoutLoopTitle.title.text =
                sharedCharacterViewModel.getCharacterData(uid).name
        }
        adapter.submitList(null)
        //修改fab位置
        binding.apply {
            layoutLoopTitle.root.visibility = View.VISIBLE
            ToolbarUtil(layoutLoopTitle).apply {
                setCenterTitle("")
                setRightIcon(R.drawable.ic_loop)
                rightIcon.setOnClickListener {
                    CharacterSkillLoopDialogFragment.getInstance(uid)
                        .show(parentFragmentManager, "loop")
                }
            }
        }
    }

}