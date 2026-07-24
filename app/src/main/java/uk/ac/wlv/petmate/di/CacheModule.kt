package uk.ac.wlv.petmate.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton
import uk.ac.wlv.petmate.data.datasources.local.UserCache
import uk.ac.wlv.petmate.data.datasources.local.UserCacheImpl

@Module
@InstallIn(SingletonComponent::class)
abstract class CacheModule {


    @Binds
    @Singleton
    abstract fun bindUserCache(
        impl: UserCacheImpl
    ): UserCache

}