package cn.wthee.pcrtool.di

import cn.wthee.pcrtool.data.network.ApiRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
class ApiRepositoryModule {

    @Provides
    fun provideApiRepositoryService(): ApiRepository {
        return ApiRepository()
    }
}