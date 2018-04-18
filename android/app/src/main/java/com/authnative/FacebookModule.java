package com.authnative;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookRequestError;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

public class FacebookModule extends ReactContextBaseJavaModule implements ActivityEventListener {
    private static final String NAME = "FacebookUtil";
    private static final String TAG = "ReactNativeJS";
    private final String LOGIN = "onLogin";
    private final String LOGOUT = "onLogout";
    private final String EVENT_KEY = "eventName";
    private final String MESSAGE_KEY = "message";
    private final String CANCEL_KEY = "isCancelled";

    private List<String> PermisionList = Arrays.asList("public_profile", "email");
    private CallbackManager callbackManager;
    private Callback tokenCallback;

    public FacebookModule(ReactApplicationContext reactContext) {
        super(reactContext);

        reactContext.addActivityEventListener(this);

        FacebookSdk.sdkInitialize(reactContext.getApplicationContext());

        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(final LoginResult loginResult) {
                        if (loginResult.getRecentlyGrantedPermissions().size() > 0) {

                            GraphRequest request = GraphRequest.newMeRequest(
                                    loginResult.getAccessToken(),
                                    new GraphRequest.GraphJSONObjectCallback() {
                                        @Override
                                        public void onCompleted(JSONObject me, GraphResponse response) {
                                            if (tokenCallback != null) {
                                                FacebookRequestError error = response.getError();

                                                if (error != null) {
                                                    WritableMap map = Arguments.createMap();
                                                    map.putString(MESSAGE_KEY, error.getErrorMessage());
                                                    onLogin(map, false);
                                                } else {
                                                    WritableMap map = Arguments.createMap();
                                                    map.putString("profile", me.toString());
                                                    onLogin(map, true);
                                                }
                                            }
                                        }
                                    });
                            Bundle parameters = new Bundle();
                            String fields = "id,name,email,first_name,last_name," +
                                    "age_range,link,picture,gender,locale,timezone," +
                                    "updated_time,verified";
                            parameters.putString("fields", fields);
                            request.setParameters(parameters);
                            request.executeAsync();
                        } else {
                            WritableMap map = Arguments.createMap();
                            map.putString(MESSAGE_KEY, "Insufficient permissions");
                            onLogin(map, false);
                        }
                    }

                    @Override
                    public void onCancel() {
                        if (tokenCallback != null) {
                            WritableMap map = Arguments.createMap();
                            map.putString(MESSAGE_KEY, "FacebookCallback onCancel event triggered");
                            map.putBoolean(CANCEL_KEY, true);
                            onLogin(map, false);
                        }
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        if (tokenCallback != null) {
                            WritableMap map = Arguments.createMap();
                            map.putString("message", exception.getMessage());
                            onLogin(map, false);
                        }
                    }
                });
    }

    private void onLogin(WritableMap map, boolean success) {
        map.putString(EVENT_KEY, LOGIN);
        if (success)
            tokenCallback.invoke(null, map);
        else
            tokenCallback.invoke(map, null);
    }

    @ReactMethod
    public void login(final Callback callback) {
        tokenCallback = callback;
        Activity currentActivity = getCurrentActivity();
        LoginManager.getInstance().logInWithReadPermissions(currentActivity, PermisionList);
    }

    @ReactMethod
    public void logout(final Callback callback) {
        tokenCallback = callback;
        LoginManager.getInstance().logOut();

        WritableMap map = Arguments.createMap();
        map.putString(EVENT_KEY, LOGOUT);
        tokenCallback.invoke(null, map);
    }

    //<editor-fold desc="ActivityEventListener">
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void onNewIntent(Intent intent) {

    }
    //</editor-fold>

    //<editor-fold desc="ReactContextBaseJavaModule">
    @Override
    public String getName() {
        return NAME;
    }
    //</editor-fold>

}
