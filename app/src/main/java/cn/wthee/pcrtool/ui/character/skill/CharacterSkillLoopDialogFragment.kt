package cn.wthee.pcrtool.ui.character.skill

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.adapter.SkillLoopAllAdapter
import cn.wthee.pcrtool.data.model.SkillLoop
import cn.wthee.pcrtool.databinding.FragmentCharacterSkillLoopBinding
import cn.wthee.pcrtool.ui.common.CommonBottomSheetDialogFragment
import cn.wthee.pcrtool.utils.Constants.UID
import cn.wthee.pcrtool.utils.InjectorUtil
import cn.wthee.pcrtool.viewmodel.SkillViewModel

/**
 * 角色技能循环页面
 *
 * 根据 [uid] 显示角色数据
 *
 * 页面布局 [FragmentCharacterSkillLoopBinding]
 *
 * ViewModels [SkillViewModel]
 */
class CharacterSkillLoopDialogFragment : CommonBottomSheetDialogFragment(true) {

    companion object {
        fun getInstance(uid: Int) =
            CharacterSkillLoopDialogFragment().apply {
                arguments = Bundle().apply {
                    putInt(UID, uid)
                }
            }
    }

    private lateinit var binding: FragmentCharacterSkillLoopBinding
    private var uid = 0
    private val sharedSkillViewModel by activityViewModels<SkillViewModel> {
        InjectorUtil.provideSkillViewModelFactory()
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
        binding = FragmentCharacterSkillLoopBinding.inflate(inflater, container, false)
        val adapter = SkillLoopAllAdapter()
        binding.skillLoopList.adapter = adapter

        sharedSkillViewModel.getCharacterSkillLoops(uid)
        //技能动作循环
        sharedSkillViewModel.atlPattern.observe(viewLifecycleOwner) {
            val loops = arrayListOf<SkillLoop>()
            if (it.size > 1) {
                it.forEach {
                    if (it.getBefore().size > 0) {
                        loops.add(SkillLoop(getString(R.string.before_loop), it.getBefore()))
                    }
                    if (it.getLoop().size > 0) {
                        loops.add(SkillLoop(getString(R.string.looping), it.getLoop()))
                    }
                }
            } else {
                loops.add(SkillLoop(getString(R.string.before_loop), it[0].getBefore()))
                loops.add(SkillLoop(getString(R.string.looping), it[0].getLoop()))
            }
            adapter.submitList(loops) {
                adapter.notifyDataSetChanged()
            }
        }
        return binding.root
    }


}