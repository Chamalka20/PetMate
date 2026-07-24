package uk.ac.wlv.petmate.core.utils
import kotlinx.coroutines.flow.Flow
import uk.ac.wlv.petmate.data.network.InternetChecker
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class NetworkObserver @Inject constructor(
    private val internetChecker: InternetChecker
) {

    fun observeConnectivity(): Flow<Boolean> {
        return internetChecker.observeInternetConnectivity()
    }

}