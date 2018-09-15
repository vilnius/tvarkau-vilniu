package lt.vilnius.tvarkau.repository

import android.arch.paging.DataSource
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import io.reactivex.Single
import lt.vilnius.tvarkau.entity.Report

@Dao
abstract class ReportsDao {

    @Query("SELECT * FROM reports")
    abstract fun reportsDataSourceFactory(): DataSource.Factory<Int, Report>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertAll(reports: List<Report>)

    @Query("DELETE FROM reports")
    abstract fun deleteAll()

    @Query("SELECT * FROM reports where id = :reportId")
    abstract fun getById(reportId: Int): Single<Report>
}
