package uk.ac.wlv.petmate.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton
import uk.ac.wlv.petmate.data.repository.AppointmentRepository
import uk.ac.wlv.petmate.data.repository.AuthRepository
import uk.ac.wlv.petmate.data.repository.ImageRepository
import uk.ac.wlv.petmate.data.repository.LocationSearchRepository
import uk.ac.wlv.petmate.data.repository.PetRepository
import uk.ac.wlv.petmate.data.repository.VetRepository
import uk.ac.wlv.petmate.data.repository.impl.AppointmentRepositoryImpl
import uk.ac.wlv.petmate.data.repository.impl.AuthRepositoryImpl
import uk.ac.wlv.petmate.data.repository.impl.ImageRepositoryImpl
import uk.ac.wlv.petmate.data.repository.impl.LocationSearchRepositoryImpl
import uk.ac.wlv.petmate.data.repository.impl.PetRepositoryImpl
import uk.ac.wlv.petmate.data.repository.impl.VetRepositoryImpl

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {


    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        impl: AuthRepositoryImpl
    ): AuthRepository


    @Binds
    @Singleton
    abstract fun bindImageRepository(
        impl: ImageRepositoryImpl
    ): ImageRepository


    @Binds
    @Singleton
    abstract fun bindLocationRepository(
        impl: LocationSearchRepositoryImpl
    ): LocationSearchRepository


    @Binds
    @Singleton
    abstract fun bindPetRepository(
        impl: PetRepositoryImpl
    ): PetRepository


    @Binds
    @Singleton
    abstract fun bindVetRepository(
        impl: VetRepositoryImpl
    ): VetRepository


    @Binds
    @Singleton
    abstract fun bindAppointmentRepository(
        impl: AppointmentRepositoryImpl
    ): AppointmentRepository

}