package cn.wthee.pcrtool.ui.tool.clan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.databinding.FragmentToolClanBossInfoBinding
import cn.wthee.pcrtool.ui.skill.SkillFragment
import cn.wthee.pcrtool.ui.tool.clan.ClanPagerFragment.Companion.clan
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
        fun getInstance(index: Int, section: Int) =
            ClanBossInfoFragment().apply {
                arguments = Bundle().apply {
                    putInt(Constants.CLAN_BOSS_NO, index)
                    putInt(Constants.CLAN_SELECT_SECTION, section)
                }
            }
    }

    private val viewModel by activityViewModels<ClanViewModel> {
        InjectorUtil.provideClanViewModelFactory()
    }
    private lateinit var binding: FragmentToolClanBossInfoBinding
    private var uid = -1
    private var index = 0
    private var selectSection = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireArguments().let {
            index = it.getInt(Constants.CLAN_BOSS_NO)
            selectSection = it.getInt(Constants.CLAN_SELECT_SECTION)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentToolClanBossInfoBinding.inflate(inflater, container, false)
        uid = clan.getUnitIdList(selectSection)[index]
        //技能
        childFragmentManager.beginTransaction()
            .replace(R.id.layout_skill, SkillFragment.getInstance(uid, 1))
            .commit()
        return binding.root
    }
}