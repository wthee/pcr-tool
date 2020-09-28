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
    private lateinit var loopSpAdapter: SkillLoopAdapter
    private lateinit var beforeSpLoopadapter: SkillLoopAdapter
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
                beforeSpLoopadapter = SkillLoopAdapter()
                attactPattern.beforeLoopSp.adapter = beforeSpLoopadapter
                //循环信息
                loopAdapter = SkillLoopAdapter()
                attactPattern.looping.adapter = loopAdapter
                loopSpAdapter = SkillLoopAdapter()
                attactPattern.loopingSp.adapter = loopSpAdapter
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
                if(it.size >1 ){
                    binding.attactPattern.beforeLoopSp.visibility = View.VISIBLE
                    binding.attactPattern.loopingSp.visibility = View.VISIBLE
                    binding.attactPattern.titleBeforeLoopSp.visibility = View.VISIBLE
                    binding.attactPattern.titleLoopingSp.visibility = View.VISIBLE
                    beforeLoopadapter.submitList(it[0].getBefore()){
                        beforeLoopadapter.notifyDataSetChanged()
                    }
                    loopAdapter.submitList(it[0].getLoop()){
                        loopAdapter.notifyDataSetChanged()
                    }
                    beforeSpLoopadapter.submitList(it[1].getBefore()){
                        beforeLoopadapter.notifyDataSetChanged()
                    }
                    loopSpAdapter.submitList(it[1].getLoop()){
                        loopAdapter.notifyDataSetChanged()
                    }
                }else{
                    binding.attactPattern.beforeLoopSp.visibility = View.GONE
                    binding.attactPattern.loopingSp.visibility = View.GONE
                    binding.attactPattern.titleBeforeLoopSp.visibility = View.GONE
                    binding.attactPattern.titleLoopingSp.visibility = View.GONE
                    beforeLoopadapter.submitList(it[0].getBefore()){
                        beforeLoopadapter.notifyDataSetChanged()
                    }
                    loopAdapter.submitList(it[0].getLoop()){
                        loopAdapter.notifyDataSetChanged()
                    }
                }
            })
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.root.layoutTransition.setAnimateParentHierarchy(false);
    }
}