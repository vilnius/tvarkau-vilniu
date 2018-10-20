package lt.vilnius.tvarkau.dagger.module

import android.arch.persistence.room.Room
import android.os.Debug
import dagger.Module
import dagger.Provides
import lt.vilnius.tvarkau.TvarkauApplication
import lt.vilnius.tvarkau.database.AppDatabase
import javax.inject.Singleton


@Module
class DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(context: TvarkauApplication): AppDatabase {
        val builder = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "app_database.db"
        )
            .fallbackToDestructiveMigration()
        if (Debug.isDebuggerConnected()) {
            builder.allowMainThreadQueries()
        }
        return builder.build()
    }

    @Singleton
    @Provides
    fun provideReportsDao(db: AppDatabase) = db.reportsDao()

    @Singleton
    @Provides
    fun provideReportTypesDao(db: AppDatabase) = db.reportTypesDao()

    @Singleton
    @Provides
    fun provideReportStatusesDao(db: AppDatabase) = db.reportStatusesDao()
}
