package cn.wthee.pcrtool.ui.tool.pvp

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.adapter.PvpLikedAdapter
import cn.wthee.pcrtool.data.db.dao.PvpDao
import cn.wthee.pcrtool.data.entity.PvpLikedData
import cn.wthee.pcrtool.database.AppPvpDatabase
import cn.wthee.pcrtool.database.DatabaseUpdater
import cn.wthee.pcrtool.databinding.FragmentToolPvpLikedBinding
import cn.wthee.pcrtool.utils.FabHelper
import cn.wthee.pcrtool.utils.InjectorUtil
import cn.wthee.pcrtool.utils.ToolbarHelper
import cn.wthee.pcrtool.viewmodel.PvpLikedViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textview.MaterialTextView
import com.google.android.material.transition.MaterialContainerTransform
import kotlinx.coroutines.launch

/**
 * 竞技场收藏
 *
 * 页面布局 [FragmentToolPvpLikedBinding]
 *
 * ViewModels [PvpLikedViewModel]
 */
class PvpLikedFragment : Fragment() {

    private lateinit var binding: FragmentToolPvpLikedBinding
    private var region = DatabaseUpdater.getRegion()
    private lateinit var likedAdapter: PvpLikedAdapter
    private lateinit var dao: PvpDao
    private val viewModel by activityViewModels<PvpLikedViewModel> {
        InjectorUtil.providePvpViewModelFactory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //过渡
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            duration = 500L
            scrimColor = Color.TRANSPARENT
            setAllContainerColors(Color.TRANSPARENT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentToolPvpLikedBinding.inflate(inflater, container, false)
        init()
        setListener()
        lifecycleScope.launch {
            setSwipeDelete(AppPvpDatabase.getInstance().getPvpDao())
        }
        ToolbarHelper(binding.toolHead).setMainToolbar(
            R.drawable.ic_loved,
            getString(R.string.tool_pvp_liked)
        )
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        viewModel.getLiked(region)
    }

    private fun init() {
        dao = AppPvpDatabase.getInstance().getPvpDao()
        binding.root.transitionName = "liked"
        binding.fabAdd.transitionName = "liked_add"
        FabHelper.addBackFab(2)
        //初始化适配器
        likedAdapter = PvpLikedAdapter(false)
        binding.toolList.adapter = likedAdapter
        viewModel.getLiked(region)
        viewModel.allData.observe(viewLifecycleOwner) {
            likedAdapter.submitList(it) {
                updateTip(it)
            }
        }
    }

    private fun setListener() {
        //自定义
        binding.fabAdd.setOnClickListener {
            val extras = FragmentNavigatorExtras(
                it to it.transitionName
            )
            findNavController().navigate(
                R.id.action_pvpLikedFragment_to_pvpLikedCustomize, null,
                null, extras
            )
        }
    }

    private suspend fun setSwipeDelete(dao: PvpDao) {
        //列表设置左右滑动
        ItemTouchHelper(object :
            ItemTouchHelper.SimpleCallback(
                0,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            @SuppressLint("SimpleDateFormat")
            override fun onSwiped(
                viewHolder: RecyclerView.ViewHolder,
                direction: Int
            ) {
                lifecycleScope.launch {
                    val atks =
                        viewHolder.itemView.findViewById<MaterialTextView>(R.id.atk_ids)
                    val defs =
                        viewHolder.itemView.findViewById<MaterialTextView>(R.id.def_ids)
                    val type =
                        viewHolder.itemView.findViewById<MaterialTextView>(R.id.type)
                    val atkIds = atks.text.toString()
                    val defIds = defs.text.toString()
                    val typeInt = type.text.toString().toInt()
                    //删除记录
                    val deleteData = dao.getLiked(atkIds, defIds, region, typeInt)!!
                    dao.delete(deleteData)
                    viewModel.getLiked(region)
                    //显示撤回
                    Snackbar.make(binding.root, "已取消收藏~", Snackbar.LENGTH_LONG)
                        .setAction("撤回") {
                            //添加记录
                            lifecycleScope.launch {
                                dao.insert(deleteData)
                                viewModel.getLiked(region)
                            }
                        }
                        .show()
                }
            }
        }).attachToRecyclerView(binding.toolList)
    }

    private fun updateTip(list: List<PvpLikedData>) {
        binding.toolHead.title.text =
            if (list.isNotEmpty())
                getString(R.string.liked_count, list.size)
            else
                getString(R.string.tool_pvp_liked)
    }
}