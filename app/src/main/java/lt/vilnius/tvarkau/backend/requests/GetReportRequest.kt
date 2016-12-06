package lt.vilnius.tvarkau.backend.requests

import lt.vilnius.tvarkau.backend.ApiMethod.GET_REPORT
import lt.vilnius.tvarkau.backend.ApiRequest
import lt.vilnius.tvarkau.backend.GetProblemParams

/**
 * @author Martynas Jurkus
 */
class GetReportRequest(params: GetProblemParams) : ApiRequest<GetProblemParams>(GET_REPORT, params)
