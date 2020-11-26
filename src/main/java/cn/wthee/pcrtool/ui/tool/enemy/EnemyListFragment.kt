package cn.wthee.pcrtool.ui.tool.enemy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.MainActivity
import cn.wthee.pcrtool.MainPagerFragment
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.adapters.EnemyListAdapter
import cn.wthee.pcrtool.databinding.FragmentToolEnemyBinding
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.FabHelper
import cn.wthee.pcrtool.utils.InjectorUtil
import cn.wthee.pcrtool.utils.ResourcesUtil


class EnemyListFragment : Fragment() {

    private lateinit var binding: FragmentToolEnemyBinding
    private lateinit var viewModel: EnemyViewModel
    var filterFlag = 0
    private lateinit var listAdapter: EnemyListAdapter
    private lateinit var list: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        FabHelper.addBackFab()
        binding = FragmentToolEnemyBinding.inflate(inflater, container, false)
        viewModel =
            InjectorUtil.provideEnemyViewModelFactory().create(EnemyViewModel::class.java)
        init()
        //绑定观察
        setObserve()
        return binding.root
    }

    private fun init() {
        binding.apply {
            list = recycler
            listAdapter = EnemyListAdapter(parentFragmentManager)
            list.adapter = listAdapter
            //设置头部
            toolEnemy.apply {
                toolIcon.setImageDrawable(ResourcesUtil.getDrawable(R.drawable.ic_enemy))
                toolTitle.text = getString(R.string.tool_enemy)
            }
        }
        viewModel.getAllEnemy()
    }

    private fun setObserve() {
        viewModel.apply {
            //加载
            if (!isLoading.hasObservers()) {
                isLoading.observe(viewLifecycleOwner, {
//                    MainPagerFragment.progress.visibility = if (it) View.VISIBLE else View.GONE
                })
            }
            //获取信息
            if (!enemies.hasObservers()) {
                viewModel.enemies.observe(viewLifecycleOwner, {
                    if (it != null && it.isNotEmpty()) {
                        MainPagerFragment.tipText.visibility = View.GONE
                        listAdapter.submitList(it) {
                            if (filterFlag.toString() != "0") {
                                listAdapter.filter.filter(
                                    filterFlag.toString()
                                )
                            }
                            MainActivity.sp.edit {
                                putInt(Constants.SP_COUNT_ENEMY, it.size)
                            }
                            MainPagerFragment.tabLayout.getTabAt(2)?.text = it.size.toString()
                        }
                    } else {
                        MainPagerFragment.tipText.visibility = View.VISIBLE
                    }
                })
            }
        }
    }

}