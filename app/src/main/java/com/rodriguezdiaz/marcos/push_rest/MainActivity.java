package com.rodriguezdiaz.marcos.push_rest;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;


public class MainActivity extends ActionBarActivity implements  GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "MainActivity";

    private GoogleApiClient gac;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check device for Play Services APK. If check succeeds, proceed with
        //  GCM registration.
        if (checkPlayServices()) {
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container, new PlaceholderFragment())
                        .commit();
            }
            context = getApplicationContext();
            gac = new GoogleApiClient.Builder(context)
                    .addApi(ActivityRecognition.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
            gac.connect();

        } else {
            Log.i(TAG, "No valid Google Play Services APK found.");
        }
    }

    @Override
    public void onConnected(Bundle bundle) {

        Intent intent = new Intent(this, ActivityDetectionIntentService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(gac, 0, pendingIntent);
    }

    @Override
    public void onConnectionSuspended(int i) {

        Log.i(TAG, "Connection to GoogleApiClient has been suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        Log.i(TAG, "Connection to GoogleApiClient has been failed");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }
 /*   private class GcmRegistrationAsyncTask extends AsyncTask<String, Void, String> {
        private GoogleCloudMessaging gcm;
        private Context context;

        public GcmRegistrationAsyncTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... params) {
            if (regService == null) {
                Registration.Builder builder = new Registration.Builder(AndroidHttp.newCompatibleTransport(),
                        new AndroidJsonFactory(), null)
                        // Need setRootUrl and setGoogleClientRequestInitializer only for local testing,
                        // otherwise they can be skipped
                        .setRootUrl("https://fifth-moment-846.appspot.com/_ah/api/");

                regService = builder.build();
            }
            String msg = "";
            try {
                if (gcm == null) {
                    gcm = GoogleCloudMessaging.getInstance(context);
                }
                //Check if regId is stored in SharedPreferences
                String regId = getRegistrationId(context);
                if(regId.isEmpty()){

                    regId = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + regId;
                    //Now the app gets the Vendor Device Id
                    TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                    String idPhone = telephonyManager.getDeviceId();
                    //Here the app register this device and its registrationId from GCM to RegistrationEndpoint
                    regService.register(regId, idPhone).execute();
                    storeRegistrationDeviceId(context, regId);

                }//Here the app send the DetectedActivity to RegistrationEndpoint
                else {
                    regService.record(regId, params[0], params[1]);
                }

            } catch (IOException ex) {
                ex.printStackTrace();
                msg = "Error: " + ex.getMessage();
            }
            return msg;
        }

        @Override
        protected void onPostExecute(String msg) {
            if(!msg.isEmpty()) {
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                Logger.getLogger("REGISTRATION").log(Level.INFO, msg);
            }
        }
    }
 private String registerDetectedActivity(Context context, String time, String act) {


     if (regService == null)

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
             gcm = GoogleCloudMessaging.getInstance(context);
         }
         //Check if regId is stored in SharedPreferences
         String regId = getRegistrationId(context);
         if (regId.isEmpty()) {

             regId = gcm.register(SENDER_ID);
             msg = "Device registered, registration ID=" + regId;
             //Now the app gets the Vendor Device Id
             TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
             String idPhone = telephonyManager.getDeviceId();
             //Here the app register this device and its registrationId from GCM to RegistrationEndpoint
             regService.register(regId, idPhone).execute();
             storeRegistrationDeviceId(context, regId);

         }//Here the app send the DetectedActivity to RegistrationEndpoint

         regService.record(regId, time, act);


     } catch (IOException ex) {
         ex.printStackTrace();
         msg = "Error: " + ex.getMessage();
     }
     return msg;
 }*/

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }
}
