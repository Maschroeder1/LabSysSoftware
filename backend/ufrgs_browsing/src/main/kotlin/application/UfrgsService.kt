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

    fun startPossibilitiesProcessing(cookie: Cookie): Int {
        val possibilities = possibilitiesRequester.requestPossibilities(cookie)
        return collegeClassRequester.bulkRequest(possibilities)
    }

    fun retrieveCurrentPossibilities(key: Int): Map<String, CollegeClass?> {
        return collegeClassRequester.bulkQuery(key)
    }
}