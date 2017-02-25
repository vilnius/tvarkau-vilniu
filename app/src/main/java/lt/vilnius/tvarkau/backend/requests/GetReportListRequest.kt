package lt.vilnius.tvarkau.backend.requests

import lt.vilnius.tvarkau.backend.ApiMethod.GET_PROBLEMS
import lt.vilnius.tvarkau.backend.ApiRequest
import lt.vilnius.tvarkau.backend.GetProblemsParams

/**
 * @author Martynas Jurkus
 */
class GetReportListRequest(params: GetProblemsParams) : ApiRequest<GetProblemsParams>(GET_PROBLEMS, params)
