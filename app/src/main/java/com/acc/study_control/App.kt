package com.acc.study_control

import android.app.Application
import android.content.Context
import android.support.multidex.MultiDex
import com.androidnetworking.AndroidNetworking
import com.jacksonandroidnetworking.JacksonParserFactory
import com.orm.SugarContext

//keytool -exportcert -alias androiddebugkey -keystore "C:\Users\USERNAME\.android\debug.keystore" | "PATH_TO_OPENSSL_LIBRARY\bin\openssl" sha1 -binary | "PATH_TO_OPENSSL_LIBRARY\bin\openssl" base64

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialize Libraries
        SugarContext.init(this)
        AndroidNetworking.initialize(this)
        AndroidNetworking.setParserFactory(JacksonParserFactory())
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)

        // Init MultiDex
        MultiDex.install(this)
    }

    override fun onTerminate() {
        // Terminate Libraries
        SugarContext.terminate()

        super.onTerminate()
    }
}