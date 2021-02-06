package cn.wthee.pcrtool.ui.tool.pvp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import cn.wthee.pcrtool.adapter.PvpCharacterAdapter
import cn.wthee.pcrtool.databinding.FragmentToolPvpCharacterBinding
import cn.wthee.pcrtool.ui.tool.pvp.PvpSelectFragment.Companion.character1
import cn.wthee.pcrtool.ui.tool.pvp.PvpSelectFragment.Companion.character2
import cn.wthee.pcrtool.ui.tool.pvp.PvpSelectFragment.Companion.character3
import kotlinx.coroutines.launch

/**
 * 竞技场角色 ViewPager
 *
 * 根据位置 [position] 悬浮[isFloatWindow] ，加载数据
 *
 * 页面布局 [FragmentToolPvpCharacterBinding]
 *
 * ViewModels []
 */
private const val POSITION = "positon"
private const val FLOAT = "float"

class PvpPagerFragment : Fragment() {

    companion object {
        fun getInstance(position: Int, isFloatWindow: Boolean) =
            PvpPagerFragment().apply {
                arguments = Bundle().apply {
                    putInt(POSITION, position)
                    putSerializable(FLOAT, isFloatWindow)
                }
            }
    }

    private lateinit var binding: FragmentToolPvpCharacterBinding
    private var position = 0
    private var isFloatWindow = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireArguments().apply {
            position = getInt(POSITION)
            isFloatWindow = getBoolean(FLOAT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentToolPvpCharacterBinding.inflate(inflater, container, false)
        lifecycleScope.launch {

            val adapter = PvpCharacterAdapter(isFloatWindow)
//            adapter.setHasStableIds(!isFloatWindow)
            binding.icons.adapter = adapter

            adapter.submitList(
                when (position) {
                    1 -> character1
                    2 -> character2
                    3 -> character3
                    else -> null
                }
            )
        }
        return binding.root
    }
}