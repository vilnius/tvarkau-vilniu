package lt.vilnius.tvarkau.fragments.presenters

import io.reactivex.Single

/**
 * @author Martynas Jurkus
 */
interface ConnectivityProvider {

    /**
     * Return true if network is connected.
     * Return [ConnectivityProviderImpl.NetworkConnectivityError] if network is not available
     */
    fun ensureConnected(): Single<Boolean>
}