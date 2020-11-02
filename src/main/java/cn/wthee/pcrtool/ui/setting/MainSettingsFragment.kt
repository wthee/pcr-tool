package cn.wthee.pcrtool.ui.setting

import android.content.Intent
import android.os.Bundle
import androidx.core.content.edit
import androidx.fragment.app.activityViewModels
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat
import cn.wthee.pcrtool.MainActivity
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.database.DatabaseUpdateHelper
import cn.wthee.pcrtool.ui.main.CharacterListFragment
import cn.wthee.pcrtool.ui.main.CharacterViewModel
import cn.wthee.pcrtool.ui.main.EquipmentViewModel
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.FabHelper
import cn.wthee.pcrtool.utils.InjectorUtil
import cn.wthee.pcrtool.utils.ToastUtil
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MainSettingsFragment : PreferenceFragmentCompat() {

    companion object {
        lateinit var checkUpdateDb: Preference
    }

    private val viewModel by activityViewModels<EquipmentViewModel> {
        InjectorUtil.provideEquipmentViewModelFactory()
    }
    private val sharedCharacterViewModel by activityViewModels<CharacterViewModel> {
        InjectorUtil.provideCharacterViewModelFactory()
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        //添加返回fab
        FabHelper.addBackFab()
        //获取控件
        checkUpdateDb = findPreference<Preference>("check_update_db")!!
        val forceUpdateDb = findPreference<Preference>("force_update_db")
        val appUpdate = findPreference<Preference>("force_update_app")
        val shareApp = findPreference<Preference>("share_app")
        val changeDbType = findPreference<ListPreference>("change_database")
        changeDbType?.title =
            "游戏版本 - " + if (changeDbType?.value == "1") getString(R.string.db_cn) else getString(R.string.db_jp)
        //摘要替换
        checkUpdateDb.summary = MainActivity.sp.getString(
            if (changeDbType?.value == "1") Constants.SP_DATABASE_VERSION else Constants.SP_DATABASE_VERSION_JP,
            "0"
        )
        appUpdate?.summary = MainActivity.nowVersionName
        //设置监听
        //检查更新数据库
        checkUpdateDb.setOnPreferenceClickListener {
            DatabaseUpdateHelper.checkDBVersion(0)
            return@setOnPreferenceClickListener true
        }
        //强制更新数据库
        forceUpdateDb?.setOnPreferenceClickListener {
            DatabaseUpdateHelper.checkDBVersion(0, force = true)
            return@setOnPreferenceClickListener true
        }
        //切换数据库版本
        changeDbType?.setOnPreferenceChangeListener { _, newValue ->
            changeDbType.title =
                "游戏版本 - " + if (newValue as String == "1") getString(R.string.db_cn) else getString(
                    R.string.db_jp
                )
            MainScope().launch {
                delay(800L)
                DatabaseUpdateHelper.checkDBVersion(1)
            }
            return@setOnPreferenceChangeListener true
        }
        //egg
        val eggs = findPreference<PreferenceCategory>("egg")
        val eggKL = findPreference<Preference>("kl")
        eggs?.isVisible = MainActivity.sp.getBoolean("106001", false)
                || MainActivity.sp.getBoolean("107801", false)
        var count = MainActivity.sp.getInt("click_kl", 0)
        eggKL?.setOnPreferenceClickListener {
            count++
            val text = when (count) {
                1 -> "不要碰我了~烦死啦"
                2 -> "我都说过了！烦死啦~烦死啦~"
                3 -> "凯露我啊~真的生气了！！！"
                else -> ""
            }
            ToastUtil.short(text)
            if (count == 3) {
                eggs?.isVisible = false
                MainActivity.sp.edit {
                    putBoolean("106001", false)
                    putBoolean("107801", false)
                }
                CharacterListFragment.characterfilterParams.initData()
                sharedCharacterViewModel.getCharacters(
                    MainActivity.sortType,
                    MainActivity.sortAsc,
                    ""
                )
            }

            return@setOnPreferenceClickListener true
        }

        //分享应用
        shareApp?.setOnPreferenceClickListener {
            var shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.type = "text/plain"
            shareIntent.putExtra(
                Intent.EXTRA_TEXT,
                "PCR Tool 下载地址: https://www.coolapk.com/apk/273453"
            )
            shareIntent = Intent.createChooser(shareIntent, "分享到：")
            //将mipmap中图片转换成Uri
//            val imgUri = FileUtil.getUriFromDrawableRes(MyApplication.context, R.drawable.app_code)
//            shareIntent.action = Intent.ACTION_SEND
//            shareIntent.putExtra(Intent.EXTRA_STREAM, imgUri)
//            shareIntent.type = "image/*"
//            shareIntent = Intent.createChooser(shareIntent, "分享下载二维码")
            startActivity(shareIntent)
            return@setOnPreferenceClickListener true
        }
    }


}