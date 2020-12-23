package cn.wthee.pcrtool.ui.detail.character.skill

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.adapter.SkillLoopAllAdapter
import cn.wthee.pcrtool.data.bean.SkillLoop
import cn.wthee.pcrtool.databinding.FragmentSkillLoopBinding
import cn.wthee.pcrtool.ui.common.CommonBasicDialogFragment
import cn.wthee.pcrtool.utils.Constants.UID
import cn.wthee.pcrtool.utils.InjectorUtil

/**
 * 角色技能循环页面
 */
class CharacterSkillLoopDialogFragment : CommonBasicDialogFragment() {

    companion object {
        fun getInstance(uid: Int) =
            CharacterSkillLoopDialogFragment().apply {
                arguments = Bundle().apply {
                    putInt(UID, uid)
                }
            }
    }

    private lateinit var binding: FragmentSkillLoopBinding
    private var uid = 0

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
        binding = FragmentSkillLoopBinding.inflate(inflater, container, false)
        val adapter = SkillLoopAllAdapter()
        binding.skillLoopList.adapter = adapter

        val viewModel = InjectorUtil.provideCharacterSkillViewModelFactory()
            .create(CharacterSkillViewModel::class.java)

        viewModel.getCharacterSkillLoops(uid)
        //技能动作循环
        viewModel.atlPattern.observe(viewLifecycleOwner, {
            val loops = arrayListOf<SkillLoop>()
            if (it.size > 1) {
                loops.add(SkillLoop(getString(R.string.before_loop), it[0].getBefore()))
                loops.add(SkillLoop(getString(R.string.looping), it[0].getLoop()))
                loops.add(SkillLoop(getString(R.string.before_loop), it[1].getBefore()))
                loops.add(SkillLoop(getString(R.string.title_looping_sp), it[1].getLoop()))
            } else {
                loops.add(SkillLoop(getString(R.string.before_loop), it[0].getBefore()))
                loops.add(SkillLoop(getString(R.string.looping), it[0].getLoop()))
            }
            adapter.submitList(loops) {
                adapter.notifyDataSetChanged()
            }
        })
        return binding.root
    }


}