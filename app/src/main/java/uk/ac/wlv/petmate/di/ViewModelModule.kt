package uk.ac.wlv.petmate.di


import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import uk.ac.wlv.petmate.data.datasources.local.UserCache
import uk.ac.wlv.petmate.data.datasources.local.UserCacheImpl
import uk.ac.wlv.petmate.data.datasources.remote.FirebaseUserDataSource
import uk.ac.wlv.petmate.data.repository.AuthRepository
import uk.ac.wlv.petmate.data.repository.impl.AuthRepositoryImpl
import uk.ac.wlv.petmate.viewmodel.AuthViewModel
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

    viewModel {
        SessionViewModel(get())
    }
    viewModel { AuthViewModel(get(),get()) }
}