package uk.ac.wlv.petmate.di

import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import uk.ac.wlv.petmate.data.datasources.local.UserCache
import uk.ac.wlv.petmate.data.datasources.local.UserCacheImpl
import uk.ac.wlv.petmate.data.datasources.remote.AppointmentRemoteDataSource
import uk.ac.wlv.petmate.data.datasources.remote.ImageDataSource
import uk.ac.wlv.petmate.data.datasources.remote.PetRemoteDataSource
import uk.ac.wlv.petmate.data.datasources.remote.UserDataSource
import uk.ac.wlv.petmate.data.datasources.remote.VetRemoteDataSource
import uk.ac.wlv.petmate.data.network.ApiClient
import uk.ac.wlv.petmate.data.network.InternetChecker
import uk.ac.wlv.petmate.data.network.NominatimService
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
import uk.ac.wlv.petmate.services.GoogleAuthService
import uk.ac.wlv.petmate.services.LocationService
import uk.ac.wlv.petmate.viewmodel.AppointmentViewModel
import uk.ac.wlv.petmate.viewmodel.AuthViewModel
import uk.ac.wlv.petmate.viewmodel.BaseViewModel
import uk.ac.wlv.petmate.viewmodel.PetProfileViewModel
import uk.ac.wlv.petmate.viewmodel.SessionViewModel
import uk.ac.wlv.petmate.viewmodel.VetViewModel

val viewModelModule = module {

    // ── Services ──────────────────────────────────────────────────────
    single { LocationService(androidContext()) }
    single { GoogleAuthService(androidContext()) }

    // ── Nominatim (free location search) ─────────────────────────────
    single<NominatimService> { ApiClient.nominatimApi }


    single<UserCache> {
        UserCacheImpl(androidContext())
    }

    single {
        UserDataSource()
    }
    single<AuthRepository> {
        AuthRepositoryImpl(
            userCache = get(),
            UserDataSource = get()
        )
    }
    single {
        ImageDataSource(androidContext())
    }
    single<ImageRepository> {
        ImageRepositoryImpl(
get()
        )
    }

    single<LocationSearchRepository> {
        LocationSearchRepositoryImpl(ApiClient.nominatimApi)
    }
    single { InternetChecker(androidContext()) }

    viewModel {
        BaseViewModel()
    }
    viewModel {
        SessionViewModel(get())
    }
    viewModel { AuthViewModel(get(),get(),get()) }
    single { PetRemoteDataSource(get  ()) }
    single<PetRepository> {
        PetRepositoryImpl(get())
    }

    viewModel {
        PetProfileViewModel(
           get(),
            get(),
             get(),
            get()
        )
    }
    single { VetRemoteDataSource() }
    single<VetRepository> {
        VetRepositoryImpl(get())
    }
    viewModel {
        VetViewModel(
            get(),
get(),
            get()
        )
    }
    single { AppointmentRemoteDataSource(get()) }
    single<AppointmentRepository> {
        AppointmentRepositoryImpl(get())
    }

    viewModel {
        AppointmentViewModel(
          get()
        )
    }
}