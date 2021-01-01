package cn.wthee.pcrtool.ui.character.attr

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.adapter.CharacterDropAdapter
import cn.wthee.pcrtool.databinding.FragmentCharacterDropInfoBinding
import cn.wthee.pcrtool.ui.common.CommonBottomSheetDialogFragment
import cn.wthee.pcrtool.ui.home.CharacterViewModel
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.Constants.UID
import cn.wthee.pcrtool.utils.InjectorUtil
import cn.wthee.pcrtool.utils.ToolbarUtil
import coil.load
import kotlinx.coroutines.launch

/**
 * 角色碎片掉落页面
 */
class CharacterDropDialogFragment : CommonBottomSheetDialogFragment() {

    companion object {
        fun getInstance(uid: Int) =
            CharacterDropDialogFragment().apply {
                arguments = Bundle().apply {
                    putInt(UID, uid)
                }
            }
    }

    private lateinit var binding: FragmentCharacterDropInfoBinding
    private var uid = 0

    private val sharedViewModel by activityViewModels<CharacterViewModel> {
        InjectorUtil.provideCharacterViewModelFactory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireArguments().apply {
            uid = getInt(UID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCharacterDropInfoBinding.inflate(inflater, container, false)
        //toolbar
        ToolbarUtil(binding.toolbar).setCenterTitle("角色碎片掉落")
        val picUrl = Constants.ITEM_URL + (uid / 100 + 30000) + Constants.WEBP
        binding.dropIcon.load(picUrl) {
            placeholder(R.drawable.unknown_gray)
            error(R.drawable.unknown_gray)
        }
        lifecycleScope.launch {
            //初始化列表
            val adapter = CharacterDropAdapter()
            binding.characterDrops.adapter = adapter
            adapter.submitList(sharedViewModel.getDrops(uid))
        }
        return binding.root
    }

}