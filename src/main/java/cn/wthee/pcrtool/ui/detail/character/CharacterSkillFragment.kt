package cn.wthee.pcrtool.ui.detail.character

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import cn.wthee.pcrtool.adapters.SkillAdapter
import cn.wthee.pcrtool.databinding.FragmentCharacterSkillBinding
import cn.wthee.pcrtool.utils.InjectorUtil
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class CharacterSkillFragment(
    val uid: Int = 0,
    val isDialog: Boolean = false
) : Fragment() {


    private lateinit var binding: FragmentCharacterSkillBinding
    private lateinit var adapter: SkillAdapter

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
                CharacterSkillLoopDialogFragment(uid).show(parentFragmentManager, "loop")
            }
        }
        //以悬浮窗显示时
        if (isDialog) {
            adapter.submitList(null)
            //修改fab位置
            binding.apply {
                fabSkillLoop.visibility = View.GONE
                layoutLoopTitle.visibility = View.VISIBLE
                openLoopListBtn.setOnClickListener {
                    fabSkillLoop.callOnClick()
                }
            }
        }

        viewModel.skills.observe(viewLifecycleOwner, {
            adapter.submitList(it) {
                binding.skillLoad.visibility = View.GONE
            }
        })
        lifecycleScope.launch {
            delay(400L)
            viewModel.getCharacterSkills(uid)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.root.layoutTransition.setAnimateParentHierarchy(false);
    }
}