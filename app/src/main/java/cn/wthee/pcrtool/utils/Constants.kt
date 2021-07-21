package cn.wthee.pcrtool.utils

import cn.wthee.pcrtool.BuildConfig

/**
 * 常量
 */
object Constants {

    //图片格式
    const val WEBP = ".webp"
    const val PNG = ".png"
    const val FADE_TIME = 1000

    //图片资源地址
    private const val RESOURCE_URL = "https://wthee.xyz/resource/"

    //数据库资源地址
    const val DATABASE_URL = "https://wthee.xyz/db/"

    //接口正式地址
    const val API_URL = "https://wthee.xyz:8848/api/"

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
    const val DATABASE_WAL_JP_BACKUP = BuildConfig.VERSION_CODE.toString() + DATABASE_WAL_JP


    //国服数据库
    const val DATABASE_VERSION_URL = "last_version_cn.json"
    const val DATABASE_DOWNLOAD_FILE_NAME = "redive_cn.db.br"
    const val DATABASE_NAME = "redive_cn.db"
    const val DATABASE_WAL = "redive_cn.db-wal"
    const val DATABASE_SHM = "redive_cn.db-shm"

    //国服备份数据库
    const val DATABASE_DOWNLOAD_FILE_NAME_BACKUP =
        BuildConfig.VERSION_CODE.toString() + DATABASE_DOWNLOAD_FILE_NAME
    const val DATABASE_BACKUP_NAME = BuildConfig.VERSION_CODE.toString() + DATABASE_NAME
    const val DATABASE_WAL_BACKUP = BuildConfig.VERSION_CODE.toString() + DATABASE_WAL


    //其它数据库
    const val DATABASE_NEWS = "news.db"
    const val DATABASE_TWEET = "tweet.db"
    const val DATABASE_PVP = "pvp.db"

    //角色卡片接口
    const val CHARACTER_URL = RESOURCE_URL + "card/profile/"
    const val CHARACTER_FULL_URL = RESOURCE_URL + "card/full/"

    //现实角色卡片接口
    const val Reality_CHARACTER_URL = RESOURCE_URL + "card/actual_profile/"

    //装备图标接口
    const val UNKNOWN_EQUIP_ID = 999999
    const val EQUIPMENT_URL = RESOURCE_URL + "icon/equipment/"
    const val UNKNOWN_EQUIPMENT_ICON = EQUIPMENT_URL + UNKNOWN_EQUIP_ID + WEBP

    //图标接口
    const val UNIT_ICON_URL = RESOURCE_URL + "icon/unit/"

    //技能图标接口
    const val SKILL_ICON_URL = RESOURCE_URL + "icon/skill/"

    //漫画地址
    const val COMIC_URL = RESOURCE_URL + "comic/"

    //本地储存
    const val SP_DATABASE_TYPE = "database_type"
    const val SP_DATABASE_VERSION = "database_version"
    const val SP_DATABASE_VERSION_JP = "database_version_jp"
    const val SP_STAR_CHARACTER = "star_character"
    const val SP_STAR_EQUIP = "star_equip"
    const val SP_VIBRATE_STATE = "vibrate_state"
    const val SP_ANIM_STATE = "animation_state"

    //默认值
    const val NOTICE_TITLE = "正在下载数据"
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

    //现实图片有误的角色编号
    val errorIDs = arrayListOf(
        101001,
        101301,
        101501,
        102201,
        102801,
        103801,
        104501,
        104601,
        105401,
    )

    //无现实图片角色
    val notExistsIDs = arrayListOf(
        109731, 109831, 109931, 109231, 109331, 109431
    )

    //未实装角色
    val unuseIDs = arrayListOf(
        106701,
        106901,
        107201,
        107301,
        110201,
        191301
    )

    // 异常
    private const val EXCEPTION = "异常"
    const val EXCEPTION_API = "接口$EXCEPTION"
    const val EXCEPTION_DOWNLOAD_DB = "数据库文件下载$EXCEPTION"
    const val EXCEPTION_SAVE_DB = "数据库文件保存$EXCEPTION"
    const val EXCEPTION_LOAD_ATTR = "获取属性$EXCEPTION"
    const val EXCEPTION_UNIT_NULL = "角色信息空值$EXCEPTION"
    const val EXCEPTION_SKILL = "角色技能$EXCEPTION"
}