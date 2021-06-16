package cn.wthee.pcrtool.utils

import cn.wthee.pcrtool.ui.MainActivity

/**
 * 角色 id 工具
 */
object CharacterIdUtil {

    /**
     * 获取角色卡面 urls
     *
     * @param unitId 角色编号
     * @param r6Id 是否已解放六星
     */
    fun getAllPicUrl(unitId: Int, r6Id: Boolean): ArrayList<String> {
        val list = arrayListOf<String>()
        if (r6Id) {
            list.add(Constants.CHARACTER_FULL_URL + getStarId(unitId, 6) + Constants.WEBP)
        }
        list.add(Constants.CHARACTER_FULL_URL + getStarId(unitId, 3) + Constants.WEBP)
        list.add(Constants.CHARACTER_URL + getStarId(unitId, 1) + Constants.WEBP)
        if (!Constants.notExistsIDs.contains(getFixedId(unitId))) {
            list.add(Constants.Reality_CHARACTER_URL + getFixedId(unitId) + Constants.WEBP)
        }
        return list
    }

    /**
     * 获取星级最高的角色图片
     *
     * @param unitId 角色编号
     */
    fun getMaxCardUrl(unitId: Int): String {
        if (MainActivity.r6Ids.contains(unitId)) {
            return Constants.CHARACTER_FULL_URL + getStarId(unitId, 6) + Constants.WEBP
        }
        return if (unitId == 106701) {
            Constants.CHARACTER_URL + getStarId(unitId, 1)
        } else {
            Constants.CHARACTER_FULL_URL + getStarId(unitId, 3)
        } + Constants.WEBP
    }

    /**
     * 获取星级最高的角色图标
     *
     * @param unitId 角色编号
     * @param r6Id 是否已解放六星
     */
    fun getMaxIconUrl(unitId: Int, r6Id: Boolean): String {
        if (r6Id) {
            return Constants.UNIT_ICON_URL + getStarId(unitId, 6) + Constants.WEBP
        }
        return Constants.UNIT_ICON_URL + getStarId(unitId, 3) + Constants.WEBP
    }

    /**
     * 获取角色图片 urls
     */
    fun getAllIconUrl(unitId: Int, r6Id: Boolean): ArrayList<String> {
        val list = arrayListOf<String>()

        if (r6Id) {
            list.add(Constants.UNIT_ICON_URL + getStarId(unitId, 6) + Constants.WEBP)
        }
        list.add(Constants.UNIT_ICON_URL + getStarId(unitId, 3) + Constants.WEBP)
        list.add(Constants.UNIT_ICON_URL + getStarId(unitId, 1) + Constants.WEBP)
        if (!Constants.notExistsIDs.contains(getFixedId(unitId))) {
            list.add(Constants.UNKNOWN_EQUIPMENT_ICON)
        }
        return list
    }

    /**
     * 获取星级 [star] id
     */
    private fun getStarId(unitId: Int, star: Int): String {
        try {
            val idStr = unitId.toString()
            return idStr.substring(0, 4) + star + idStr[idStr.lastIndex]
        } catch (e: Exception) {
            return ""
        }
    }

    /**
     * 去除无效id
     */
    private fun getFixedId(unitId: Int) =
        unitId + if (Constants.errorIDs.contains(unitId)) 31 else 30


}