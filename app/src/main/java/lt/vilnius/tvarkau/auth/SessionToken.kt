package lt.vilnius.tvarkau.auth

import io.reactivex.Completable
import lt.vilnius.tvarkau.entity.SocialNetworkUser

interface SessionToken {

    fun refreshSocialLoginToken(socialNetworkUser: SocialNetworkUser): Completable

    fun refreshGuestToken(): Completable

    fun refreshCurrentToken(token: ApiToken): Completable

    fun refreshViispToken(ticket: String): Completable
}
