package cn.wthee.pcrtool.utils

import cn.wthee.pcrtool.BuildConfig

/**
 * 常量
 */
object Constants {

    //图片格式
    const val WEBP = ".webp"

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
    const val DATABASE_SHM_JP_BACKUP = BuildConfig.VERSION_CODE.toString() + DATABASE_SHM_JP


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
    const val DATABASE_SHM_BACKUP = BuildConfig.VERSION_CODE.toString() + DATABASE_SHM


    //其它数据库
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

    //技能图标接口
    const val SKILL_ICON_URL = RESOURCE_URL + "icon/skill/"

    //卡面
    const val UNIT_PLATE_URL = RESOURCE_URL + "icon/plate/"

    //本地储存
    const val SP_DATABASE_TYPE = "database_type"
    const val SP_DATABASE_VERSION = "database_version"
    const val SP_DATABASE_VERSION_JP = "database_version_jp"
    const val SP_STAR_CHARACTER = "star_character"
    const val SP_STAR_EQUIP = "star_equip"

    //默认值
    const val SORT_ASC = false
    const val UNKNOWN_EQUIP_ID = 999999
    const val NOTICE_TITLE = "正在下载数据"
    const val NOTICE_TOAST_SUCCESS = "数据更新完成！"
    const val NOTICE_TOAST_CHANGE_SUCCESS = "数据切换完成！"
    const val NOTICE_TOAST_CHANGE = "数据切换中..."
    const val NOTICE_TOAST_CHECKING = "正在获取信息"
    const val NOTICE_TOAST_NETWORK_ERROR = "网络异常..."
    const val RANK_UPPER = "RANK"
    const val SECTION = "阶段"
    const val UNKNOWN = "未知"

    //常量值
    const val UID = "unit_id"
    const val UNIT_NAME = "unit_name"
    const val UNIT_NAME_EX = "unit_name_ex"
    const val PIC_CACHE_KEY = "pic_cache_key"
    const val REGION = "region"
    const val SELECT_LEVEL = "select_level"
    const val SELECT_RANK = "select_rank"
    const val START_RANK = "start_rank"
    const val END_RANK = "end_rank"
    const val LOG_TAG = "log_tag"
    const val RANK = "rank"
    const val RARITY = "rarity"
    const val LEVEL = "level"
    const val MAX_LEVEL = "max_level"
    const val MAX_UE_LEVEL = "max_ue_level"
    const val UNIQUE_EQUIP_LEVEL = "ueLv"
    const val CLAN_DATE = "clan_date"
    const val CLAN_BOSS_NO = "clan_boss_no"
    const val CLAN_DATA = "clan_data"
    const val CLAN_MAX_SECTION = "clan_max_section"
    const val CLAN_SELECT_SECTION = "clan_select_section"
    const val CLAN_SKILL_LVS = "clan_skill_lvs"
    const val CLAN_BOSS_ATK = "clan_boss_atk"
    const val ATK = "atk"
    const val SKILL_LEVEL = "skill_level"
    const val TYPE_SKILL = "type_skill"

    val ATTR = arrayListOf(
        "HP",
        "HP吸收",
        "物理攻击力",
        "魔法攻击力",
        "物理防御力",
        "魔法防御力",
        "物理暴击",
        "魔法暴击",
        "物理穿透",
        "魔法穿透",
        "命中",
        "回避",
        "HP回复",
        "回复量上升",
        "TP回复",
        "TP上升",
        "TP消耗减少",
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

    // 异常
    const val EXCEPTION = "异常"
    const val EXCEPTION_BACK_TOP = "回到顶部$EXCEPTION"
    const val EXCEPTION_MENU_NAV = "菜单跳转$EXCEPTION"
    const val EXCEPTION_API = "接口$EXCEPTION"
    const val EXCEPTION_DOWNLOAD_DB = "数据库文件下载$EXCEPTION"
    const val EXCEPTION_SAVE_DB = "数据库文件保存$EXCEPTION"
    const val EXCEPTION_LOAD_PIC = "图片加载$EXCEPTION"
    const val EXCEPTION_DOWNLOAD_PIC = "图片下载$EXCEPTION"
    const val EXCEPTION_LOAD_ATTR = "获取属性$EXCEPTION"
    const val EXCEPTION_UNIT_NULL = "角色信息空值$EXCEPTION"
    const val EXCEPTION_SKILL = "角色技能$EXCEPTION"
    const val EXCEPTION_PVP_DIALOG = "竞技场查询弹窗$EXCEPTION"
}