package uk.ac.wlv.petmate.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton
import uk.ac.wlv.petmate.data.network.ApiClient
import uk.ac.wlv.petmate.data.network.NominatimService

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {


    @Provides
    @Singleton
    fun provideNominatimService(): NominatimService {

        return ApiClient.nominatimApi
    }


}