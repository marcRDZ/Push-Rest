package com.rodriguezdiaz.marcos.push_rest;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.rodriguezdiaz.marcos.restwebservice.registration.Registration;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;

/**
 * Created by Ras-Mars on 05/02/2015.
 */
public class ActivityDetectionIntentService extends IntentService {

    private static final String PROPERTY_REG_ID = "registration_id";
    private static final String SENDER_ID = "412331380840";
    private static final String TAG = "ActDetectService";
    private String idPhone;
    private static Registration regService = null;
    private GoogleCloudMessaging gcm;
    private static int lastAct = 0;


    public ActivityDetectionIntentService() {
        super("ActivityDetectionIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        String msg = "";
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        idPhone = telephonyManager.getDeviceId();

        if (ActivityRecognitionResult.hasResult(intent)) {
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            DetectedActivity mostProbAct = result.getMostProbableActivity();
            if (mostProbAct.getType() != lastAct){
                msg = registerDetectedActivity(DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime())
                        , getFriendlyName(mostProbAct.getType()));
                lastAct = mostProbAct.getType();
            }

            if (!msg.isEmpty()) showToast(msg);

    }
    }
    protected void showToast(final String message) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
        });
    }
    private static String getFriendlyName(int typeACt) {
        switch (typeACt) {
            case DetectedActivity.IN_VEHICLE:
                return "in vehicle";
            case DetectedActivity.ON_BICYCLE:
                return "on bike";
            case DetectedActivity.ON_FOOT:
                return "on foot";
            case DetectedActivity.TILTING:
                return "tilting";
            case DetectedActivity.STILL:
                return "still";
            default:
                return "unknown";
        }
    }
    private String registerDetectedActivity(String time, String act) {


        if(regService==null)

        {
            Registration.Builder builder = new Registration.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(), null)
                    // Need setRootUrl and setGoogleClientRequestInitializer only for local testing,
                    // otherwise they can be skipped
                    .setRootUrl("https://fifth-moment-846.appspot.com/_ah/api/");

            regService = builder.build();
        }

        String msg = "";
        try

        {
            if (gcm == null) {
                gcm = GoogleCloudMessaging.getInstance( getApplicationContext());
            }
            //Check if regId is stored in SharedPreferences
            String regId = getRegistrationId( getApplicationContext());
            if (regId.isEmpty()) {

                regId = gcm.register(SENDER_ID);
                msg = "Device registered, registration ID=" + regId;
                //Now the app gets the Vendor Device Id
                //Here the app register this device and its registrationId from GCM to RegistrationEndpoint
                regService.register(regId).execute();
                storeRegistrationDeviceId( getApplicationContext(), regId);

            }//Here the app send the DetectedActivity to RegistrationEndpoint
            regService.record(regId, idPhone, time, act).execute();

        }

        catch(IOException ex){
            ex.printStackTrace();
            msg = "Error: " + ex.getMessage();
        }
        return msg;
    }
    /**
     * Gets the current registration ID for application on GCM service.
     * <p>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     *         registration ID.
     */
    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i("Registration not found.", TAG);
            return "";
        }

        return registrationId;
    }
    /**
     * Stores the registration ID in the application's
     * {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId registration ID
     */
    private void storeRegistrationDeviceId(Context context, String regId) {

        final SharedPreferences prefs = getGCMPreferences(context);

        Log.i(TAG, "Saving regId on preferences");
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.apply();
    }
    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGCMPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return context.getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
    }

}
