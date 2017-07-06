package lt.vilnius.tvarkau.dagger.module

import android.support.v7.app.AppCompatActivity
import com.nhaarman.mockito_kotlin.mock
import dagger.Module
import dagger.Provides
import lt.vilnius.tvarkau.navigation.NavigationManager

@Module
class TestActivityModule(private val activity: AppCompatActivity) {

    @Provides
    fun provideNavManager(): NavigationManager = mock()
}

