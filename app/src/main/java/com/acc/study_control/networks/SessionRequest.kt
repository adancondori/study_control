package com.acc.study_control.networks

import android.os.AsyncTask
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONArrayRequestListener
import com.acc.study_control.models.User
import org.json.JSONArray
import org.json.JSONObject

class SessionRequest(activity: Any, type: Int, provider: String, uid: String) :
        AsyncTask<Unit, Unit, Unit>(), JSONArrayRequestListener {

    // Constants
    companion object {
        val loginRequest = 1001
        val signupRequest = 1002
    }

    // Vars
    private val requestType = type
    private val parentActivity = activity
    private val nameProvider = provider
    private val codeUid = uid

    private var email = ""
    private var name = ""
    private var phone = ""

    interface OnResponseSessionRequest {
        fun responseSessionLoginRequest(successful: Boolean, isLogin: Boolean)
        fun responseSessionSignupRequest(successful: Boolean, isRegister: Boolean)
    }

    override fun onPreExecute() {
        super.onPreExecute()
    }

    fun setSignupData(email: String, name: String, phone: String) {
        this.email = email
        this.name = name
        this.phone = phone
    }

    override fun doInBackground(vararg params: Unit?) {
        // Prepare request
        val jsonObject = JSONObject()
        val dataJson = JSONObject()
        var url = Urls.getUrlLogin()
        dataJson.put("provider", this.nameProvider)
        dataJson.put("uid", this.codeUid)

        if (requestType.equals(signupRequest)) {
            url = Urls.getUrlSignup()
            dataJson.put("email", this.email)
            dataJson.put("name", this.name)
            dataJson.put("phone", this.phone)
        }

        jsonObject.put("message", "SESSIONS_REQUEST")
        jsonObject.put("data", dataJson)

        // Make Request
        AndroidNetworking.post(url)
                .addJSONObjectBody(jsonObject)
                .setTag(this)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONArray(this)
    }

    @Suppress("UNREACHABLE_CODE")
    override fun onResponse(response: JSONArray?) {
        if (requestType.equals(loginRequest)) {
            // Get Response
            val jsonObject = response?.getJSONObject(0)
            val isRegister = if (jsonObject?.getString("status").equals("ok")) true else false
            if (isRegister) {
                // Register User
                setUser(jsonObject)
            }
            // Send Callback
            (parentActivity as OnResponseSessionRequest).responseSessionLoginRequest(true, isRegister)
        } else if (requestType.equals(signupRequest)) {
            // Get User Response
            val jsonObject = response?.getJSONObject(0)
            val isSuccessful = if (jsonObject?.getString("status").equals("ok")) true else false
            if (isSuccessful) {
                // Register User
                setUser(jsonObject)
            }
            // Send Callback
            (parentActivity as OnResponseSessionRequest).responseSessionSignupRequest(true, isSuccessful)
        }
    }

    @Suppress("UNREACHABLE_CODE")
    override fun onError(anError: ANError?) {
        if (requestType.equals(loginRequest)) {
            (parentActivity as OnResponseSessionRequest).responseSessionLoginRequest(false, false)
        } else if (requestType.equals(signupRequest)) {
            (parentActivity as OnResponseSessionRequest).responseSessionSignupRequest(false, false)
        }
    }

    private fun setUser(jsonObject: JSONObject?) {
        // Register User
        User.cleanUser()
        val dataUser = jsonObject?.getJSONObject("data")?.getJSONObject("customer")

        val user = User()
        user.email = dataUser?.getString("email")
        user.name = dataUser?.getString("name")
        user.phone = dataUser?.getString("phone")
        user.token = dataUser?.getString("token")
        user.provider = nameProvider
        user.uid = codeUid
        user.save()
    }

    override fun onPostExecute(result: Unit?) {
        super.onPostExecute(result)
    }
}