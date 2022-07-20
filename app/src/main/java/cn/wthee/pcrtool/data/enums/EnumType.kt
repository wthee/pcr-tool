package cn.wthee.pcrtool.data.enums

/**
 * 立绘类型
 */
enum class AllPicsType(val type: Int) {
    CHARACTER(0),
    STORY(1);

    companion object {
        fun getByValue(value: Int) = values().find { it.type == value } ?: CHARACTER
    }
}

/**
 * 活动类型
 */
enum class EventType(val type: Int) {
    UNKNOWN(0),
    IN_PROGRESS(1),
    COMING_SOON(2);

    companion object {
        fun getByValue(value: Int) = values().find { it.type == value } ?: UNKNOWN
    }
}

/**
 * 角色单位类型
 */
enum class UnitType(val type: Int) {
    CHARACTER(0),
    CHARACTER_SUMMON(1),
    ENEMY(2),
    ENEMY_SUMMON(3);

    companion object {
        fun getByValue(value: Int) = values().find { it.type == value } ?: CHARACTER
    }
}

/**
 * Rank 选择类型
 */
enum class RankSelectType(val type: Int) {
    DEFAULT(0),
    LIMIT(1);

    companion object {
        fun getByValue(value: Int) = values().find { it.type == value } ?: DEFAULT
    }
}

/**
 * 卡池类型
 */
enum class GachaType(val typeName: String) {
    UNKNOWN(""),
    LIMIT("限定"),
    RE_LIMIT("复刻限定"),
    NORMAL("常驻"),
    RE_NORMAL("复刻常驻"),
    FES("公主庆典"),
    ANNIV("周年");
}

/**
 *菜单
 */
enum class ToolMenuType(val id: Int) {
    CHARACTER(200),
    EQUIP(201),
    GUILD(202),
    CLAN(203),
    RANDOM_AREA(204),
    GACHA(205),
    EVENT(206),
    NEWS(207),
    FREE_GACHA(208),
    PVP_SEARCH(209),
    LEADER(210),
    TWEET(211),
    COMIC(212),
    ALL_SKILL(213),
    ALL_EQUIP(214),
    MOCK_GACHA(215),
    BIRTHDAY(216),
    CALENDAR_EVENT(217);

    companion object {
        fun getByValue(value: Int) = values()
            .find { it.id == value }
    }
}

/**
 * 站位
 */
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

/**
 * 技能类型
 */
enum class SkillType {
    ALL,
    NORMAL,
    SP
}