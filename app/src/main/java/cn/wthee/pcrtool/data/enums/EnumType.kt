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

//ChipGroup 类型
enum class ChipGroupType(val type: Int) {
    DEFAULT(0),
    RANK(1);

    companion object {
        fun getByValue(value: Int) = values().find { it.type == value } ?: DEFAULT
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
    PICK_UP("PICK UP"),
    RE("复刻"),
    FES("公主庆典"),
    ANNIV("周年");


    companion object {
        fun getByValue(value: String) = values().find { it.typeName == value } ?: UNKNOWN
    }
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
    MOCK_GACHA(15);

    companion object {
        fun getByValue(value: Int) = values()
            .find { it.id == value } ?: CHARACTER
    }
}