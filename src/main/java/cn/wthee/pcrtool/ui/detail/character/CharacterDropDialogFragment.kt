package cn.wthee.pcrtool.ui.detail.character

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import cn.wthee.pcrtool.adapters.CharacterDropAdapter
import cn.wthee.pcrtool.databinding.FragmentCharacterDropInfoBinding
import cn.wthee.pcrtool.ui.main.CharacterViewModel
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.InjectorUtil
import cn.wthee.pcrtool.utils.ToolbarUtil
import coil.load
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch


class CharacterDropDialogFragment(
    private val uid: Int
) : BottomSheetDialogFragment() {


    private lateinit var binding: FragmentCharacterDropInfoBinding
    private val sharedViewModel by activityViewModels<CharacterViewModel> {
        InjectorUtil.provideCharacterViewModelFactory()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCharacterDropInfoBinding.inflate(inflater, container, false)
        //toolbar
        ToolbarUtil(binding.toolbar).setCenterTitle("角色碎片掉落")
            .leftIcon.setOnClickListener {
                dialog?.dismiss()
            }
        val picUrl = Constants.ITEM_URL + (uid / 100 + 30000) + Constants.WEBP
        binding.dropIcon.load(picUrl)
        lifecycleScope.launch {
            //初始化列表
            val adapter = CharacterDropAdapter()
            binding.drops.adapter = adapter
            adapter.submitList(sharedViewModel.getDrops(uid))
        }
        return binding.root
    }

}