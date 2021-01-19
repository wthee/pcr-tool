package cn.wthee.pcrtool.utils

/**
 * 常量
 */
object Constants {

    //图片格式
    const val WEBP = ".webp"

    //图片资源地址
    private const val RESOURCE_URL = "http://wthee.xyz/resource/"

    //数据库资源地址
    const val DATABASE_URL = "http://wthee.xyz/db/"

    //接口正式地址
    const val API_URL = "http://wthee.xyz:1211/api/"

    //数据库
    const val DATABASE_DOWNLOAD_File_Name_JP = "redive_jp.db.br"
    const val DATABASE_Name_JP = "redive_jp.db"
    const val DATABASE_WAL_JP = "redive_jp.db-wal"
    const val DATABASE_SHM_JP = "redive_jp.db-shm"
    const val DATABASE_VERSION_URL_JP = "last_version_jp.json"

    const val DATABASE_DOWNLOAD_File_Name = "redive_cn.db.br"
    const val DATABASE_Name = "redive_cn.db"
    const val DATABASE_WAL = "redive_cn.db-wal"
    const val DATABASE_SHM = "redive_cn.db-shm"
    const val DATABASE_VERSION_URL = "last_version_cn.json"
    const val DATABASE_NEWS = "news.db"
    const val DATABASE_PVP = "pvp.db"

    //角色卡片接口
    const val CHARACTER_URL = RESOURCE_URL + "card/profile/"
    const val CHARACTER_FULL_URL = RESOURCE_URL + "card/full/"

    //现实角色卡片接口
    const val Reality_CHARACTER_URL = RESOURCE_URL + "card/actual_profile/"

    //装备图标接口
    const val EQUIPMENT_URL = RESOURCE_URL + "icon/equipment/"

    //道具
    const val ITEM_URL = RESOURCE_URL + "icon/item/"

    //图标接口
    const val UNIT_ICON_URL = RESOURCE_URL + "icon/unit/"
    const val UNIT_ICON_SHADOW_URL = RESOURCE_URL + "icon/unit_shadow/"

    //角色界面接口
    const val CHARACTER_PLATE_URL = RESOURCE_URL + "icon/plate/"

    //技能图标接口
    const val SKILL_ICON_URL = RESOURCE_URL + "icon/skill/"

    //角色Rank
    const val CHARACTER_MIN_RANK = 2

    //Log输出
    const val LOG_TAG = "pcrtool_log_info"

    //本地储存
    const val SP_DATABASE_VERSION = "database_version"
    const val SP_DATABASE_HASH = "database_hash"
    const val SP_DATABASE_VERSION_JP = "database_version_jp"
    const val SP_DATABASE_HASH_JP = "database_hash_jp"
    const val SP_STAR_CHARACTER = "star_character"
    const val SP_STAR_EQUIP = "star_equip"

    //数量
    const val SP_COUNT_CHARACTER = "count_character"
    const val SP_COUNT_EQUIP = "count_equip"

    //默认值
    const val SORT_ASC = false
    const val UNKNOWN_EQUIP_ID = 999999
    const val NOTICE_TITLE = "正在下载数据"
    const val NOTICE_TOAST_SUCCESS = "数据更新完成！"
    const val NOTICE_TOAST_NO_FILE = "数据文件下载失败，请稍后重试~"
    const val NOTICE_TOAST_CHANGE = "数据切换中..."
    const val NOTICE_TOAST_CHECKING = "正在获取信息"

    //常量值
    const val UID = "unit_id"
    const val SIXSTAR = "six_star"
    const val REGION = "region"

    val ATTR = arrayListOf(
        "HP",
        "HP吸收",
        "物理攻击力",
        "魔法攻击力",
        "物理防御力",
        "魔法防御力",
        "物理暴击",
        "魔法暴击",
        "物理贯穿",
        "魔法贯穿",
        "命中",
        "回避",
        "HP自动回复",
        "回复量上升",
        "TP自动回复",
        "TP上升",
        "TP消耗减轻",
    )

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

    val notExistsIDs = arrayListOf(
        109731, 109831, 109931,
    )
}