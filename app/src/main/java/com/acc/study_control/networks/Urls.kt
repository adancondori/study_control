package com.acc.study_control.networks

object Urls {
    // Domains
    private const val URL_ROOT = "http://"
    private const val COMPANY = "libertad"
//    private const val DOMAIN = ".atymovil.net/"
    private const val DOMAIN = ".trackdio.me:3000/"
    private const val API_URL = "api/v1/"

    // Urls
    fun getUrlLogin() =
            URL_ROOT + COMPANY + DOMAIN + API_URL + "customers/login"

    fun getUrlSignup() =
            URL_ROOT + COMPANY + DOMAIN + API_URL + "customers/signup"
}