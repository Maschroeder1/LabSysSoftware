package application

import model.*

class UfrgsService(
    private val loginRequester: LoginRequester,
    private val possibilitiesRequester: PossibilitiesRequester,
    private val collegeClassRequester: CollegeClassRequester
) {

    fun requestLogin(credentials: Login): LoginRequestResponse {
        return loginRequester.requestLogin(credentials)
    }

    fun requestEnrollmentPossibilities(cookie: Cookie): List<CollegeClass> {
        val possibilities = possibilitiesRequester.requestPossibilities(cookie)
        return collegeClassRequester.bulkRequest(possibilities)
    }
}