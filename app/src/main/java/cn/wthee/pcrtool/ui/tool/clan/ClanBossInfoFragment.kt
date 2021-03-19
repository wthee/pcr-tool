package cn.wthee.pcrtool.ui.tool.clan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.view.ClanBattleInfo
import cn.wthee.pcrtool.databinding.FragmentToolClanBossInfoBinding
import cn.wthee.pcrtool.ui.skill.SkillFragment
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.InjectorUtil
import cn.wthee.pcrtool.viewmodel.ClanViewModel

/**
 * 团队战
 *
 * 页面布局 [FragmentToolClanBossInfoBinding]
 *
 * ViewModels [ClanViewModel]
 */
class ClanBossInfoFragment : Fragment() {

    companion object {
        fun getInstance(date: String, index: Int, clan: ClanBattleInfo) =
            ClanBossInfoFragment().apply {
                arguments = Bundle().apply {
                    putString(Constants.CLAN_DATE, date)
                    putInt(Constants.CLAN_BOSS_NO, index)
                    putSerializable(Constants.CLAN_DATA, clan)
                }
            }
    }

    private val viewModel by activityViewModels<ClanViewModel> {
        InjectorUtil.provideClanViewModelFactory()
    }
    private lateinit var binding: FragmentToolClanBossInfoBinding
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
        binding = FragmentToolClanBossInfoBinding.inflate(inflater, container, false)
        uid = clan.getUnitIdList(clan.section)[index]
        //技能
        parentFragmentManager.beginTransaction()
            .replace(R.id.layout_skill, SkillFragment.getInstance(uid, 1))
            .commit()
        return binding.root
    }
}