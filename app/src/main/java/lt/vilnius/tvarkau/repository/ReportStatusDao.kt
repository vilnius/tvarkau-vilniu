package lt.vilnius.tvarkau.repository

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import lt.vilnius.tvarkau.entity.ReportStatus

@Dao
interface ReportStatusDao {

    @Query("SELECT * FROM report_statuses where id = :reportStatusId")
    abstract fun getById(reportStatusId: Int): ReportStatus?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertAll(reportStatuses: List<ReportStatus>)

    @Query("SELECT * FROM report_statuses")
    abstract fun getAll() : List<ReportStatus>
}
