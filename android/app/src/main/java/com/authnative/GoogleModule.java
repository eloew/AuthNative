package com.authnative;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.BaseActivityEventListener;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;


public class GoogleModule extends ReactContextBaseJavaModule {
    private static final String NAME = "GoogleUtil";
    private static final String TAG = "ReactNativeJS";

    private final String LOGIN = "onLogin";
    private final String VALIDATE = "onValidate";
    private final String LOGOUT = "onLogout";
    private final String ERROR = "onError";
    private final String EVENT_KEY = "eventName";
    private final String ERROR_KEY = "error";
    private final String CANCEL_KEY = "isCancelled";


    public static final int RequestCode_SignIn = 999;

    private GoogleApiClient googleApiClient;
    private Callback tokenCallback;

    public GoogleModule(ReactApplicationContext reactContext) {
        super(reactContext);

        reactContext.addActivityEventListener(new ActivityEventListener());

    }

    @ReactMethod
    public void setup(final Promise promise) {
        final Activity activity = getCurrentActivity();

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .build();

                googleApiClient = new GoogleApiClient.Builder(activity.getBaseContext())
                        .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                        .build();
                googleApiClient.connect();
                promise.resolve(true);
            }
        });
    }



    @ReactMethod
    public void login(final Callback callback) {
        tokenCallback = callback;
        if (googleApiClient == null) {
            onError(-1, "googleApiClient is null");
            return;
        }

        final Activity activity = getCurrentActivity();

        if (activity == null) {
            onError(-1, "No activity");
            return;
        }

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                activity.startActivityForResult(signInIntent, RequestCode_SignIn);
            }
        });

    }


    private void handleSignInResult(GoogleSignInResult result, Boolean isSilent) {
        WritableMap map = Arguments.createMap();
        WritableArray scopes = Arguments.createArray();

        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();
            Uri photoUrl = acct.getPhotoUrl();

            for(Scope scope : acct.getGrantedScopes()) {
                String scopeString = scope.toString();
                if (scopeString.startsWith("http")) {
                    scopes.pushString(scopeString);
                }
            }

            map.putString("id", acct.getId());
            map.putString("name", acct.getDisplayName());
            map.putString("email", acct.getEmail());
            map.putString("photo", photoUrl != null ? photoUrl.toString() : null);
            map.putString("idToken", acct.getIdToken());
            map.putString("serverAuthCode", acct.getServerAuthCode());
            map.putArray("scopes", scopes);

            onLogin(map);
        } else {
            int code = result.getStatus().getStatusCode();
            String error = GoogleSignInStatusCodes.getStatusCodeString(code);
            Log.d(TAG, "handleSignInResult error: " + code + " - " + error);

            onError(code, error);
        }
    }

    private void onError(int code, String error) {
        WritableMap map = Arguments.createMap();
        map.putString(EVENT_KEY, ERROR);
        map.putString("error", error);
        map.putString(ERROR_KEY, error);
        tokenCallback.invoke(map, null);
    }

    private void onLogin(WritableMap map) {
        map.putString(EVENT_KEY, LOGIN);
        tokenCallback.invoke(null, map);
    }

    private void onLogout() {
        WritableMap map = Arguments.createMap();
        map.putString(EVENT_KEY, LOGOUT);
        tokenCallback.invoke(null, map);
    }


    @ReactMethod
    public void logout(final Callback callback) {
        tokenCallback = callback;
        if (googleApiClient == null) {
            onError(-1, "googleApiClient is null");
            return;
        }

        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
                if (status.isSuccess()) {
                    onLogout();
                } else {
                    int code = status.getStatusCode();
                    String error = GoogleSignInStatusCodes.getStatusCodeString(code);
                    onError(code, error);
                }
            }
        });

    }



    private class ActivityEventListener extends BaseActivityEventListener {
        @Override
        public void onActivityResult(Activity activity, final int requestCode, final int resultCode, final Intent intent) {
            if (requestCode == RequestCode_SignIn) {
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(intent);
                handleSignInResult(result, false);
            }
        }
    }


    //<editor-fold desc="ReactContextBaseJavaModule">
    @Override
    public String getName() {
        return NAME;
    }
    //</editor-fold>
}
