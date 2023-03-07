package cn.wthee.pcrtool.data.enums

import cn.wthee.pcrtool.R

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
}

/**
 * 卡池类型
 */
enum class GachaType(val stringId: Int) {
    UNKNOWN(R.string.unknown),
    LIMIT(R.string.type_limit),
    RE_LIMIT(R.string.limit_re),
    RE_LIMIT_PICK(R.string.limit_re_pick),
    NORMAL(R.string.type_normal),
    RE_NORMAL(R.string.normal_re),
    FES(R.string.fes),
    ANNIV(R.string.anv);
}

/**
 * 模拟卡池类型
 */
enum class MockGachaType(val type: Int) {
    PICK_UP(0),
    FES(1),
    PICK_UP_SINGLE(2);

    companion object {
        fun getByValue(value: Int) = MockGachaType.values()
            .find { it.type == value } ?: PICK_UP
    }
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
    CALENDAR_EVENT(217),
    EXTRA_EQUIP(218),
    TRAVEL_AREA(219),
    WEBSITE(220),
    LEADER_TIER(221),
    ALL_QUEST(222);


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

/**
 * 属性值类型
 */
enum class AttrValueType {
    INT,
    DOUBLE,
    PERCENT
}

/**
 * 首页
 */
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

/**
 * 设置枚举
 */
enum class SettingSwitchType {
    /**
     * 振动设置
     */
    VIBRATE,

    /**
     * 动画效果设置
     */
    ANIMATION,

    /**
     * 动态色彩设置
     */
    DYNAMIC_COLOR,

    /**
     * 使用ip访问（仅网络异常使用）
     */
    USE_IP
}


/**
 * 公告
 */
enum class NewsType(val stringId: Int) {
    UPDATE(R.string.update),
    SYSTEM(R.string.system),
    NEWS(R.string.tool_news),
    EVENT(R.string.event),
    SHOP(R.string.shop),
    LOCAL(R.string.local_note);
}

/**
 * 查询关键词
 */
enum class KeywordType(val type: Int) {
    NEWS(1),
    TWEET(2);
}

/**
 * 角色评级
 */
enum class LeaderTierType(val type: Int) {
    ALL(0),
    PVP_ATK(1),
    PVP_DEF(2),
    CLAN(3);

    companion object {
        fun getByValue(value: Int) = values()
            .find { it.type == value } ?: ALL
    }
}

/**
 * 技能下标类型
 */
enum class SkillIndexType(val index: Int) {
    UNKNOWN(-1),
    UB(0),
    UB_PLUS(0),
    MAIN_SKILL_1(1),
    MAIN_SKILL_2(2),
    MAIN_SKILL_3(3),
    MAIN_SKILL_4(4),
    MAIN_SKILL_5(5),
    MAIN_SKILL_6(6),
    MAIN_SKILL_7(7),
    MAIN_SKILL_8(8),
    MAIN_SKILL_9(9),
    MAIN_SKILL_10(10),
    MAIN_SKILL_1_PLUS(1),
    MAIN_SKILL_2_PLUS(2),
    EX_1(1),
    EX_2(2),
    EX_3(3),
    EX_4(4),
    EX_5(5),
    EX_1_PLUS(1),
    EX_2_PLUS(2),
    EX_3_PLUS(3),
    EX_4_PLUS(4),
    EX_5_PLUS(5),
    SP_UB(0),
    SP_SKILL_1(1),
    SP_SKILL_2(2),
    SP_SKILL_3(3),
    SP_SKILL_4(4),
    SP_SKILL_5(5),
    SP_SKILL_1_PLUS(1),
    SP_SKILL_2_PLUS(2),
    ;
}

/**
 * 区服
 */
enum class RegionType(val value: Int) {
    CN(2),
    TW(3),
    JP(4),
    ;

    companion object {
        fun getByValue(value: Int) = RegionType.values()
            .find { it.value == value } ?: CN
    }
}

/**
 * 活动日程类型
 */
enum class CalendarEventType(val type: Int) {
    UNKNOWN(404),
    TOWER(1),
    SP_DUNGEON(-1),
    TDF(-2),
    DAILY(18),
    LOGIN(19),
    FORTUNE(20),
    N_DROP(31),
    N_MANA(41),
    H_DROP(32),
    H_MANA(42),
    VH_DROP(39),
    VH_MANA(49),
    EXPLORE(34),
    SHRINE(37),
    TEMPLE(38),
    DUNGEON(45),
    ;

    companion object {
        fun getByValue(value: Int) = CalendarEventType.values()
            .find { it.type == value } ?: UNKNOWN
    }
}