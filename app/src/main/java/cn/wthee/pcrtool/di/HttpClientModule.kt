package cn.wthee.pcrtool.di

import cn.wthee.pcrtool.data.network.ApiRepository
import cn.wthee.pcrtool.data.network.apiHttpClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient

@InstallIn(SingletonComponent::class)
@Module
class HttpClientModule {

    @Provides
    fun provideApiClientService(): HttpClient {
        return apiHttpClient
    }

    @Provides
    fun provideApiRepositoryService(client: HttpClient): ApiRepository {
        return ApiRepository(client)
    }
}