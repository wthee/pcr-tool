package cn.wthee.pcrtool.ui.detail.character

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import cn.wthee.pcrtool.adapters.SkillLoopAdapter
import cn.wthee.pcrtool.databinding.FragmentSkillLoopBinding
import cn.wthee.pcrtool.utils.InjectorUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class CharacterSkillLoopDialogFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentSkillLoopBinding
    private lateinit var loopAdapter: SkillLoopAdapter
    private lateinit var beforeLoopadapter: SkillLoopAdapter
    private lateinit var loopSpAdapter: SkillLoopAdapter
    private lateinit var beforeSpLoopadapter: SkillLoopAdapter

    private val sharedSkillViewModel by activityViewModels<CharacterSkillViewModel> {
        InjectorUtil.provideCharacterSkillViewModelFactory()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSkillLoopBinding.inflate(inflater, container, false)
        binding.apply {
            //循环开始信息
            beforeLoopadapter = SkillLoopAdapter()
            beforeLoop.adapter = beforeLoopadapter
            beforeSpLoopadapter = SkillLoopAdapter()
            beforeLoopSp.adapter = beforeSpLoopadapter
            //循环信息
            loopAdapter = SkillLoopAdapter()
            looping.adapter = loopAdapter
            loopSpAdapter = SkillLoopAdapter()
            loopingSp.adapter = loopSpAdapter

            titleBeforeLoop.visibility = View.VISIBLE
            titleLooping.visibility = View.VISIBLE
        }
        //技能动作循环
        sharedSkillViewModel.acttackPattern.observe(viewLifecycleOwner, Observer {
            if (it.size > 1) {
                setSpVisibility(View.VISIBLE)
                beforeLoopadapter.submitList(it[0].getBefore()) {
                    beforeLoopadapter.notifyDataSetChanged()
                }
                loopAdapter.submitList(it[0].getLoop()) {
                    loopAdapter.notifyDataSetChanged()
                }
                beforeSpLoopadapter.submitList(it[1].getBefore()) {
                    beforeLoopadapter.notifyDataSetChanged()
                }
                loopSpAdapter.submitList(it[1].getLoop()) {
                    loopAdapter.notifyDataSetChanged()
                }
            } else {
                setSpVisibility(View.GONE)
                beforeLoopadapter.submitList(it[0].getBefore()) {
                    beforeLoopadapter.notifyDataSetChanged()
                }
                loopAdapter.submitList(it[0].getLoop()) {
                    loopAdapter.notifyDataSetChanged()
                }
            }
        })
        return binding.root
    }

    private fun setSpVisibility(visibility: Int) {
        binding.beforeLoopSp.visibility = visibility
        binding.loopingSp.visibility = visibility
        binding.titleBeforeLoopSp.visibility = visibility
        binding.titleLoopingSp.visibility = visibility
    }

}