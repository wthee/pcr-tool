package cn.wthee.pcrtool.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.EquipmentRepository
import cn.wthee.pcrtool.data.model.EquipmentData
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EquipmentViewModel @Inject constructor(
    private val equipmentRepository: EquipmentRepository
) : ViewModel() {

    var equipments = MutableLiveData<List<EquipmentData>>()
    var isLoading = MutableLiveData<Boolean>()
    var refresh = MutableLiveData<Boolean>()
    var isList = MutableLiveData<Boolean>()

    //获取装备列表
    fun getEquips() {
        isLoading.postValue(true)
        viewModelScope.launch {
            val data = equipmentRepository.getAllEquipments()
            equipments.postValue(data)
            isLoading.postValue(false)
            refresh.postValue(false)
        }
    }
}
