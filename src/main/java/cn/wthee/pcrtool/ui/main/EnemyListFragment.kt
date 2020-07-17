package cn.wthee.pcrtool.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.MainActivity
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.adapters.EnemyListAdapter
import cn.wthee.pcrtool.databinding.FragmentEnemyListBinding
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.InjectorUtil
import com.bumptech.glide.Glide


class EnemyListFragment : Fragment() {


    companion object {
        lateinit var viewModel: EnemyViewModel
        var filterFlag = 0
        lateinit var listAdapter: EnemyListAdapter
        lateinit var list: RecyclerView
    }

    private lateinit var binding: FragmentEnemyListBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEnemyListBinding.inflate(inflater, container, false)
        viewModel =
            InjectorUtil.provideEnemyViewModelFactory().create(EnemyViewModel::class.java)
        init()
        //设置监听
        setListener()
        //绑定观察
        setObserve()
        viewModel.getEnemyCount()
        return binding.root
    }

    private fun init() {
        binding.apply {
            list = recycler
            layoutRefresh.setColorSchemeColors(resources.getColor(R.color.colorPrimary, null))
            listAdapter = EnemyListAdapter()
            list.adapter = listAdapter
        }
        viewModel.getAllEnemy()
    }

    private fun setObserve() {
        viewModel.apply {
            //加载
            if (!isLoading.hasObservers()) {
                isLoading.observe(viewLifecycleOwner, Observer {
                    MainPagerFragment.progress.visibility = if (it) View.VISIBLE else View.GONE
                })
            }
            //获取信息
            if (!enemies.hasObservers()) {
                viewModel.enemies.observe(viewLifecycleOwner, Observer {
                    if (it != null && it.isNotEmpty()) {
                        binding.noDataTip.visibility = View.GONE
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
                        binding.noDataTip.visibility = View.VISIBLE
                    }
                })
            }
            //数量
            if (!enemyCount.hasObservers()) {
                enemyCount.observe(viewLifecycleOwner, Observer {
                    MainActivity.sp.edit {
                        putInt(Constants.SP_COUNT_ENEMY, it)
                    }
                    MainPagerFragment.tabLayout.getTabAt(2)?.text = it.toString()
                    binding.noDataTip.visibility = if (it != 0) View.GONE else View.VISIBLE
                })
            }

            //刷新
            if (!refresh.hasObservers()) {
                refresh.observe(viewLifecycleOwner, Observer {
                    binding.layoutRefresh.isRefreshing = it
                })
            }
        }
    }

    private fun setListener() {
        binding.apply {
            //滑动时暂停glide加载
            list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        Glide.with(root.context).resumeRequests()
                    } else {
                        Glide.with(root.context).pauseRequests()
                    }
                }
            })
            //下拉刷新
            layoutRefresh.setOnRefreshListener {
                viewModel.getAllEnemy()
            }

        }
    }
}