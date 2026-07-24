package uk.ac.wlv.petmate.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton
import uk.ac.wlv.petmate.data.network.InternetChecker
import uk.ac.wlv.petmate.services.GoogleAuthService
import uk.ac.wlv.petmate.services.LocationService

@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {


    @Provides
    @Singleton
    fun provideLocationService(
        @ApplicationContext context: Context
    ): LocationService {

        return LocationService(context)
    }


    @Provides
    @Singleton
    fun provideGoogleAuthService(
        @ApplicationContext context: Context
    ): GoogleAuthService {

        return GoogleAuthService(context)
    }


    @Provides
    @Singleton
    fun provideInternetChecker(
        @ApplicationContext context: Context
    ): InternetChecker {

        return InternetChecker(context)
    }

}