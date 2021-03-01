package cn.wthee.pcrtool.data.db.repository

import cn.wthee.pcrtool.data.db.dao.CharacterDao
import cn.wthee.pcrtool.data.enums.SortType
import cn.wthee.pcrtool.data.model.FilterCharacter

/**
 * 角色 Repository
 *
 * 数据来源 [CharacterDao]
 */
class CharacterRepository(private val characterDao: CharacterDao) {

    fun getInfoAndData(
        sortType: SortType,
        asc: Boolean,
        name: String,
        filter: FilterCharacter
    ) = characterDao.getInfoAndData(
        sortType.type,
        if (asc) "asc" else "desc",
        name,
        filter.position()[0],
        filter.position()[1],
        filter.atk,
        filter.guild,
        if (filter.all) 1 else 0,
        if (filter.r6) 1 else 0,
        filter.starIds
    )

    suspend fun getInfoAndDataCount(name: String, filter: FilterCharacter) =
        characterDao.getInfoAndDataCount(
            name,
            filter.position()[0],
            filter.position()[1],
            filter.atk,
            filter.guild,
            if (filter.all) 1 else 0,
            if (filter.r6) 1 else 0,
            filter.starIds
        )

    suspend fun getInfoPro(uid: Int) = characterDao.getInfoPro(uid)

    suspend fun getCharacterByPosition(start: Int, end: Int) =
        characterDao.getCharacterByPosition(start, end)

    suspend fun getEquipmentIds(unitId: Int, rank: Int) =
        characterDao.getRankEquipment(unitId, rank)

    suspend fun getRankStatus(unitId: Int, rank: Int) = characterDao.getRankStatus(unitId, rank)

    suspend fun getRarity(unitId: Int, rarity: Int) = characterDao.getRarity(unitId, rarity)

    suspend fun getMaxRank(id: Int) = characterDao.getMaxRank(id)

    suspend fun getMaxRarity(id: Int) = characterDao.getMaxRarity(id)

    suspend fun getCharacterSkill(id: Int) = characterDao.getCharacterSkill(id)

    suspend fun getSkillData(sid: Int) = characterDao.getSkillData(sid)

    suspend fun getSkillActions(aids: List<Int>) = characterDao.getSkillActions(aids)

    suspend fun getMaxLevel() = characterDao.getMaxLevel()

    suspend fun getAttackPattern(unitId: Int) = characterDao.getAttackPattern(unitId)

    suspend fun getGuilds() = characterDao.getGuilds()

    suspend fun getR6Ids() = characterDao.getR6Ids()

    suspend fun getItemDropInfos(unitId: Int) = characterDao.getItemDropInfos(unitId)

    suspend fun getCharacterStoryStatus(unitId: Int) = characterDao.getCharacterStoryStatus(unitId)

    companion object {

        @Volatile
        private var instance: CharacterRepository? = null

        fun getInstance(characterDao: CharacterDao) =
            instance ?: synchronized(this) {
                instance ?: CharacterRepository(characterDao).also { instance = it }
            }
    }
}