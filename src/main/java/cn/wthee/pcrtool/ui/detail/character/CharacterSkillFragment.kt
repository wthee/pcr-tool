package cn.wthee.pcrtool.ui.detail.character

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import cn.wthee.pcrtool.adapters.SkillAdapter
import cn.wthee.pcrtool.adapters.SkillLoopAdapter
import cn.wthee.pcrtool.databinding.FragmentCharacterSkillBinding
import cn.wthee.pcrtool.utils.InjectorUtil
import kotlinx.coroutines.launch


class CharacterSkillFragment : Fragment() {

    companion object {
        fun getInstance(id: Int): CharacterSkillFragment {
            val fragment = CharacterSkillFragment()
            val bundle = Bundle()
            bundle.putInt("id", id)
            fragment.arguments = bundle
            return fragment
        }
    }

    private lateinit var binding: FragmentCharacterSkillBinding
    private lateinit var adapter: SkillAdapter
    private lateinit var loopAdapter: SkillLoopAdapter
    private lateinit var beforeLoopadapter: SkillLoopAdapter
    private var unitId = 0

    private val sharedSkillViewModel by activityViewModels<CharacterSkillViewModel> {
        InjectorUtil.provideCharacterSkillViewModelFactory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireArguments().let {
            unitId = it.getInt("id")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCharacterSkillBinding.inflate(inflater, container, false)

        viewLifecycleOwner.lifecycleScope.launch {
            binding.apply {
                //循环开始信息
                beforeLoopadapter = SkillLoopAdapter()
                attactPattern.beforeLoop.adapter = beforeLoopadapter
                //循环信息
                loopAdapter = SkillLoopAdapter()
                attactPattern.looping.adapter = loopAdapter
                //技能信息
                adapter = SkillAdapter()
                recycler.adapter = adapter

                attactPattern.apply {
                    titleBeforeLoop.visibility = View.VISIBLE
                    titleLooping.visibility = View.VISIBLE
                }
            }

            sharedSkillViewModel.skills.observe(viewLifecycleOwner, Observer {
                adapter.submitList(it)
            })

            sharedSkillViewModel.acttackPattern.observe(viewLifecycleOwner, Observer {
                beforeLoopadapter.submitList(it.getBefore())
                loopAdapter.submitList(it.getLoop())
            })
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.root.layoutTransition.setAnimateParentHierarchy(false);
    }
}