package cn.wthee.pcrtool.ui.tool.pvp

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.adapter.PvpLikedAdapter
import cn.wthee.pcrtool.data.db.dao.PvpDao
import cn.wthee.pcrtool.data.db.entity.PvpLikedData
import cn.wthee.pcrtool.database.AppPvpDatabase
import cn.wthee.pcrtool.database.DatabaseUpdater
import cn.wthee.pcrtool.databinding.FragmentToolPvpLikedBinding
import cn.wthee.pcrtool.utils.FabHelper
import cn.wthee.pcrtool.utils.ToolbarHelper
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textview.MaterialTextView
import com.google.android.material.transition.MaterialContainerTransform
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


class PvpLikedFragment : Fragment() {

    private lateinit var binding: FragmentToolPvpLikedBinding
    private var region = DatabaseUpdater.getRegion()
    private lateinit var likedAdapter: PvpLikedAdapter
    private var allData = listOf<PvpLikedData>()
    private lateinit var dao: PvpDao

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
        lifecycleScope.launch {
            allData = dao.getAll(region)
        }
    }

    private fun init() {
        dao = AppPvpDatabase.getInstance().getPvpDao()
        binding.root.transitionName = "liked"
        binding.fabAdd.transitionName = "liked_add"
        FabHelper.addBackFab(2)
        //初始化适配器
        likedAdapter = PvpLikedAdapter(false)
        binding.toolList.adapter = likedAdapter
        lifecycleScope.launch {
            allData = dao.getAll(region)
            likedAdapter.submitList(allData) {
                updateTip()
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
        //回到顶部
        binding.fabTop.setOnClickListener {
            binding.root.transitionToStart()
            binding.toolList.scrollToPosition(0)
        }
    }

    private suspend fun setSwipeDelete(dao: PvpDao) {
        val data = dao.getAll(region)
        likedAdapter.submitList(data) {
            updateTip()
        }

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
                MainScope().launch {
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
                    allData = dao.getAll(region)
                    likedAdapter.submitList(allData) {
                        updateTip()
                    }
                    //显示撤回
                    Snackbar.make(binding.root, "已取消收藏~", Snackbar.LENGTH_LONG)
                        .setAction("撤回") {
                            //添加记录
                            lifecycleScope.launch {
                                dao.insert(deleteData)
                                allData = dao.getAll(region)
                                likedAdapter.submitList(allData) {
                                    updateTip()
                                }
                            }
                        }
                        .show()
                }
            }
        }).attachToRecyclerView(binding.toolList)
    }

    private fun updateTip() {
        binding.tip.text =
            if (allData.isNotEmpty())
                getString(R.string.liked_count, allData.size)
            else
                getString(R.string.no_liked_data)
    }
}