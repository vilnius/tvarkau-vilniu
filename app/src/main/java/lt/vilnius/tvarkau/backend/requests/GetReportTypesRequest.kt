package lt.vilnius.tvarkau.backend.requests

import lt.vilnius.tvarkau.backend.ApiMethod.GET_PROBLEM_TYPES
import lt.vilnius.tvarkau.backend.ApiRequest
import lt.vilnius.tvarkau.backend.GetProblemParams

/**
 * @author Martynas Jurkus
 */
class GetReportTypesRequest : ApiRequest<GetProblemParams>(GET_PROBLEM_TYPES, null)
