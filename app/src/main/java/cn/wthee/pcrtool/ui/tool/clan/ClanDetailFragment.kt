package cn.wthee.pcrtool.ui.tool.clan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.adapter.ClanBossIconAdapter
import cn.wthee.pcrtool.data.view.ClanBattleInfo
import cn.wthee.pcrtool.databinding.FragmentToolClanDetailBinding
import cn.wthee.pcrtool.ui.skill.SkillFragment
import cn.wthee.pcrtool.ui.skill.SkillLoopDialogFragment
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.FabHelper
import cn.wthee.pcrtool.utils.InjectorUtil
import cn.wthee.pcrtool.utils.ToolbarHelper
import cn.wthee.pcrtool.viewmodel.ClanViewModel

/**
 * 团队战
 *
 * 页面布局 [FragmentToolClanDetailBinding]
 *
 * ViewModels [ClanViewModel]
 */
class ClanDetailFragment : Fragment() {

    private val viewModel by activityViewModels<ClanViewModel> {
        InjectorUtil.provideClanViewModelFactory()
    }
    private lateinit var binding: FragmentToolClanDetailBinding
    private var uid = -1
    private var date = ""
    private var index = 0
    private lateinit var clan: ClanBattleInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireArguments().let {
            date = it.getString(Constants.CLAN_DATE) ?: ""
            index = it.getInt(Constants.CLAN_BOSS_NO)
            clan = it.getSerializable(Constants.CLAN_DATA) as ClanBattleInfo
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        FabHelper.addBackFab(2)
        binding = FragmentToolClanDetailBinding.inflate(inflater, container, false)
        uid = clan.getUnitIdList(clan.section)[index]
        //图片列表
        val list = clan.getUnitIdList(1)
        val adapter = ClanBossIconAdapter(date, clan)
        binding.toolList.adapter = adapter
        adapter.submitList(list)
        //技能
        parentFragmentManager.beginTransaction()
            .replace(R.id.layout_skill, SkillFragment.getInstance(uid, 1))
            .commit()
        //技能循环
        binding.fabBossSkillLoop.setOnClickListener {
            SkillLoopDialogFragment.getInstance(uid)
                .show(parentFragmentManager, "loop")
        }
        //设置头部
        ToolbarHelper(binding.toolHead).setMainToolbar(
            R.drawable.ic_def, "${date} BOSS ${index + 1}"
        )
        return binding.root
    }
}