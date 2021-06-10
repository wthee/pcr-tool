package cn.wthee.pcrtool.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.db.repository.EquipmentRepository
import cn.wthee.pcrtool.data.db.repository.UnitRepository
import cn.wthee.pcrtool.data.db.view.CharacterInfo
import cn.wthee.pcrtool.data.db.view.EquipmentMaxData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 首页纵览
 */
@HiltViewModel
class OverviewViewModel @Inject constructor(
    private val unitRepository: UnitRepository,
    private val equipmentRepository: EquipmentRepository
) : ViewModel() {

    var characterList = MutableLiveData<List<CharacterInfo>>()
    var equipList = MutableLiveData<List<EquipmentMaxData>>()


    /**
     * 获取角色列表
     */
    fun getCharacterList() {
        viewModelScope.launch {
            val data = unitRepository.getInfoAndData(10)
            characterList.postValue(data)
        }
    }

    /**
     * 获取装备列表
     */
    fun getEquipList() {
        viewModelScope.launch {
            val data = equipmentRepository.getEquipments(10)
            equipList.postValue(data)
        }
    }
}