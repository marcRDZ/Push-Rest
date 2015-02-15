package com.rodriguezdiaz.marcos.push_rest;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.rodriguezdiaz.marcos.restwebservice.registration.Registration;
import com.rodriguezdiaz.marcos.restwebservice.registration.model.CollectionResponseRegistrationRecord;
import com.rodriguezdiaz.marcos.restwebservice.registration.model.RegistrationRecord;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends ActionBarActivity implements  GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "MainActivity";
    private static Registration regService = null;
    private GoogleApiClient gac;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check device for Play Services APK. If check succeeds, proceed with
        //  GCM registration.
        if (checkPlayServices()) {
            /*if (savedInstanceState == null) {
                getFragmentManager().beginTransaction()
                        .add(R.id.container, new PlaceholderFragment())
                        .commit();
            }*/
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
    protected void onResume() {
        super.onResume();
        new ListRegistrationAsyncTask(this).execute();
    }

    @Override
    public void onConnected(Bundle bundle) {

        Intent intent = new Intent(this, ActivityDetectionIntentService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(gac, 3000, pendingIntent);
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
    private class ListRegistrationAsyncTask extends AsyncTask<Void, Void, CollectionResponseRegistrationRecord> {

        private String idPhone;
        private Context context;
        private CollectionResponseRegistrationRecord acts;
        private ProgressDialog pd;

        private ArrayList<Map<String,String>> list = null;
        private SimpleAdapter adapter = null;
        private String[] from = { "moment", "act" };
        private int[] to = { android.R.id.text1, android.R.id.text2 };


        public ListRegistrationAsyncTask(Context context) {
            this.context = context;
            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            idPhone = telephonyManager.getDeviceId();

        }

        protected void onPreExecute(){
            super.onPreExecute();
            pd = new ProgressDialog(context);
            pd.setMessage("Retrieving activities...");
            pd.show();
        }

        @Override
        protected CollectionResponseRegistrationRecord doInBackground(Void... unused) {

            if (regService == null) {
                Registration.Builder builder = new Registration.Builder(AndroidHttp.newCompatibleTransport(),
                        new AndroidJsonFactory(), null)
                        .setRootUrl("https://fifth-moment-846.appspot.com/_ah/api/");

                regService = builder.build();

            }
            try {
                acts = regService.listActivities(idPhone).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return acts;
        }

        @Override
        protected void onPostExecute(CollectionResponseRegistrationRecord acts) {
            pd.dismiss();
            // Do something with the result.
            list = new ArrayList<>();
            List<RegistrationRecord> actList = acts.getItems();
            for (RegistrationRecord record : actList) {
                HashMap<String, String> item = new HashMap<>();
                item.put("moment", record.getMoment());
                item.put("act", record.getTypeAct());
                list.add(item);
            }
            ListView listView = (ListView) findViewById(R.id.list);
            adapter = new SimpleAdapter(MainActivity.this, list,android.R.layout.simple_list_item_2, from, to);
            listView.setAdapter(adapter);
        }
    }
    /**
     * A placeholder fragment containing a simple view.

    public static class PlaceholderFragment extends ListFragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    } */
}
