package cn.wthee.pcrtool.utils

import cn.wthee.pcrtool.BuildConfig
import cn.wthee.pcrtool.MyApplication.Companion.URL_DOMAIN
import cn.wthee.pcrtool.R

/**
 * 常量
 */
object Constants {

    //tp属性变化等级，260级后，tp回复将转化为攻击力
    const val TP_LIMIT_LEVEL = 260

    //本地数据库版本
    const val SQLITE_VERSION = 351

    //本地储存
    const val COIL_DIR = "coil_image_cache"
    const val SP_DATABASE_TYPE = "database_type"
    const val SP_DATABASE_VERSION_CN = "database_version_cn"
    const val SP_DATABASE_VERSION_TW = "database_version_tw"
    const val SP_DATABASE_VERSION_JP = "database_version_jp"
    const val SP_STAR_CHARACTER = "star_character"
    const val SP_STAR_EQUIP = "star_equip"
    const val SP_STAR_EXTRA_EQUIP = "star_extra_equip"
    const val SP_VIBRATE_STATE = "vibrate_state"
    const val SP_ANIM_STATE = "animation_state"
    const val SP_COLOR_STATE = "dynamic_color_state"
    const val SP_USE_IP = "use_ip"
    const val SP_TOOL_ORDER = "tool_order"
    const val SP_OVERVIEW_ORDER = "overview_order"
    const val SP_CHARACTER_DETAIL_ORDER = "character_detail_order"

    const val mediaType = "application/json; charset=utf-8"

    //数据库资源地址
    var DATABASE_URL = "https://$URL_DOMAIN/db/"

    //接口正式地址
    var API_URL = "https://$URL_DOMAIN/pcr/api/v1/"

    //Spine 预览地址
    var PREVIEW_URL = "https://$URL_DOMAIN/spine/index.html"
    var PREVIEW_UNIT_URL = "$PREVIEW_URL?unitId="
    var PREVIEW_ENEMY_URL = "$PREVIEW_URL?enemyId="

    //国服数据库
    const val DATABASE_DOWNLOAD_FILE_NAME_CN = "redive_cn.db.br"
    const val DATABASE_NAME_CN = "redive_cn.db"
    const val DATABASE_WAL_CN = "redive_cn.db-wal"

    //国服备份数据库
    const val DATABASE_DOWNLOAD_FILE_NAME_BACKUP_CN =
        BuildConfig.VERSION_CODE.toString() + DATABASE_DOWNLOAD_FILE_NAME_CN
    const val DATABASE_BACKUP_NAME_CN = BuildConfig.VERSION_CODE.toString() + DATABASE_NAME_CN
    const val DATABASE_WAL_BACKUP_CN = BuildConfig.VERSION_CODE.toString() + DATABASE_WAL_CN

    //台服数据库
    const val DATABASE_DOWNLOAD_FILE_NAME_TW = "redive_tw.db.br"
    const val DATABASE_NAME_TW = "redive_tw.db"
    const val DATABASE_WAL_TW = "redive_tw.db-wal"

    //台服备份数据库
    const val DATABASE_DOWNLOAD_FILE_NAME_BACKUP_TW =
        BuildConfig.VERSION_CODE.toString() + DATABASE_DOWNLOAD_FILE_NAME_TW
    const val DATABASE_BACKUP_NAME_TW = BuildConfig.VERSION_CODE.toString() + DATABASE_NAME_TW
    const val DATABASE_WAL_BACKUP_TW = BuildConfig.VERSION_CODE.toString() + DATABASE_WAL_TW

    //日服数据库
    const val DATABASE_DOWNLOAD_FILE_NAME_JP = "redive_jp.db.br"
    const val DATABASE_NAME_JP = "redive_jp.db"
    const val DATABASE_WAL_JP = "redive_jp.db-wal"

    //日服备份数据库
    const val DATABASE_DOWNLOAD_FILE_NAME_BACKUP_JP =
        BuildConfig.VERSION_CODE.toString() + DATABASE_DOWNLOAD_FILE_NAME_JP
    const val DATABASE_BACKUP_NAME_JP = BuildConfig.VERSION_CODE.toString() + DATABASE_NAME_JP
    const val DATABASE_WAL_BACKUP_JP = BuildConfig.VERSION_CODE.toString() + DATABASE_WAL_JP


    //其它数据库
    const val DATABASE_NEWS = "news.db"
    const val DATABASE_TWEET = "tweet.db"
    const val DATABASE_COMIC = "comic.db"
    const val DATABASE_PVP = "pvp.db"
    const val DATABASE_MOCK_GACHA = "mock_gacha.db"

    const val RANK_UPPER = "RANK"

    val ATTR = arrayListOf(
        getString(R.string.attr_hp),
        getString(R.string.attr_life_steal),
        getString(R.string.attr_atk),
        getString(R.string.attr_magic_str),
        getString(R.string.attr_def),
        getString(R.string.attr_magic_def),
        getString(R.string.attr_physical_critical),
        getString(R.string.attr_magic_critical),
        getString(R.string.attr_physical_penetrate),
        getString(R.string.attr_magic_penetrate),
        getString(R.string.attr_accuracy),
        getString(R.string.attr_dodge),
        getString(R.string.attr_wave_hp_recovery),
        getString(R.string.attr_hp_recovery_rate),
        getString(R.string.attr_wave_energy_recovery),
        getString(R.string.attr_energy_recovery_rate),
        getString(R.string.attr_energy_reduce_rate),
    )

    const val UNKNOWN = "?"


    // 异常
    const val EXCEPTION_API = "api exception:"
    const val EXCEPTION_DOWNLOAD_DB = "db download exception:"
    const val EXCEPTION_DOWNLOAD_APK = "apk download exception:"
    const val EXCEPTION_SAVE_DB = "db file save exception:"
    const val EXCEPTION_LOAD_ATTR = "character attr exception:"
    const val EXCEPTION_UNIT_NULL = "character info exception:"
    const val EXCEPTION_SKILL = "skill exception:"
    const val EXCEPTION_PVP_SERVICE = "pvp search exception:"
    const val EXCEPTION_DATA_CHANGE = "db change exception:"

    //任务
    const val DOWNLOAD_DB_WORK = "updateDatabase"
    const val DOWNLOAD_APK_WORK = "updateApk"
}