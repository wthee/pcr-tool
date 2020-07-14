package cn.wthee.pcrtool.data


//角色数据Repository

class EquipmentRepository(private val equipmentDao: EquipmentDao) {


    //获取角色Rank所需装备具体属性
    suspend fun getEquipmentDatas(eids: List<Int>) = equipmentDao.getEquipmentDatas(eids)

    //获取装备具体属性
    suspend fun getEquipmentData(eid: Int) = equipmentDao.getEquipmentData(eid)

    //角色所有装备信息
    suspend fun getAllEquipments() = equipmentDao.getAllEquipments()

    //装备提升信息
    suspend fun getEquipmentEnhanceData(eid: Int) = equipmentDao.getEquipmentEnhanceData(eid)

    //    //装备掉落信息
//    suspend fun getEnemyRewardDatas(eid: Int) = equipmentDao.getEnemyRewardDatas(eid)
//
//    //装备掉落对应关卡信息
//    suspend fun getWaveGroupDatas(dropIds: List<Int>) = equipmentDao.getEnemyRewardDatas(dropIds)
//
//    //关卡信息
//    suspend fun getQuestDataDatas(waveIds: List<Int>) = equipmentDao.getEnemyRewardDatas(waveIds)
//
    //装备对应关卡信息
    suspend fun getDropWaveID(eid: Int) = equipmentDao.getDropWaveID(eid)

    //查找关卡掉落奖励id
    suspend fun getDropRewardID(waveIds: List<Int>) = equipmentDao.getDropRewardID(waveIds)

    //关卡道具掉落率
    suspend fun getOdds(rids: List<Int>) = equipmentDao.getOdds(rids)

    //装备碎片信息
    suspend fun getEquipmentCraft(eid: Int) = equipmentDao.getEquipmentCraft(eid)

    companion object {

        @Volatile
        private var instance: EquipmentRepository? = null

        fun getInstance(equipmentDao: EquipmentDao) =
            instance ?: synchronized(this) {
                instance ?: EquipmentRepository(equipmentDao).also { instance = it }
            }
    }
}