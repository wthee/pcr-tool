package cn.wthee.pcrtool.ui.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import cn.wthee.pcrtool.databinding.FragmentToolsBinding
import cn.wthee.pcrtool.utils.FabHelper


class ToolsFragment : Fragment() {

    private lateinit var binding: FragmentToolsBinding
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = FragmentToolsBinding.inflate(inflater, container, false)
        //添加返回fab
        FabHelper.addBackFab()
        return binding.root
    }

}