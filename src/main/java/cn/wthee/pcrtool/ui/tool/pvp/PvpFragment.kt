package cn.wthee.pcrtool.ui.tool.pvp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION
import android.provider.Settings.canDrawOverlays
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.PvpCharacterData
import cn.wthee.pcrtool.databinding.FragmentToolPvpBinding
import cn.wthee.pcrtool.ui.tool.pvp.PvpSelectFragment.Companion.character1
import cn.wthee.pcrtool.ui.tool.pvp.PvpSelectFragment.Companion.character2
import cn.wthee.pcrtool.ui.tool.pvp.PvpSelectFragment.Companion.character3
import cn.wthee.pcrtool.ui.tool.pvp.PvpSelectFragment.Companion.selects
import cn.wthee.pcrtool.utils.FabHelper
import cn.wthee.pcrtool.utils.ToastUtil
import cn.wthee.pcrtool.utils.ToolbarUtil
import java.io.Serializable

/**
 * 竞技场查询
 */
class PvpFragment : Fragment() {

    private lateinit var binding: FragmentToolPvpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        exitTransition = Hold()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        FabHelper.addBackFab()
        binding = FragmentToolPvpBinding.inflate(inflater, container, false)
        binding.pvpLike.transitionName = "liked_add"
        //设置头部
        ToolbarUtil(binding.toolPvp).setToolHead(
            R.drawable.ic_pvp,
            getString(R.string.tool_pvp)
        )
        //监听
        setListener()
        //显示选择角色布局
        childFragmentManager.beginTransaction()
            .replace(R.id.layout_select, PvpSelectFragment())
            .commit()
        return binding.root
    }

    private fun setListener() {
        binding.apply {
            pvpSearch.setOnClickListener {
                if (selects.contains(PvpCharacterData(0, 999))) {
                    ToastUtil.short("请选择 5 名角色~")
                } else {
                    //展示查询结果
                    PvpResultDialogFragment().show(parentFragmentManager, "pvp")
                }
            }
            //收藏页面
            pvpLike.setOnClickListener {
                val extras = FragmentNavigatorExtras(
                    pvpLike to pvpLike.transitionName
                )
                findNavController().navigate(
                    R.id.action_toolPvpFragment_to_pvpLikedFragment, null,
                    null,
                    extras
                )
            }
            //悬浮窗
            pvpFloat.setOnClickListener {
                //检查是否已经授予权限
                if (!canDrawOverlays(requireContext())) {
                    //若未授权则请求权限
                    getOverlayPermission()
                } else {
                    val intent =
                        Intent(requireActivity().applicationContext, PvpService::class.java)
                    requireActivity().stopService(intent)
                    intent.putExtra("character1", character1 as Serializable)
                    intent.putExtra("character2", character2 as Serializable)
                    intent.putExtra("character3", character3 as Serializable)
                    requireActivity().startService(intent)
                    //退回桌面
                    val home = Intent(Intent.ACTION_MAIN)
                    home.addCategory(Intent.CATEGORY_HOME)
                    startActivity(home)
                }
            }
        }
    }



    //请求悬浮窗权限
    private fun getOverlayPermission() {
        val intent = Intent(ACTION_MANAGE_OVERLAY_PERMISSION)
        intent.data = Uri.parse("package:" + requireActivity().packageName)
        startActivityForResult(intent, 0)
    }

}