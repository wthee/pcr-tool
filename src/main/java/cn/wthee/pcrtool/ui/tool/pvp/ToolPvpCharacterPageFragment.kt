package cn.wthee.pcrtool.ui.tool.pvp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import cn.wthee.pcrtool.adapters.PvpCharacterAdapter
import cn.wthee.pcrtool.databinding.FragmentToolPvpCharacterBinding
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


class ToolPvpCharacterPageFragment : Fragment() {

    private val POSITION = "positon"
    private val FLOAT = "float"

    companion object {
        fun getInstance(position: Int, isFloatWindow: Boolean) =
            ToolPvpCharacterPageFragment().apply {
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
    ): View? {
        binding = FragmentToolPvpCharacterBinding.inflate(inflater, container, false)
        MainScope().launch {
            val adapter = PvpCharacterAdapter(isFloatWindow, requireActivity())
//            adapter.setHasStableIds(!isFloatWindow)
            binding.icons.adapter = adapter

            adapter.submitList(
                when (position) {
                    1 -> ToolPvpFragment.character1
                    2 -> ToolPvpFragment.character2
                    3 -> ToolPvpFragment.character3
                    else -> null
                }
            ) {
                try {
                    ToolPvpFragment.progressBar.visibility = View.GONE
                } catch (e: Exception) {
                }
                try {
                    ToolPvpService.progressBar.visibility = View.GONE
                } catch (e: Exception) {
                }
            }
        }
        return binding.root
    }
}