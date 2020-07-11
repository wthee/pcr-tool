package cn.wthee.pcrtool.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.MainActivity
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.adapters.EnemyListAdapter
import cn.wthee.pcrtool.databinding.FragmentEnemyListBinding
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.InjectorUtil
import com.bumptech.glide.Glide
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Singleton

@Singleton
class EnemyListFragment : Fragment() {


    private lateinit var binding: FragmentEnemyListBinding
    private lateinit var adapter: EnemyListAdapter

    private val viewModel =
        InjectorUtil.provideEnemyViewModelFactory().create(EnemyViewModel::class.java)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEnemyListBinding.inflate(inflater, container, false)
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
            layoutRefresh.setColorSchemeColors(resources.getColor(R.color.colorPrimary, null))
            adapter = EnemyListAdapter()
            recycler.adapter = adapter
        }
    }

    private fun setObserve() {
        //加载
        if (!viewModel.isLoading.hasObservers()) {
            viewModel.isLoading.observe(viewLifecycleOwner, Observer {
                MainPagerFragment.progress.visibility = if (it) View.VISIBLE else View.GONE
            })
        }
        //获取信息
        lifecycleScope.launch {
            @OptIn(ExperimentalCoroutinesApi::class)
            viewModel.data.collectLatest {
                adapter.submitData(it)
            }
        }
        viewModel.enemyCount.observe(viewLifecycleOwner, Observer {
            MainActivity.sp.edit {
                putInt(Constants.SP_COUNT_ENEMY, it)
            }
            MainPagerFragment.tabLayout.getTabAt(2)?.text = it.toString()
            binding.noDataTip.visibility = if (it != 0) View.GONE else View.VISIBLE
        })

        //刷新
        viewModel.refresh.observe(viewLifecycleOwner, Observer {
            binding.layoutRefresh.isRefreshing = it
        })
    }

    private fun setListener() {
        binding.apply {
            //滑动时暂停glide加载
            recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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
//                viewModel.getAllEnemy()
            }

        }
    }
}