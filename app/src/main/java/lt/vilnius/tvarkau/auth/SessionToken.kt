package lt.vilnius.tvarkau.auth

import io.reactivex.Completable

interface SessionToken {

    fun refreshGuestToken(): Completable
}
