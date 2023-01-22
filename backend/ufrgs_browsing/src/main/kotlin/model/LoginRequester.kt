package model

interface LoginRequester {

    fun requestLogin(credentials: Login): LoginRequestResponse
}