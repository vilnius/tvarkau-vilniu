package lt.vilnius.tvarkau.api

import lt.vilnius.tvarkau.entity.City
import lt.vilnius.tvarkau.entity.Report
import lt.vilnius.tvarkau.entity.ReportStatus
import lt.vilnius.tvarkau.entity.ReportType
import lt.vilnius.tvarkau.entity.User

class CitiesResponse(val cities: List<City>) : BaseResponse()

class ReportsResponse(val reports: List<Report>) : BaseResponse()

class ReportTypeResponse(val reportTypes: List<ReportType>) : BaseResponse()

class ReportStatusesResponse(val reportStatuses: List<ReportStatus>) : BaseResponse()

class ReportResponse(val report: Report) : BaseResponse()

class UserResponse(val user: User) : BaseResponse()
