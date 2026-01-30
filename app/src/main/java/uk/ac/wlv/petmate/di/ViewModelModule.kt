package uk.ac.wlv.petmate.di


import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import uk.ac.wlv.petmate.data.datasources.local.UserCache
import uk.ac.wlv.petmate.data.datasources.local.UserCacheImpl
import uk.ac.wlv.petmate.data.datasources.remote.FirebaseUserDataSource
import uk.ac.wlv.petmate.data.datasources.remote.ImageDataSource
import uk.ac.wlv.petmate.data.datasources.remote.PetRemoteDataSource
import uk.ac.wlv.petmate.data.network.InternetChecker
import uk.ac.wlv.petmate.data.repository.AuthRepository
import uk.ac.wlv.petmate.data.repository.ImageRepository
import uk.ac.wlv.petmate.data.repository.PetRepository
import uk.ac.wlv.petmate.data.repository.impl.AuthRepositoryImpl
import uk.ac.wlv.petmate.data.repository.impl.ImageRepositoryImpl
import uk.ac.wlv.petmate.data.repository.impl.PetRepositoryImpl
import uk.ac.wlv.petmate.viewmodel.AuthViewModel
import uk.ac.wlv.petmate.viewmodel.BaseViewModel
import uk.ac.wlv.petmate.viewmodel.PetProfileViewModel
import uk.ac.wlv.petmate.viewmodel.SessionViewModel

val viewModelModule = module {
    single<UserCache> {
        UserCacheImpl(androidContext())
    }

    single {
        FirebaseUserDataSource()
    }
    single<AuthRepository> {
        AuthRepositoryImpl(
            userCache = get(),
            firebaseDataSource = get()
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
    single { InternetChecker(androidContext()) }

    viewModel {
        BaseViewModel()
    }
    viewModel {
        SessionViewModel(get())
    }
    viewModel { AuthViewModel(get(),get()) }
    single { PetRemoteDataSource() }
    single<PetRepository> {
        PetRepositoryImpl(get())
    }

    viewModel {
        PetProfileViewModel(
           get(),
            get(),
             get()
        )
    }
}