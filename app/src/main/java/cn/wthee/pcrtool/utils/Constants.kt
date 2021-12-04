package cn.wthee.pcrtool.utils

import cn.wthee.pcrtool.BuildConfig

/**
 * 常量
 */
object Constants {

    //本地储存
    const val SP_DATABASE_TYPE = "database_type"
    const val SP_DATABASE_VERSION_CN = "database_version_cn"
    const val SP_DATABASE_VERSION_TW = "database_version_tw"
    const val SP_DATABASE_VERSION_JP = "database_version_jp"
    const val SP_STAR_CHARACTER = "star_character"
    const val SP_STAR_EQUIP = "star_equip"
    const val SP_VIBRATE_STATE = "vibrate_state"
    const val SP_ANIM_STATE = "animation_state"


    //数据库资源地址
    const val DATABASE_URL = "https://wthee.xyz/db/"

    //接口正式地址
    const val API_URL = "https://wthee.xyz:8848/api/"

    //国服数据库
    const val DATABASE_VERSION_URL_CN = "last_version_cn.json"
    const val DATABASE_DOWNLOAD_FILE_NAME_CN = "redive_cn.db.br"
    const val DATABASE_NAME_CN = "redive_cn.db"
    const val DATABASE_WAL_CN = "redive_cn.db-wal"
    const val DATABASE_SHM_CN = "redive_cn.db-shm"


    //国服备份数据库
    const val DATABASE_DOWNLOAD_FILE_NAME_BACKUP_CN =
        BuildConfig.VERSION_CODE.toString() + DATABASE_DOWNLOAD_FILE_NAME_CN
    const val DATABASE_BACKUP_NAME_CN = BuildConfig.VERSION_CODE.toString() + DATABASE_NAME_CN
    const val DATABASE_WAL_BACKUP_CN = BuildConfig.VERSION_CODE.toString() + DATABASE_WAL_CN

    //台服数据库
    const val DATABASE_VERSION_URL_TW = "last_version_tw.json"
    const val DATABASE_DOWNLOAD_FILE_NAME_TW = "redive_tw.db.br"
    const val DATABASE_NAME_TW = "redive_tw.db"
    const val DATABASE_WAL_TW = "redive_tw.db-wal"
    const val DATABASE_SHM_TW = "redive_tw.db-shm"


    //台服备份数据库
    const val DATABASE_DOWNLOAD_FILE_NAME_BACKUP_TW =
        BuildConfig.VERSION_CODE.toString() + DATABASE_DOWNLOAD_FILE_NAME_TW
    const val DATABASE_BACKUP_NAME_TW = BuildConfig.VERSION_CODE.toString() + DATABASE_NAME_TW
    const val DATABASE_WAL_BACKUP_TW = BuildConfig.VERSION_CODE.toString() + DATABASE_WAL_TW

    //日服数据库
    const val DATABASE_DOWNLOAD_FILE_NAME_JP = "redive_jp.db.br"
    const val DATABASE_VERSION_URL_JP = "last_version_jp.json"
    const val DATABASE_NAME_JP = "redive_jp.db"
    const val DATABASE_WAL_JP = "redive_jp.db-wal"
    const val DATABASE_SHM_JP = "redive_jp.db-shm"

    //日服备份数据库
    const val DATABASE_DOWNLOAD_FILE_NAME_BACKUP_JP =
        BuildConfig.VERSION_CODE.toString() + DATABASE_DOWNLOAD_FILE_NAME_JP
    const val DATABASE_BACKUP_NAME_JP = BuildConfig.VERSION_CODE.toString() + DATABASE_NAME_JP
    const val DATABASE_WAL_BACKUP_JP = BuildConfig.VERSION_CODE.toString() + DATABASE_WAL_JP


    //其它数据库
    const val DATABASE_NEWS = "news.db"
    const val DATABASE_TWEET = "tweet.db"
    const val DATABASE_PVP = "pvp.db"



    const val DOWNLOAD_NOTICE_TITLE = "正在下载数据"
    const val PVPSEARCH_NOTICE_TITLE = "竞技场查询服务正在运行"
    const val DOWNLOAD_ERROR = "下载数据出现未知错误~"
    const val RANK_UPPER = "RANK"

    val ATTR = arrayListOf(
        "HP",
        "HP 吸收",
        "物理攻击",
        "魔法攻击",
        "物理防御",
        "魔法防御",
        "物理暴击",
        "魔法暴击",
        "物理穿透",
        "魔法穿透",
        "命 中",
        "回 避",
        "HP 回复",
        "回复上升",
        "TP 回复",
        "TP 上升",
        "TP 减少",
    )

    const val UNKNOWN = "?"


    // 异常
    private const val EXCEPTION = "异常"
    const val EXCEPTION_API = "接口$EXCEPTION"
    const val EXCEPTION_DOWNLOAD_DB = "数据库文件下载$EXCEPTION"
    const val EXCEPTION_SAVE_DB = "数据库文件保存$EXCEPTION"
    const val EXCEPTION_LOAD_ATTR = "获取属性$EXCEPTION"
    const val EXCEPTION_UNIT_NULL = "角色信息空值$EXCEPTION"
    const val EXCEPTION_SKILL = "角色技能$EXCEPTION"
    const val EXCEPTION_PVP_SERVICE = "竞技场查询服务$EXCEPTION"
    const val EXCEPTION_DATA_EXPORT = "数据导出$EXCEPTION"
    const val EXCEPTION_DATA_IMPORT = "数据导入$EXCEPTION"
    const val EXCEPTION_DATA_CHANGE = "数据切换$EXCEPTION"
}