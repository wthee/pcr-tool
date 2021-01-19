package cn.wthee.pcrtool.utils

object CharacterIdUtil {

    fun getAllPicUrl(id: Int, r6Id: Boolean): ArrayList<String> {
        val list = arrayListOf<String>()
        if (r6Id) {
            list.add(Constants.CHARACTER_FULL_URL + getStarId(id, 6) + Constants.WEBP)
        }
        list.add(Constants.CHARACTER_FULL_URL + getStarId(id, 3) + Constants.WEBP)
        list.add(Constants.CHARACTER_URL + getStarId(id, 1) + Constants.WEBP)
        if (!Constants.notExistsIDs.contains(getFixedId(id))) {
            list.add(Constants.Reality_CHARACTER_URL + getFixedId(id) + Constants.WEBP)
        }
        return list
    }

    fun getAllIconUrl(uid: Int, r6Id: Boolean): ArrayList<String> {
        val list = arrayListOf<String>()

        if (r6Id) {
            list.add(Constants.UNIT_ICON_URL + getStarId(uid, 6) + Constants.WEBP)
        }
        list.add(Constants.UNIT_ICON_URL + getStarId(uid, 3) + Constants.WEBP)
        list.add(Constants.UNIT_ICON_URL + getStarId(uid, 1) + Constants.WEBP)
        return list
    }

    private fun getStarId(uid: Int, star: Int): String {
        val idStr = uid.toString()
        return idStr.substring(0, 4) + star + idStr[idStr.lastIndex]
    }

    //去除无效id
    private fun getFixedId(id: Int) = id + if (Constants.errorIDs.contains(id)) 31 else 30


}