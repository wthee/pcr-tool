package cn.wthee.pcrtool.ui.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.databinding.FragmentCommonBottomSheetBinding
import cn.wthee.pcrtool.enums.PageType
import cn.wthee.pcrtool.ui.detail.character.CharacterSkillFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class CommonBottomSheetFragment(private val uid: Int, private val page: PageType) :
    BottomSheetDialogFragment() {

    private lateinit var binding: FragmentCommonBottomSheetBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCommonBottomSheetBinding.inflate(inflater, container, false)
        when (page) {
            PageType.CAHRACTER_SKILL -> {
                childFragmentManager.beginTransaction()
                    .replace(R.id.container, CharacterSkillFragment.getInstance(uid, true))
                    .commit()
            }
        }

        return binding.root
    }

}