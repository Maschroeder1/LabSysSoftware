package infra

import model.ClassCode
import model.Cookie
import model.PossibilitiesRequester

class HttpRequestPossibilitiesRequester : PossibilitiesRequester {
    private val wat  = 1

    override fun requestPossibilities(cookie: Cookie): List<ClassCode> {
        return listOf<ClassCode>()
    }
}