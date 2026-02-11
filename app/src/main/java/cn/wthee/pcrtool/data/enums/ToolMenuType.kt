package cn.wthee.pcrtool.data.enums

import androidx.annotation.StringRes
import cn.wthee.pcrtool.R


/**
 *菜单
 */
enum class ToolMenuType(val id: Int, @param:StringRes val titleId: Int, val iconType: MainIconType) {
    CHARACTER(200, R.string.character, MainIconType.CHARACTER),
    EQUIP(201, R.string.tool_equip, MainIconType.EQUIP),
    GUILD(202, R.string.tool_guild, MainIconType.GUILD),
    CLAN(203, R.string.tool_clan, MainIconType.CLAN),
    RANDOM_AREA(204, R.string.random_area, MainIconType.RANDOM_AREA),
    GACHA(205, R.string.tool_gacha, MainIconType.GACHA),
    STORY_EVENT(206, R.string.tool_event, MainIconType.EVENT),
    NEWS(207, R.string.tool_news, MainIconType.NEWS),
    FREE_GACHA(208, R.string.tool_free_gacha, MainIconType.FREE_GACHA),
    PVP_SEARCH(209, R.string.tool_pvp, MainIconType.PVP_SEARCH),
    LEADER(210, R.string.tool_leader, MainIconType.LEADER),
    TWEET(211, R.string.tweet, MainIconType.TWEET),
    COMIC(212, R.string.comic_4, MainIconType.COMIC),

    ALL_EQUIP(214, R.string.calc_equip_count, MainIconType.EQUIP_CALC),
    MOCK_GACHA(215, R.string.tool_mock_gacha, MainIconType.MOCK_GACHA),
    BIRTHDAY(216, R.string.tool_birthday, MainIconType.BIRTHDAY),
    CALENDAR_EVENT(217, R.string.tool_calendar_event, MainIconType.CALENDAR),
    EXTRA_EQUIP(218, R.string.tool_extra_equip, MainIconType.EXTRA_EQUIP),
    TRAVEL_AREA(219, R.string.tool_travel, MainIconType.EXTRA_EQUIP_DROP),
    WEBSITE(220, R.string.tool_website, MainIconType.WEBSITE_BOOKMARK),
    LEADER_TIER(221, R.string.tool_leader_tier, MainIconType.LEADER_TIER),
    ALL_QUEST(222, R.string.tool_all_quest, MainIconType.ALL_QUEST),
    UNIQUE_EQUIP(223, R.string.tool_unique_equip, MainIconType.UNIQUE_EQUIP),
    LOAD_COMIC(224, R.string.tool_load_comic, MainIconType.LOAD_COMIC),
    TALENT_LIST(225, R.string.unit_talent, MainIconType.TALENT),
    TALENT_QUEST(226, R.string.talent_quest, MainIconType.TALENT_QUEST),
    ROLE(227, R.string.unit_role, MainIconType.ROLE),
    UNKNOWN_SKILL_LIST(999, R.string.skill, MainIconType.SKILL_LOOP),
    ;


    companion object {
        fun getByValue(value: Int) = entries
            .find { it.id == value }
    }
}