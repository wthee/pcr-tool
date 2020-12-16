package cn.wthee.pcrtool.ui.tool.pvp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import cn.wthee.pcrtool.adapter.PvpCharacterAdapter
import cn.wthee.pcrtool.databinding.FragmentToolPvpCharacterBinding
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

/**
 * 竞技场角色ViewPager
 */
class PvpPagerFragment : Fragment() {

    private val POSITION = "positon"
    private val FLOAT = "float"

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
        MainScope().launch {
            val adapter = PvpCharacterAdapter(isFloatWindow)
//            adapter.setHasStableIds(!isFloatWindow)
            binding.icons.adapter = adapter

            adapter.submitList(
                when (position) {
                    1 -> PvpFragment.character1
                    2 -> PvpFragment.character2
                    3 -> PvpFragment.character3
                    else -> null
                }
            ) {
                try {
                    PvpFragment.progressBar.visibility = View.GONE
                } catch (e: Exception) {
                }
                try {
                    PvpService.progressBar.visibility = View.GONE
                } catch (e: Exception) {
                }
            }
        }
        return binding.root
    }
}