package cn.wthee.pcrtool.ui.tool.pvp

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.adapter.PvpLikedAdapter
import cn.wthee.pcrtool.data.db.entity.PvpLikedData
import cn.wthee.pcrtool.database.AppPvpDatabase
import cn.wthee.pcrtool.database.DatabaseUpdater
import cn.wthee.pcrtool.databinding.FragmentToolPvpLikedBinding
import cn.wthee.pcrtool.utils.FabHelper
import cn.wthee.pcrtool.utils.InjectorUtil
import cn.wthee.pcrtool.utils.RecyclerViewHelper.setScrollToTopListener
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textview.MaterialTextView
import com.google.android.material.transition.MaterialContainerTransform
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class PvpLikedFragment : Fragment() {

    private lateinit var binding: FragmentToolPvpLikedBinding

    private val viewModel =
        InjectorUtil.providePvpViewModelFactory().create(PvpLikedViewModel::class.java)
    private var region = DatabaseUpdater.getRegion()
    private lateinit var adapter: PvpLikedAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //过渡
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            duration = resources.getInteger(R.integer.fragment_anim).toLong()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentToolPvpLikedBinding.inflate(inflater, container, false)
        init()
        setListener(adapter)
        viewModel.data.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it) {
                updateTip(it)
            }
        })
        return binding.root
    }

    private fun updateTip(it: List<PvpLikedData>) {
        binding.likeTip.text =
            if (it.isNotEmpty())
                getString(R.string.liked_count, it.size)
            else
                getString(R.string.no_liked_data)
    }

    private fun init() {
        binding.root.transitionName = "liked"
        FabHelper.addBackFab(2)
        //获取数据版本
        viewModel.getLiked(region)
        //初始化适配器
        adapter = PvpLikedAdapter(requireActivity(), false)
        binding.listLiked.adapter = adapter
    }

    private fun setListener(adapter: PvpLikedAdapter) {
        //列表设置左右滑动
        ItemTouchHelper(object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            @SuppressLint("SimpleDateFormat")
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                lifecycleScope.launch {
                    val dao = AppPvpDatabase.getInstance().getPvpDao()
                    val atks = viewHolder.itemView.findViewById<MaterialTextView>(R.id.atk_ids)
                    val defs = viewHolder.itemView.findViewById<MaterialTextView>(R.id.def_ids)
                    val atkIds = atks.text.toString()
                    val defIds = defs.text.toString()
                    val data = dao.get(atkIds, defIds, region)!!
                    //删除记录
                    dao.delete(data)
                    val result = dao.getAll(region)
                    adapter.submitList(result) {
                        updateTip(result)
                    }
                    //显示撤回
                    Snackbar.make(binding.root, "已取消收藏~", Snackbar.LENGTH_LONG)
                        .setAction("撤回") {
                            //添加记录
                            lifecycleScope.launch {
                                dao.insert(data)
                                val list = dao.getAll(region)
                                val position = list.indexOf(data)
                                adapter.submitList(list) {
                                    updateTip(list)
                                }
                                delay(500L)
                                binding.listLiked.smoothScrollToPosition(position)
                            }
                        }
                        .show()
                }
            }
        }).attachToRecyclerView(binding.listLiked)
        //滚动监听
        binding.listLiked.setScrollToTopListener(binding.fabTop)

    }


}