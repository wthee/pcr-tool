package cn.wthee.pcrtool.data.preferences

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object MainPreferencesKeys {

    val SP_OVERVIEW_ORDER = stringPreferencesKey("overview_order")
    val SP_CHARACTER_DETAIL_ORDER = stringPreferencesKey("character_detail_order")
    val SP_TOOL_ORDER = stringPreferencesKey("tool_order")

    val SP_STAR_CHARACTER = stringPreferencesKey("star_character")
    val SP_STAR_EQUIP = stringPreferencesKey("star_equip")
    val SP_STAR_EXTRA_EQUIP = stringPreferencesKey("star_extra_equip")


}

object SettingPreferencesKeys {
    val SP_DATABASE_TYPE = intPreferencesKey("database_type")
    val SP_DATABASE_VERSION_CN = stringPreferencesKey("database_version_cn")
    val SP_DATABASE_VERSION_TW = stringPreferencesKey("database_version_tw")
    val SP_DATABASE_VERSION_JP = stringPreferencesKey("database_version_jp")

    val SP_VIBRATE_STATE = booleanPreferencesKey("vibrate_state")
    val SP_ANIM_STATE = booleanPreferencesKey("animation_state")
    val SP_COLOR_STATE = booleanPreferencesKey("dynamic_color_state")
    val SP_USE_IP = booleanPreferencesKey("use_ip")
}