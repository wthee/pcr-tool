package cn.wthee.pcrtool.utils

import cn.wthee.pcrtool.MyApplication.Companion.URL_DOMAIN
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.VideoType
import cn.wthee.pcrtool.ui.MainActivity.Companion.r6Ids

/**
 * 图片资源地址
 */
class ImageRequestHelper {

    var type = "cn"

    init {
        type = getRegionCode()
    }

    companion object {

        fun getInstance() = ImageRequestHelper()

        private var RESOURCE_PREFIX_URL = "https://${URL_DOMAIN}/redive/"
        private var RESOURCE = "/resource/"

        //图片格式
        const val WEBP = ".webp"

        //视频格式
        const val MP4 = ".mp4"

        //角色卡面
        const val CARD_PROFILE = "card/profile/"

        //角色完整卡面
        const val CARD_FULL = "card/full/"

        //现实角色卡面
        const val CARD_ACTUAL_PROFILE = "card/actual_profile/"

        //角色剧情卡面
        const val CARD_STORY = "card/story/"

        //装备图标
        const val UNKNOWN_EQUIP_ID = 999999
        const val ICON_EQUIPMENT = "icon/equipment/"
        const val ICON_EXTRA_EQUIPMENT = "icon/ex_equipment/"
        const val ICON_EXTRA_EQUIPMENT_CATEGORY = "icon/ex_equipment/category/"
        const val ICON_EXTRA_EQUIPMENT_TRAVEL_MAP = "icon/ex_equipment/map/"

        //角色图标
        const val ICON_UNIT = "icon/unit/"

        //技能图标
        const val ICON_SKILL = "icon/skill/"

        //剧情活动 banner
        const val EVENT_BANNER = "event/banner/"
        const val EVENT_TEASER = "event/teaser/"

        //剧情活动剧情
        const val EVENT_STORY = "event/story/"

        //技能动画
        const val SKILL_MOVIE = "movie/skill/"

        //动态卡面
        const val CARD_MOVIE = "movie/card/"

        //过场漫画
        const val COMIC = "comic/"
    }

    /**
     * 获取资源地址前缀
     * @param forceJpType 使用日服图片资源
     */
    fun getUrl(resUrl: String, id: Any, forceJpType: Boolean = true) =
        RESOURCE_PREFIX_URL + (if (forceJpType) "jp" else type) + RESOURCE + resUrl + id.toString() + WEBP

    fun getResourcePrefixUrl() = RESOURCE_PREFIX_URL

    /**
     * 获取过场漫画资源地址
     * @param resourceType 资源类型
     */
    fun getComicUrl(id: Any, resourceType: String) =
        RESOURCE_PREFIX_URL + resourceType + RESOURCE + COMIC + id.toString() + WEBP


    //获取动画列表
    fun getMovieUrlList(unitId: Int, videoType: VideoType): List<String> {
        val list = arrayListOf<String>()

        when (videoType) {
            VideoType.UB_SKILL -> {
                val url =
                    RESOURCE_PREFIX_URL + "jp" + RESOURCE + SKILL_MOVIE + unitId.toString() + MP4
                list.add(url)
            }

            VideoType.CHARACTER_CARD -> {
                if (r6Ids.contains(unitId)) {
                    val sixStarUrl = RESOURCE_PREFIX_URL + "jp" + RESOURCE + CARD_MOVIE + getStarId(
                        unitId,
                        6
                    ) + MP4
                    list.add(sixStarUrl)
                }
                val normalUrl =
                    RESOURCE_PREFIX_URL + "jp" + RESOURCE + CARD_MOVIE + getStarId(unitId, 3) + MP4

                list.add(normalUrl)
            }

            VideoType.UNKNOWN -> {}
        }

        return list
    }


    //获取装备图标
    fun getEquipPic(id: Int) = if (id == UNKNOWN_EQUIP_ID) {
        R.drawable.unknown_item
    } else {
        getUrl(ICON_EQUIPMENT, id.toString())
    }

    /**
     * 获取角色卡面 urls
     *
     * @param unitId 角色编号
     */
    fun getAllPicUrl(unitId: Int, actualId: Int?): ArrayList<String> {
        val list = arrayListOf<String>()
        if (r6Ids.contains(unitId)) {
            list.add(getUrl(CARD_FULL, getStarId(unitId, 6)))
        }
        list.add(getUrl(CARD_FULL, getStarId(unitId, 3)))
        list.add(getUrl(CARD_PROFILE, getStarId(unitId, 1)))
        if (actualId != null) {
            list.add(getUrl(CARD_ACTUAL_PROFILE, actualId))
        }
        return list
    }

    /**
     * 获取星级最高的角色图片
     *
     * @param unitId 角色编号
     */
    fun getMaxCardUrl(unitId: Int): String {
        if (r6Ids.contains(unitId)) {
            return getUrl(CARD_FULL, getStarId(unitId, 6))
        }
        return getUrl(CARD_FULL, getStarId(unitId, 3))
    }

    /**
     * 获取星级最高的角色图标
     *
     * @param unitId 角色编号
     */
    fun getMaxIconUrl(unitId: Int): String {
        if (r6Ids.contains(unitId)) {
            return getUrl(ICON_UNIT, getStarId(unitId, 6))
        }
        return getUrl(ICON_UNIT, getStarId(unitId, 3))
    }

    /**
     * 获取角色图标
     *
     * @param unitId 角色编号
     * @param star 星级
     */
    fun getUnitIconUrl(unitId: Int, star: Int): String {
        return getUrl(ICON_UNIT, getStarId(unitId, star))
    }

    /**
     * 获取星级 [star] id
     */
    private fun getStarId(unitId: Int, star: Int): String {
        return try {
            val idStr = unitId.toString()
            idStr.substring(0, 4) + star + idStr[idStr.lastIndex]
        } catch (e: Exception) {
            ""
        }
    }

}

