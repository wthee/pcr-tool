package cn.wthee.pcrtool.di

import cn.wthee.pcrtool.data.network.MyAPIService
import cn.wthee.pcrtool.utils.ApiUtil
import cn.wthee.pcrtool.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
class APIServiceModule {

    @Provides
    fun provideMyAPIService(): MyAPIService {
        return ApiUtil.create(
            MyAPIService::class.java,
            Constants.API_URL,
            10
        )
    }

}