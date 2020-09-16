package cn.wthee.pcrtool.utils

object Constants {
    //图片格式
    const val WEBP = ".webp"

    //接口地址
    const val API_URL = "https://redive.estertion.win/"

    //数据库中版
    const val DATABASE_CN_DOWNLOAD_File_Name = "redive_cn.db.br"
    const val DATABASE_CN_Name = "redive_cn.db"
    const val DATABASE_CN_WAL = "redive_cn.db-wal"

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

    //默认值
    const val DATABASE_VERSION = "202005141837"
    const val SORT_TYPE = 0
    const val SORT_ASC = false
    const val UNKNOW_EQUIP_ID = 999999
    const val NOTICE_TITLE = "正在更新数据..."
    const val NOTICE_TITLE_PRE = "PCR Tool\n"
    const val NOTICE_TITLE_ERROR = "访问出错"
    const val NOTICE_TOAST_SUCCESS = "数据更新完成"
    const val NOTICE_TOAST_TITLE_DB_DOWNLOAD = "数据更新中\n详情查看通知栏"
    const val NOTICE_TOAST_TIMEOUT = "数据库版本信息获取失败。若内容未正常显示，点击${BTN_OPERATE_FORCE_UPDATE_DB}，将更新至最新数据库"
    const val NOTICE_TOAST_CHECKING = "正在检测数据库版本"
    const val NOTICE_TOAST_CHECKED = "数据库已是最新版本"
    const val NOTICE_TOAST_TODO = "功能暂未实装"


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
        "物理攻击力",
        "物理贯穿",
        "魔法攻击力",
        "魔法贯穿",
        "物理防御力",
        "魔法防御力",
        "物理暴击",
        "回避",
        "魔法暴击",
        "HP自动回复",
        "TP自动回复",
        "HP吸收",
        "回复量上升",
        "TP上升",
        "TP消耗减轻",
        "命中"
    )
}