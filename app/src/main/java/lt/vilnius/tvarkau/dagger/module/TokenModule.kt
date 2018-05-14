package lt.vilnius.tvarkau.dagger.module

import dagger.Binds
import dagger.Module
import lt.vilnius.tvarkau.auth.SessionToken
import lt.vilnius.tvarkau.auth.SessionTokenImpl

@Module
abstract class TokenModule {

    @Binds
    abstract fun bindSessionToken(sessionToken: SessionTokenImpl): SessionToken
}
