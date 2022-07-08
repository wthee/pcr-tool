package cn.wthee.pcrtool.data.enums

//立绘类型
enum class AllPicsType(val type: Int) {
    CHARACTER(0),
    STORY(1);

    companion object {
        fun getByValue(value: Int) = values().find { it.type == value } ?: CHARACTER
    }
}

//活动类型
enum class EventType(val type: Int) {
    UNKNOWN(0),
    IN_PROGRESS(1),
    COMING_SOON(2);

    companion object {
        fun getByValue(value: Int) = values().find { it.type == value } ?: UNKNOWN
    }
}

//角色单位类型
enum class UnitType(val type: Int) {
    CHARACTER(0),
    CHARACTER_SUMMON(1),
    ENEMY(2),
    ENEMY_SUMMON(3);

    companion object {
        fun getByValue(value: Int) = values().find { it.type == value } ?: CHARACTER
    }
}

//Rank 选择类型
enum class RankSelectType(val type: Int) {
    DEFAULT(0),
    LIMIT(1);

    companion object {
        fun getByValue(value: Int) = values().find { it.type == value } ?: DEFAULT
    }
}

//卡池类型
enum class GachaType(val typeName: String) {
    UNKNOWN(""),
    LIMIT("限定"),
    RE_LIMIT("复刻限定"),
    NORMAL("常驻"),
    RE_NORMAL("复刻常驻"),
    FES("公主庆典"),
    ANNIV("周年");
}

//菜单
enum class ToolMenuType(val id: Int) {
    CHARACTER(0),
    EQUIP(1),
    GUILD(2),
    CLAN(3),
    RANDOM_AREA(4),
    GACHA(5),
    EVENT(6),
    NEWS(7),
    FREE_GACHA(8),
    PVP_SEARCH(9),
    LEADER(10),
    TWEET(11),
    COMIC(12),
    ALL_SKILL(13),
    ALL_EQUIP(14),
    MOCK_GACHA(15),
    BIRTHDAY(16),
    CALENDAR_EVENT(17);

    companion object {
        fun getByValue(value: Int) = values()
            .find { it.id == value } ?: CHARACTER
    }
}

//首页
enum class OverviewType(val id: Int) {
    CHARACTER(0),
    EQUIP(1),
    TOOL(2),
    NEWS(3),
    IN_PROGRESS_EVENT(4),
    COMING_SOON_EVENT(5);

    companion object {
        fun getByValue(value: Int) = values()
            .find { it.id == value } ?: CHARACTER
    }
}

//站位
enum class PositionType {
    UNKNOWN,
    POSITION_0_299,
    POSITION_300_599,
    POSITION_600_999;

    companion object {
        fun getPositionType(position: Int) = when (position) {
            in 1..299 -> POSITION_0_299
            in 300..599 -> POSITION_300_599
            in 600..9999 -> POSITION_600_999
            else -> UNKNOWN
        }
    }
}