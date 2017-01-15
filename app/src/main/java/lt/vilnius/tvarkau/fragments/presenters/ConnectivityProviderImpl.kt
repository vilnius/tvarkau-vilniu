package lt.vilnius.tvarkau.fragments.presenters

import android.app.Activity
import lt.vilnius.tvarkau.utils.NetworkUtils
import rx.Single

/**
 * @author Martynas Jurkus
 */
class ConnectivityProviderImpl(
        private val activity: Activity
) : ConnectivityProvider {

    override fun ensureConnected(): Single<Boolean> {
        return Single.defer {
            val connected = NetworkUtils.isNetworkConnected(activity)

            if (connected) {
                Single.just(true)
            } else {
                Single.error(NetworkConnectivityError())
            }
        }
    }

    class NetworkConnectivityError : RuntimeException()
}