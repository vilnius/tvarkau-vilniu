package lt.vilnius.tvarkau.repository

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import lt.vilnius.tvarkau.entity.ReportType

@Dao
interface ReportTypeDao {

    @Query("SELECT * FROM report_types where id = :reportTypeId")
    abstract fun getById(reportTypeId: Int): ReportType?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertAll(reportTypes: List<ReportType>)

    @Query("SELECT * FROM report_types")
    abstract fun getAll() : List<ReportType>
}
