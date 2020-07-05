package cn.wthee.pcrtool.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.MainActivity
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.adapters.EquipmentAdapter
import cn.wthee.pcrtool.databinding.FragmentEquipmentListBinding
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.InjectorUtil
import com.bumptech.glide.Glide
import javax.inject.Singleton

@Singleton
class EquipmentListFragment : Fragment() {

    companion object {
        lateinit var list: RecyclerView
        lateinit var adapter: EquipmentAdapter
        var isList = true
        lateinit var viewModel: EquipmentViewModel
    }
    private lateinit var binding: FragmentEquipmentListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEquipmentListBinding.inflate(inflater, container, false)
        viewModel = InjectorUtil.provideEquipmentViewModelFactory()
            .create(EquipmentViewModel::class.java)
        init(isList)
        //设置监听
        setListener()
        //绑定观察
        setObserve()
        viewModel.getEquips()
        return binding.root
    }

    private fun init(isList: Boolean) {
        binding.apply {
            layoutRefresh.setColorSchemeColors(resources.getColor(R.color.colorPrimary, null))
            list = recycler
            if (isList) {
                val linearLayoutManager = LinearLayoutManager(MyApplication.getContext())
                linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
                recycler.layoutManager = linearLayoutManager
            } else {
                val gridLayoutManager =
                    GridLayoutManager(MyApplication.getContext(), Constants.COLUMN_COUNT_EQUIP)
                gridLayoutManager.orientation = GridLayoutManager.VERTICAL
                recycler.layoutManager = gridLayoutManager
            }
            adapter = EquipmentAdapter(isList)
            recycler.adapter =
                adapter
        }
    }

    private fun setObserve(){
        //加载
        viewModel.isLoading.observe(viewLifecycleOwner, Observer {
            binding.characterProgress.visibility = if (it) View.VISIBLE else View.GONE
        })
        viewModel.isList.observe(viewLifecycleOwner, Observer {
            init(it)
            viewModel.getEquips()
        })
        //获取信息
        viewModel.equipments.observe(viewLifecycleOwner, Observer {data ->
            adapter.submitList(data)
            MainActivity.sp.edit {
                putInt(Constants.SP_COUNT_EQUIP, data.size)
            }
            ContainerFragment.tabLayout.getTabAt(1)?.text = data.size.toString()
//            adapter.notifyDataSetChanged()
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
                viewModel.getEquips()
            }

        }
    }
}