package cn.wthee.pcrtool.ui.tool.clan

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.adapter.ClanBossIconAdapter
import cn.wthee.pcrtool.adapter.viewpager.ClanBossPagerAdapter
import cn.wthee.pcrtool.data.view.ClanBattleInfo
import cn.wthee.pcrtool.databinding.FragmentToolClanPagerBinding
import cn.wthee.pcrtool.ui.skill.SkillLoopDialogFragment
import cn.wthee.pcrtool.utils.*
import com.google.android.material.transition.MaterialContainerTransform

/**
 * 团队战
 *
 * 页面布局 [FragmentToolClanPagerBinding]
 *
 * ViewModels []
 */
class ClanPagerFragment : Fragment() {

    companion object {
        lateinit var clan: ClanBattleInfo
    }

    private lateinit var binding: FragmentToolClanPagerBinding
    private var date = ""
    private var index = 0
    private lateinit var adapter: ClanBossIconAdapter
    private val REQUEST_CODE = 0
    private var selSection = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireArguments().let {
            date = it.getString(Constants.CLAN_DATE) ?: ""
            index = it.getInt(Constants.CLAN_BOSS_NO)
            clan = it.getSerializable(Constants.CLAN_DATA) as ClanBattleInfo
            selSection = clan.section
        }
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            scrimColor = Color.TRANSPARENT
            duration = 500L
            setAllContainerColors(ResourcesUtil.getColor(R.color.colorWhite))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        FabHelper.addBackFab(2)
        binding = FragmentToolClanPagerBinding.inflate(inflater, container, false)
        binding.root.transitionName = clan.clan_battle_id.toString()
        init()
        setListener()
        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE) {
            data?.extras?.let {
                val newSection = it.getInt(Constants.CLAN_SELECT_SECTION)
                if (selSection != newSection) {
                    selSection = newSection
                    //阶段切换
                    updateSection()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (this::binding.isInitialized) {
            binding.clanBossPager.adapter = null
        }
    }

    private fun setListener() {
        binding.clanBossPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                index = position
                //更新图标列表
                adapter.setSelectedIndex(index)
                adapter.notifyDataSetChanged()
            }

        })
        //技能循环
        binding.fabBossSkillLoop.setOnClickListener {
            SkillLoopDialogFragment.getInstance(clan.getUnitIdList(selSection)[index])
                .show(parentFragmentManager, "loop")
        }
        //阶段选择
        binding.fabSection.setOnClickListener {
            ClanSectionSelectDialogFragment(this, REQUEST_CODE).getInstance(
                clan.section,
                selSection
            ).show(parentFragmentManager, "section_select")
        }
    }

    private fun init() {
        //图片列表
        val list = clan.getUnitIdList(1)
        adapter = ClanBossIconAdapter(date, clan, binding)
        binding.toolList.adapter = adapter
        adapter.setSelectedIndex(index)
        adapter.submitList(list)
        updateSection()
    }

    private fun updateSection() {
        //BOSS viewpager 页面
        if (binding.clanBossPager.adapter == null) {
            val viewPagerAdapter = ClanBossPagerAdapter(childFragmentManager, lifecycle, selSection)
            binding.clanBossPager.adapter = viewPagerAdapter
            binding.clanBossPager.offscreenPageLimit = 1
            binding.clanBossPager.setCurrentItem(index, false)

        }
        binding.fabSection.text = getString(R.string.section, getZhNumberText(selSection))
        val fabColor = getSectionTextColor(selSection)
        binding.fabSection.setTextColor(fabColor)
        binding.fabSection.iconTint = ColorStateList.valueOf(fabColor)
    }

}