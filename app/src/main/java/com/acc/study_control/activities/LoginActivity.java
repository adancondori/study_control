package com.acc.study_control.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.acc.study_control.R;
import com.acc.study_control.models.User;
import com.acc.study_control.networks.SessionRequest;
import com.acc.study_control.utils.Utils;

import java.security.MessageDigest;
import java.util.Arrays;
import java.util.List;

public class LoginActivity extends Activity implements SessionRequest.OnResponseSessionRequest {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 123;

    class LayoutConstantKeys {
        public static final String login_layout_welcome = "login_layout_welcome";
        public static final String progress_bar_login = "progress_bar_login";
        public static final String wizard_login = "wizard_login";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Check if It is already Sign In
        if (checkUserSignIn()) redirectMain();
        printHashKey(this);
    }

    public void logInMethod(View view) {
        // Show Progress Bar
        refreshView(LayoutConstantKeys.progress_bar_login);

        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.FacebookBuilder().build());

        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setIsSmartLockEnabled(false)
                        .setAvailableProviders(providers)
                        .build(), RC_SIGN_IN);
    }

    public void signUpMethod(View view) {
        // Check for Empty Data
        if (areEmptyFields()) {
            Utils.INSTANCE.showToast(this, "Llenar todo los campos, por favor.");
            return;
        }

        // Send Data to Server
        AppCompatEditText textProvider = findViewById(R.id.wizard_provider);
        AppCompatEditText textUid = findViewById(R.id.wizard_uid);
        SessionRequest sessionRequest = new SessionRequest(
                this, SessionRequest.Companion.getSignupRequest(),
                textProvider.getText().toString(), textUid.getText().toString());

        AppCompatEditText editTextName = findViewById(R.id.wizard_name);
        AppCompatEditText editTextEmail = findViewById(R.id.wizard_email);
        AppCompatEditText editTextPhone = findViewById(R.id.wizard_phone);
        sessionRequest.setSignupData(
                editTextEmail.getText().toString(),
                editTextName.getText().toString(),
                editTextPhone.getText().toString());

        sessionRequest.execute();
        refreshView(LayoutConstantKeys.progress_bar_login);
    }

    private boolean areEmptyFields() {
        AppCompatEditText editTextName = findViewById(R.id.wizard_name);
        AppCompatEditText editTextEmail = findViewById(R.id.wizard_email);
        AppCompatEditText editTextPhone = findViewById(R.id.wizard_phone);
        if (editTextName.getText().toString().isEmpty()) return true;
        if (editTextEmail.getText().toString().isEmpty()) return true;
        if (editTextPhone.getText().toString().isEmpty()) return true;
        return false;
    }

    @Override
    public void responseSessionSignupRequest(boolean successful, boolean isRegister) {
        if (successful) {
            // Successful request
            if (isRegister) {
                // Successful Signup
                redirectMain();
            } else {
                // Error on Server
                refreshView(LayoutConstantKeys.wizard_login);
                Utils.INSTANCE.showToast(this, "Error en el servidor. Trate más tarde.");
            }
        } else {
            // Error on Request
            refreshView(LayoutConstantKeys.wizard_login);
            Utils.INSTANCE.showToast(this, "Error en la red. Intente más tarde.");
        }
    }

    @Override
    public void responseSessionLoginRequest(boolean successful, boolean isLogin) {
        if (successful) {
            // Successful Request
            if (isLogin) {
                // Successful Login
                redirectMain();
            } else {
                // Register User
                refreshView(LayoutConstantKeys.wizard_login);
            }
        } else {
            // Error On Request
            refreshView(LayoutConstantKeys.login_layout_welcome);
            Utils.INSTANCE.showToast(this, "Error en la red. Intente más tarde.");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    // Get Info for Provider
                    // First Provider: Firebase
                    // Second Provider: Facebook, Google & Phone
                    String providerId = "", uid = "";
                    User user1 = null;
                    for (UserInfo userInfo : user.getProviderData()) {
                        // Id of the provider (ex: google.com)
                        providerId = userInfo.getProviderId();

                        // UID specific to the provider
                        if (providerId.equals("phone"))
                            uid = userInfo.getPhoneNumber();
                        else
                            uid = userInfo.getUid();

                        // Name, email address, and profile photo Url
                        // String name = userInfo.getDisplayName();
                        // String email = userInfo.getEmail();
                        user1 = new User();
                        setValuesOnWizard(
                                userInfo.getDisplayName(), userInfo.getEmail(),
                                userInfo.getPhoneNumber(), providerId, uid
                        );
                        user1.email = userInfo.getEmail();
                        user1.name = userInfo.getDisplayName();
                        user1.phone = userInfo.getPhoneNumber();
                        user1.provider = providerId;
                        user1.uid = uid;
                    }

                    // Still Waiting and Process User
                    refreshView(LayoutConstantKeys.progress_bar_login);
                    if (user1 != null) {
                        user1.save();
                    }
                    processUser(providerId, uid);
                } else {
                    Log.e(TAG, "Error on Firebase User");
                    refreshView(LayoutConstantKeys.login_layout_welcome);
                    Utils.INSTANCE.showToast(this, "Error en Autenticación. Intente de nuevo.");
                }
            } else {
                //TODO: Sign in failed, check response for error code
                Log.e(TAG, "Error on Sign In");
                refreshView(LayoutConstantKeys.login_layout_welcome);
                Utils.INSTANCE.showToast(this, "Error en Autenticación. Intente de nuevo.");
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void processUser(String providerId, String uid) {
        // Prepare Request for Login
        // SessionRequest sessionRequest = new SessionRequest(this, SessionRequest.Companion.getLoginRequest(), providerId, uid);
        // sessionRequest.execute();
        responseSessionLoginRequest(true, true);
    }

    private void setUser() {
        AppCompatEditText editTextName = findViewById(R.id.wizard_name);
        AppCompatEditText editTextEmail = findViewById(R.id.wizard_email);
        AppCompatEditText editTextPhone = findViewById(R.id.wizard_phone);
        AppCompatEditText editTextProvider = findViewById(R.id.wizard_provider);
        AppCompatEditText editTextUid = findViewById(R.id.wizard_uid);
        // Register User
        User.cleanUser();
        //val dataUser = jsonObject?.getJSONObject("data")?.getJSONObject("customer")

        User user = new User();
        user.email = editTextEmail.getText().toString();
        user.name = editTextName.getText().toString();
        user.phone = editTextPhone.getText().toString();
        user.token = editTextProvider.getText().toString();
        user.provider = editTextUid.getText().toString();
        user.uid = "";
        user.save();
    }

    private void setValuesOnWizard(String name, String email, String phone, String provider, String uid) {
        AppCompatEditText editTextName = findViewById(R.id.wizard_name);
        AppCompatEditText editTextEmail = findViewById(R.id.wizard_email);
        AppCompatEditText editTextPhone = findViewById(R.id.wizard_phone);
        AppCompatEditText editTextProvider = findViewById(R.id.wizard_provider);
        AppCompatEditText editTextUid = findViewById(R.id.wizard_uid);
        editTextName.setText(name);
        editTextEmail.setText(email);
        editTextPhone.setText(phone);
        editTextProvider.setText(provider);
        editTextUid.setText(uid);
    }

    private boolean checkUserSignIn() {
        // Check on Table User
        User user = User.findUser();
        return user != null;
    }

    private void redirectMain() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void refreshView(String layoutKey) {
        RelativeLayout progressBarLayout = findViewById(R.id.progress_bar_login);
        LinearLayout loginLayout = findViewById(R.id.login_layout_welcome);
        LinearLayout wizardLayout = findViewById(R.id.wizard_login);

        switch (layoutKey) {
            case LayoutConstantKeys.login_layout_welcome:
                loginLayout.setVisibility(View.VISIBLE);
                progressBarLayout.setVisibility(View.INVISIBLE);
                wizardLayout.setVisibility(View.INVISIBLE);
                break;
            case LayoutConstantKeys.progress_bar_login:
                loginLayout.setVisibility(View.INVISIBLE);
                progressBarLayout.setVisibility(View.VISIBLE);
                wizardLayout.setVisibility(View.INVISIBLE);
                break;
            case LayoutConstantKeys.wizard_login:
                loginLayout.setVisibility(View.INVISIBLE);
                progressBarLayout.setVisibility(View.INVISIBLE);
                wizardLayout.setVisibility(View.VISIBLE);
                break;
        }
    }

    public static void printHashKey(Context context) {
        try {
            final PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            for (android.content.pm.Signature signature : info.signatures) {
                final MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                final String hashKey = new String(Base64.encode(md.digest(), 0));
                Log.i("AppLog", "key:" + hashKey + "=");
            }
        } catch (Exception e) {
            Log.e("AppLog", "error:", e);
        }
    }
}
