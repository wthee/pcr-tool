package cn.wthee.pcrtool.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import cn.wthee.pcrtool.MainActivity
import cn.wthee.pcrtool.databinding.FragmentEnemyBinding
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.InjectorUtil
import javax.inject.Singleton

@Singleton
class EnemyFragment : Fragment() {

    private lateinit var viewModel: EnemyViewModel
    private lateinit var binding: FragmentEnemyBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEnemyBinding.inflate(inflater, container, false)
        viewModel = InjectorUtil.provideEnemyViewModelFactory().create(EnemyViewModel::class.java)
        viewModel.getAllEnemy()
        viewModel.enemies.observe(viewLifecycleOwner, Observer {
            MainActivity.sp.edit {
                putInt(Constants.SP_COUNT_ENEMY, it.size)
            }
            MainPagerFragment.tabLayout.getTabAt(2)?.text = it.size.toString()
        })
        return binding.root
    }

}