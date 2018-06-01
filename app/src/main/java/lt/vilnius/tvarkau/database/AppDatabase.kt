package lt.vilnius.tvarkau.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import lt.vilnius.tvarkau.entity.Report
import lt.vilnius.tvarkau.entity.ReportStatus
import lt.vilnius.tvarkau.entity.ReportType
import lt.vilnius.tvarkau.repository.ReportStatusDao
import lt.vilnius.tvarkau.repository.ReportTypeDao
import lt.vilnius.tvarkau.repository.ReportsDao


@Database(
    entities = [
        Report::class,
        ReportType::class,
        ReportStatus::class
    ],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun reportsDao(): ReportsDao

    abstract fun reportTypesDao(): ReportTypeDao

    abstract fun reportStatusesDao(): ReportStatusDao
}
