package lt.vilnius.tvarkau.auth

import io.reactivex.Completable

interface SessionToken {

    fun refreshGuestToken(): Completable

    fun refreshCurrentToken(token: ApiToken): Completable
}
