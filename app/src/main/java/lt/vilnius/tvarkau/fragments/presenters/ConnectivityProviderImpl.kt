package lt.vilnius.tvarkau.fragments.presenters

import android.content.Context
import io.reactivex.Single
import lt.vilnius.tvarkau.utils.NetworkUtils

/**
 * @author Martynas Jurkus
 */
class ConnectivityProviderImpl(
        private val context: Context
) : ConnectivityProvider {

    override fun ensureConnected(): Single<Boolean> {
        return Single.defer {
            val connected = NetworkUtils.isNetworkConnected(context)

            if (connected) {
                Single.just(true)
            } else {
                Single.error(NetworkConnectivityError())
            }
        }
    }

    class NetworkConnectivityError : RuntimeException()
}