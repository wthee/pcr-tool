package cn.wthee.pcrtool.utils

object Constants {
    //图片格式
    const val WEBP = ".webp"

    //接口地址
    const val API_URL = "https://redive.estertion.win/"
    const val API_URL_PVP = "http://wthee.xyz:8847/api/pvp/"

    //数据库中版
    const val DATABASE_DOWNLOAD_File_Name_JP = "redive_jp.db.br"
    const val DATABASE_Name_JP = "redive_jp.db"
    const val DATABASE_WAL_JP = "redive_jp.db-wal"
    const val DATABASE_VERSION_URL_JP = "last_version_jp.json"

    const val DATABASE_DOWNLOAD_File_Name = "redive_cn.db.br"
    const val DATABASE_Name = "redive_cn.db"
    const val DATABASE_WAL = "redive_cn.db-wal"
    const val DATABASE_VERSION_URL = "last_version_cn.json"

    //角色卡片接口
    const val CHARACTER_URL = API_URL + "card/profile/"

    //现实角色卡片接口
    const val Reality_CHARACTER_URL = API_URL + "card/actual_profile/"

    //装备图标接口
    const val EQUIPMENT_URL = API_URL + "icon/equipment/"

    //图标接口
    const val UNIT_ICON_URL = API_URL + "icon/unit/"
    const val UNIT_ICON_SHADOW_URL = API_URL + "icon/unit_shadow/"

    //角色界面接口
    const val CHARACTER_PLATE_URL = API_URL + "icon/plate/"

    //技能图标接口
    const val SKILL_ICON_URL = API_URL + "icon/skill/"

    //角色Rank
    const val CHARACTER_MIN_RANK = 2

    //Log输出
    const val LOG_TAG = "pcrtool_log_info"

    //本地储存
    const val SP_DATABASE_VERSION = "database_version"
    const val SP_DATABASE_VERSION_JP = "database_version_jp"
    const val SP_SORT_TYPE = "sort_type"
    const val SP_SORT_ASC = "sort_asc"

    //数量
    const val SP_COUNT_CHARACTER = "count_character"
    const val SP_COUNT_EQUIP = "count_equip"
    const val SP_COUNT_ENEMY = "count_enemy"

    //弹框
    const val BTN_OPERATE_FORCE_UPDATE_DB = "强制更新"
    const val BTN_OK = "确认"
    const val BTN_CLOSE = "关闭"
    const val BTN_NOT_UPDATE_DB = "数据正常，无需更新"

    //默认值
    const val SORT_TYPE = 0
    const val SORT_ASC = false
    const val UNKNOW_EQUIP_ID = 999999
    const val NOTICE_TITLE = "正在下载数据..."
    const val NOTICE_TITLE_ERROR = "数据库信息获取失败"
    const val NOTICE_TOAST_SUCCESS = "数据库更新完成"
    const val NOTICE_TOAST_NO_FILE = "数据库文件丢失，请稍后重试~"
    const val NOTICE_TOAST_TITLE_DB_DOWNLOAD = "下载进度请查看通知栏"
    const val NOTICE_TOAST_TIMEOUT = "若主页内容未正常显示，请点击${BTN_OPERATE_FORCE_UPDATE_DB}，下载最新数据库文件"
    const val NOTICE_TOAST_CHANGE = "数据库切换中......"
    const val NOTICE_TOAST_TODO = "功能暂未实装"
    const val NOTICE_TOAST_CHECKING = "正在检测数据库版本"


    //列表列数
    const val COLUMN_COUNT = 1
    const val COLUMN_COUNT_EQUIP = 4

    //排序
    const val SORT_DATE = 0
    const val SORT_AGE = 1
    const val SORT_HEIGHT = 2
    const val SORT_WEIGHT = 3
    const val SORT_POSITION = 4

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
}