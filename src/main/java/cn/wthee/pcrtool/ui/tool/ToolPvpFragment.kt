package cn.wthee.pcrtool.ui.tool

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.adapters.PvpCharacterPageAdapter
import cn.wthee.pcrtool.adapters.PvpCharactertAdapter
import cn.wthee.pcrtool.data.model.entity.getDefault
import cn.wthee.pcrtool.databinding.FragmentToolPvpBinding
import com.google.android.material.tabs.TabLayoutMediator


class ToolPvpFragment : Fragment() {
    companion object {
        var selects = getDefault()
        lateinit var pvpCharactertAdapter: PvpCharactertAdapter
    }

    private lateinit var binding: FragmentToolPvpBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentToolPvpBinding.inflate(inflater, container, false)
        //已选择角色
        pvpCharactertAdapter = PvpCharactertAdapter()
        binding.selectCharacters.adapter = pvpCharactertAdapter
        pvpCharactertAdapter.submitList(selects)
        pvpCharactertAdapter.notifyDataSetChanged()
        //角色页面 绑定tab viewpager
        binding.pvpPager.offscreenPageLimit = 2
        binding.pvpPager.adapter = PvpCharacterPageAdapter(requireActivity())
        TabLayoutMediator(
            binding.tablayoutPosition,
            binding.pvpPager
        ) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = getString(R.string.position_1)
                }
                1 -> {
                    tab.text = getString(R.string.position_2)
                }
                2 -> {
                    tab.text = getString(R.string.position_3)
                }
            }
        }.attach()
        //查询
        //TODO move
//        MainScope().launch {
//            val service = ApiHelper.create(PVPService::class.java, Constants.API_URL_PVP)
//            service.getData(1, "103401,100201,101001,105201,105801")
//                .enqueue(object : Callback<PVPData> {
//                    override fun onResponse(call: Call<PVPData>, response: Response<PVPData>) {
//                        val body = response.body()
//                        if (body == null || body.code != 0) {
//                            ToastUtil.short("查询失败，请稍后重试~")
//                        } else {
//                            val data = body.message
//                            Log.e("todo", data)
//                        }
//                    }
//
//                    override fun onFailure(call: Call<PVPData>, t: Throwable) {
//                        ToastUtil.short("查询失败，请稍后重试~")
//                    }
//                }
//                )
//        }
        return binding.root
    }

}