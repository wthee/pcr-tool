package cn.wthee.pcrtool.ui.detail.character

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.adapters.SkillAdapter
import cn.wthee.pcrtool.databinding.FragmentCharacterSkillBinding
import cn.wthee.pcrtool.ui.main.CharacterViewModel
import cn.wthee.pcrtool.utils.Constants.UID
import cn.wthee.pcrtool.utils.InjectorUtil
import cn.wthee.pcrtool.utils.ToolbarUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class CharacterSkillFragment : Fragment() {

    private val DIALOG = "isDialog"

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            uid = getInt(UID)
            isDialog = getBoolean(DIALOG)
        }
        if (!isDialog) {
            uid = CharacterPagerFragment.uid
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCharacterSkillBinding.inflate(inflater, container, false)

        val viewModel = InjectorUtil.provideCharacterSkillViewModelFactory()
            .create(CharacterSkillViewModel::class.java)

        binding.apply {
            //技能信息
            adapter = SkillAdapter()
            recycler.adapter = adapter
            //点击查看
            fabSkillLoop.setOnClickListener {
                CharacterSkillLoopDialogFragment.getInstance(uid)
                    .show(parentFragmentManager, "loop")
            }
        }
        //以悬浮窗显示时
        if (isDialog) {
            lifecycleScope.launch {
                binding.layoutLoopTitle.title.text =
                    sharedCharacterViewModel.getCharacterData(uid).name
            }
            adapter.submitList(null)
            //修改fab位置
            binding.apply {
                fabSkillLoop.visibility = View.GONE
                layoutLoopTitle.root.visibility = View.VISIBLE
                ToolbarUtil(layoutLoopTitle).apply {
                    setCenterTitle("")
                    setRightIcon(R.drawable.ic_loop)
                    rightIcon.setOnClickListener {
                        fabSkillLoop.callOnClick()
                    }
                    leftIcon.setOnClickListener {
                        (parentFragment as BottomSheetDialogFragment).dismiss()
                    }
                }
            }
        }

        viewModel.skills.observe(viewLifecycleOwner, {
            adapter.submitList(it)
        })
        lifecycleScope.launch {
            delay(400L)
            viewModel.getCharacterSkills(uid)
        }
        return binding.root
    }

}