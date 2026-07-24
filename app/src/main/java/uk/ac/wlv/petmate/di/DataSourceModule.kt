package uk.ac.wlv.petmate.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton
import uk.ac.wlv.petmate.data.datasources.local.UserCache
import uk.ac.wlv.petmate.data.datasources.remote.AppointmentRemoteDataSource
import uk.ac.wlv.petmate.data.datasources.remote.ImageDataSource
import uk.ac.wlv.petmate.data.datasources.remote.PetRemoteDataSource
import uk.ac.wlv.petmate.data.datasources.remote.UserDataSource
import uk.ac.wlv.petmate.data.datasources.remote.VetRemoteDataSource

@Module
@InstallIn(SingletonComponent::class)
object DataSourceModule {


    @Provides
    @Singleton
    fun provideUserDataSource(): UserDataSource {

        return UserDataSource()
    }


    @Provides
    @Singleton
    fun provideImageDataSource(
        @ApplicationContext context: Context
    ): ImageDataSource {

        return ImageDataSource(context)
    }


    @Provides
    @Singleton
    fun providePetRemoteDataSource(
        userCache: UserCache
    ): PetRemoteDataSource {

        return PetRemoteDataSource(
            userCache
        )
    }



    @Provides
    @Singleton
    fun provideVetRemoteDataSource(): VetRemoteDataSource {

        return VetRemoteDataSource()
    }


    @Provides
    @Singleton
    fun provideAppointmentRemoteDataSource(
        userCache: UserCache
    ): AppointmentRemoteDataSource {

        return AppointmentRemoteDataSource(userCache)
    }

}