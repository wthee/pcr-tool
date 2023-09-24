package cn.wthee.pcrtool.data.db.repository

import cn.wthee.pcrtool.data.db.dao.UnitDao
import cn.wthee.pcrtool.data.db.view.CharacterInfo
import cn.wthee.pcrtool.data.db.view.GachaUnitInfo
import cn.wthee.pcrtool.data.enums.CharacterSortType
import cn.wthee.pcrtool.data.model.FilterCharacter
import cn.wthee.pcrtool.utils.formatTime
import cn.wthee.pcrtool.utils.second
import javax.inject.Inject

/**
 * 角色 Repository
 *
 * @param unitDao
 */
class UnitRepository @Inject constructor(private val unitDao: UnitDao) {

    suspend fun getCharacterInfoList(filter: FilterCharacter, limit: Int): List<CharacterInfo> {
        //额外角色编号
        val exUnitIdList = try {
            unitDao.getExUnitIdList()
        } catch (_: Exception) {
            arrayListOf()
        }

        var filterList = unitDao.getCharacterInfoList(
            filter.sortType.type,
            if (filter.asc) "asc" else "desc",
            filter.name,
            filter.position()[0],
            filter.position()[1],
            filter.atk,
            when {
                //公会
                filter.guild > 1 -> getGuilds()[filter.guild - 2].guildId
                //无公会
                filter.guild == 1 -> -1
                //全部
                else -> 0
            },
            if (filter.all) 1 else 0,
            //六星排序时，仅显示六星角色
            if (filter.sortType == CharacterSortType.SORT_UNLOCK_6) 1 else filter.r6,
            filter.starIds,
            filter.type,
            limit,
            exUnitIdList,
            when {
                //种族
                filter.race > 1 -> getRaces()[filter.race - 2]
                //多人卡
                filter.race == 1 -> "-"
                //全部
                else -> ""
            },
        )

        //按日期排序时，由于数据库部分日期格式有问题，导致排序不对，需要重新排序
        if (filter.sortType == CharacterSortType.SORT_DATE) {
            filterList = filterList.sortedWith { o1, o2 ->
                val sd1 = o1.startTime.formatTime
                val sd2 = o2.startTime.formatTime
                when {
                    sd1.second(sd2) > 0 -> 1
                    sd1.second(sd2) == 0L -> {
                        o1.id.compareTo(o2.id)
                    }

                    else -> -1
                } * (if (filter.asc) 1 else -1)
            }
        }


        return filterList
    }

    suspend fun getCount(): String {
        val unknownCount = unitDao.getUnknownCount()
        return unitDao.getCount().toString() + if (unknownCount > 0) " (${unknownCount})" else ""
    }

    suspend fun getCountInt() = unitDao.getCount()

    suspend fun getCharacterBasicInfo(unitId: Int): CharacterInfo? {
        //额外角色编号
        val exUnitIdList = try {
            unitDao.getExUnitIdList()
        } catch (_: Exception) {
            arrayListOf()
        }
        return unitDao.getCharacterBasicInfo(unitId, exUnitIdList)
    }

    suspend fun getInfoPro(unitId: Int) = unitDao.getInfoPro(unitId)

    suspend fun getRoomComments(unitId: Int) = unitDao.getRoomComments(unitId)

    suspend fun getMultiIds(unitId: Int) = unitDao.getMultiIds(unitId)

    suspend fun getCharacterByPosition(start: Int, end: Int) =
        unitDao.getCharacterByPosition(start, end)

    suspend fun getCharacterByIds(unitIds: List<Int>) = unitDao.getCharacterByIds(unitIds)

    suspend fun getEquipmentIds(unitId: Int, rank: Int) =
        unitDao.getRankEquipment(unitId, rank)

    suspend fun getRankStatus(unitId: Int, rank: Int) = unitDao.getRankStatus(unitId, rank)

    suspend fun getRarity(unitId: Int, rarity: Int) = unitDao.getRarity(unitId, rarity)

    suspend fun getMaxRank(unitId: Int) = unitDao.getMaxRank(unitId)

    suspend fun getMaxRarity(unitId: Int) = unitDao.getMaxRarity(unitId)

    suspend fun getGuilds() = unitDao.getGuilds()

    suspend fun getRaces() = unitDao.getRaces()

    suspend fun getAllGuildMembers() = unitDao.getAllGuildMembers()

    suspend fun getNoGuildMembers() = unitDao.getNoGuildMembers()

    suspend fun getR6Ids() = unitDao.getR6Ids()

    suspend fun getCharacterStoryStatus(unitId: Int) = unitDao.getCharacterStoryStatus(unitId)

    suspend fun getMaxLevel() = unitDao.getMaxLevel()

    suspend fun getRankBonus(rank: Int, unitId: Int) = unitDao.getRankBonus(rank, unitId)

    suspend fun getCoefficient() = unitDao.getCoefficient()

    suspend fun getCutinId(unitId: Int) = unitDao.getCutinId(unitId)

    suspend fun getSummonData(unitId: Int) = unitDao.getSummonData(unitId)

    suspend fun getActualId(unitId: Int) = unitDao.getActualId(unitId)

    suspend fun getGachaUnits(type: Int): List<GachaUnitInfo> {
        //额外角色编号
        val exUnitIdList = try {
            unitDao.getExUnitIdList()
        } catch (_: Exception) {
            arrayListOf()
        }
        return unitDao.getGachaUnits(type, exUnitIdList)
    }

    suspend fun getHomePageComments(unitId: Int) = unitDao.getHomePageComments(unitId)

    suspend fun getAtkCastTime(unitId: Int) = unitDao.getAtkCastTime(unitId)

}