package cn.wthee.pcrtool.ui.tool.pvp

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION
import android.provider.Settings.canDrawOverlays
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.model.PvpPositionData
import cn.wthee.pcrtool.data.view.PvpCharacterData
import cn.wthee.pcrtool.data.view.getIdStr
import cn.wthee.pcrtool.databinding.FragmentToolPvpBinding
import cn.wthee.pcrtool.ui.tool.pvp.PvpIconFragment.Companion.selects
import cn.wthee.pcrtool.utils.*
import cn.wthee.pcrtool.viewmodel.CharacterViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


/**
 * 竞技场查询
 *
 * 页面布局 [FragmentToolPvpBinding]
 *
 * ViewModels [CharacterViewModel]
 */
class PvpFragment : Fragment() {

    companion object {
        var r6Ids = listOf<Int>()
    }

    private lateinit var binding: FragmentToolPvpBinding
    private lateinit var job: Job
    private val sharedCharacterViewModel by activityViewModels<CharacterViewModel> {
        InjectorUtil.provideCharacterViewModelFactory()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        FabHelper.addBackFab()
        binding = FragmentToolPvpBinding.inflate(inflater, container, false)
        binding.pvpLike.transitionName = "liked_add"
        //设置头部
        ToolbarHelper(binding.toolPvp).setMainToolbar(
            R.drawable.ic_pvp,
            getString(R.string.tool_pvp)
        )
        //监听
        setListener()

        lifecycleScope.launch {
            //获取六星id
            r6Ids = sharedCharacterViewModel.getR6Ids()
            childFragmentManager.beginTransaction()
                .replace(R.id.layout_select, PvpIconFragment())
                .commit()
        }
        return binding.root
    }

    private fun setListener() {
        binding.apply {
            //来源
            pcrfan.setOnClickListener {
                //从其他浏览器打开
                BrowserUtil.open(requireContext(), getString(R.string.url_pcrdfans_com))
            }
            //搜索
            pvpSearch.setOnClickListener {
                if (selects.contains(PvpCharacterData(0, 999))) {
                    ToastUtil.short("请选择 5 名角色~")
                } else {
                    //展示查询结果
                    PvpResultDialogFragment.getInstance(selects.getIdStr()).show(
                        parentFragmentManager,
                        "pvp"
                    )
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
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !canDrawOverlays(
                        requireContext()
                    )
                ) {
                    //若未授权则请求权限
                    getOverlayPermission()
                } else {
                    lifecycleScope.launch {
                        val list = sharedCharacterViewModel.getAllPvp()
                        if (list != null && list.isNotEmpty()) {
                            val allCharacter = arrayListOf<PvpPositionData>()
                            val character1 = list.filter {
                                it.position in 0..299
                            }
                            val character2 = list.filter {
                                it.position in 300..599
                            }
                            val character3 = list.filter {
                                it.position in 600..9999
                            }
                            allCharacter.add(PvpPositionData(1, character1))
                            allCharacter.add(PvpPositionData(2, character2))
                            allCharacter.add(PvpPositionData(3, character3))
                            val intent =
                                Intent(requireActivity().applicationContext, PvpService::class.java)
                            requireActivity().stopService(intent)
                            val bundle = Bundle().also {
                                it.putSerializable("allCharacters", allCharacter)
                            }
                            intent.putExtra("data", bundle)
                            requireActivity().startService(intent)
                            //退回桌面
                            val home = Intent(Intent.ACTION_MAIN)
                            home.addCategory(Intent.CATEGORY_HOME)
                            startActivity(home)
                        } else {
                            ToastUtil.short("悬浮窗打开异常，请重试~")
                        }

                    }
                }
            }
        }
    }


    //请求悬浮窗权限
    @RequiresApi(Build.VERSION_CODES.M)
    private fun getOverlayPermission() {
        val intent = Intent(ACTION_MANAGE_OVERLAY_PERMISSION)
        intent.data = Uri.parse("package:" + requireActivity().packageName)
        startActivityForResult(intent, 0)
    }

}