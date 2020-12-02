package cn.wthee.pcrtool.ui.detail.character

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import cn.wthee.pcrtool.adapters.CharacterAttrAdapter
import cn.wthee.pcrtool.data.view.Compare
import cn.wthee.pcrtool.data.view.all
import cn.wthee.pcrtool.databinding.FragmentCharacterRankCompareBinding
import cn.wthee.pcrtool.ui.common.CommonBasicDialogFragment
import cn.wthee.pcrtool.utils.InjectorUtil
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


class CharacterRankCompareFragment : CommonBasicDialogFragment() {

    private lateinit var binding: FragmentCharacterRankCompareBinding
    private val sharedAttrViewModel: CharacterAttrViewModel by activityViewModels {
        InjectorUtil.provideCharacterAttrViewModelFactory()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCharacterRankCompareBinding.inflate(inflater, container, false)
        MainScope().launch {
            val rank0Attr = sharedAttrViewModel.getAttrs(
                CharacterAttrFragment.uid, 12, CharacterAttrFragment.maxStar,
                CharacterAttrFragment.lv, CharacterAttrFragment.ueLv
            )
            val rank1Attr = sharedAttrViewModel.getAttrs(
                CharacterAttrFragment.uid, 14, CharacterAttrFragment.maxStar,
                CharacterAttrFragment.lv, CharacterAttrFragment.ueLv
            )

            val adapter0 = CharacterAttrAdapter(false)
            val adapter1 = CharacterAttrAdapter(true)
            val adapter2 = CharacterAttrAdapter(true)
            binding.apply {
                rank0.adapter = adapter0
                rank1.adapter = adapter1
                rankCompare.adapter = adapter2
                adapter0.submitList(rank0Attr.all())
                adapter1.submitList(rank1Attr.all())
                adapter2.submitList(rank1Attr.Compare(rank0Attr))
            }
        }

        return binding.root
    }

}