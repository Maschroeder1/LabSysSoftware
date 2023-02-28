package model

interface PossibilitiesRequester {

    fun requestPossibilities(cookie: Cookie): List<ClassCode>
}