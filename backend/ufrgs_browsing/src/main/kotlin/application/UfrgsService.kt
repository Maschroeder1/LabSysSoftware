package application

import model.*

open class UfrgsService(
    private val loginRequester: LoginRequester,
    private val possibilitiesRequester: PossibilitiesRequester,
    private val collegeClassRequester: CollegeClassRequester,
    private val enrollmentDeclarationRequester: EnrollmentDeclarationRequester
) {

    open fun requestLogin(credentials: Login): LoginRequestResponse {
        return loginRequester.requestLogin(credentials)
    }

    open fun startPossibilitiesProcessing(cookie: Cookie): Int {
        val possibilities = possibilitiesRequester.requestPossibilities(cookie)
        return collegeClassRequester.bulkRequest(possibilities)
    }

    open fun retrieveCurrentPossibilities(key: Int): Map<String, CollegeClass?> {
        return collegeClassRequester.bulkQuery(key)
    }

    open fun retrieveEnrollmentDeclaration(cookie: Cookie): String {
        return enrollmentDeclarationRequester.requestEnrollmentDeclaration(cookie)
    }
}