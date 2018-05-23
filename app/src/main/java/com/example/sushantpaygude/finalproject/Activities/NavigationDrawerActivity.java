package com.example.sushantpaygude.finalproject.Activities;

import android.Manifest;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sushantpaygude.finalproject.Fragments.MainFragment;
import com.example.sushantpaygude.finalproject.POJOs.ServerPojo.ServerRegistration;
import com.example.sushantpaygude.finalproject.R;
import com.example.sushantpaygude.finalproject.Utils.TinyDB;
import com.example.sushantpaygude.finalproject.Utils.Utilities;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class NavigationDrawerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private View navigationHeader;
    private GoogleSignInClient googleSignInClient;

    private LocationService my_loc_service;
    Boolean mBound = false;
    private double[] mylocation = new double[2];
    private ServerRegistration serverRegistration;
    TinyDB tinyDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setDisplayShowTitleEnabled(false);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        toolbar.setTitle("");
        toolbar.setSubtitle("");
        navigationView.setNavigationItemSelectedListener(this);
        navigationHeader = navigationView.inflateHeaderView(R.layout.navigation_header);
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new MainFragment()).commit();
        tinyDB = new TinyDB(getApplicationContext());

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            GoogleSignInAccount googleSignInAccount = extras.getParcelable(Utilities.GOOGLESIGNINACCOUNT);
            if (googleSignInAccount != null) {
                inflateNavigationHeader(navigationHeader, googleSignInAccount, null);
            }

            serverRegistration = (ServerRegistration) extras.getSerializable(Utilities.SERVERSIGNINACCOUNT);

            if (serverRegistration != null) {
                inflateNavigationHeader(navigationHeader, null, serverRegistration);


            }
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        FirebaseMessaging.getInstance().subscribeToTopic("test");
        FirebaseInstanceId.getInstance().getToken();

        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        new UploadLocAsync().execute();
                    }
                });
            }
        };
        timer.schedule(task, 0, 5 * 60 * 1000); //This executes the UploadLocAsync task every 5 minutes
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.nav_logout:
                signOut();
                break;
            case R.id.nav_todo:
                Intent intent = new Intent(this, ToDoActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_account:
                Intent userProfileIntent = new Intent(this, UserProfileActivity.class);
                if (serverRegistration != null) {
                    userProfileIntent.putExtra("UserProfile", serverRegistration);

                }
                startActivity(userProfileIntent);
                break;
            case R.id.nav_notification:
                break;
        }
        return false;
    }

    private void inflateNavigationHeader(View navigationHeader, GoogleSignInAccount googleSignInAccount, ServerRegistration serverRegistration) {
        TextView textView = navigationHeader.findViewById(R.id.textUsername);
        ImageView imageView = navigationHeader.findViewById(R.id.imageUser);
        if (googleSignInAccount != null) {

            textView.setText(googleSignInAccount.getDisplayName());
            Picasso.get().load(googleSignInAccount.getPhotoUrl()).into(imageView);
        } else if (serverRegistration != null) {
            textView.setText(serverRegistration.getUsers().getUser());
        }


    }

    private void signOut() {
        googleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                        startActivity(intent);
                        // ...
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent loc_intent = new Intent(getApplicationContext(), LocationService.class);
        bindService(loc_intent, loc_service_conn, BIND_AUTO_CREATE);

//        final Handler handler = new Handler();
//        Timer timer = new Timer();
//        TimerTask task = new TimerTask() {
//            @Override
//            public void run() {
//                handler.post(new Runnable() {
//                    public void run() {
//                        new UploadLocAsync().execute();
//                    }
//                });
//            }
//        };
//        timer.schedule(task, 0, 5*60*1000); //This executes the UploadLocAsync task every 5 minutes
    }

    @Override
    protected void onResume() {
        super.onResume();
        mBound = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(loc_service_conn);
        mBound = false;
    }

    ServiceConnection loc_service_conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            my_loc_service = ((LocationService.MyBinder) iBinder).getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBound = false;
        }
    };

    private class UploadLocAsync extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                JSONObject jsonObj = new JSONObject(s);

                JSONArray places = jsonObj.getJSONArray("results");
                Log.d("Places", "list" + s);

                if (places.length() == 0) {

                } else {
                    JSONObject my_place = places.getJSONObject(0);
                    String place_name = my_place.getString("name");
                    String place_address = my_place.getString("formatted_address");

                    new GetNotifyAsync().execute(place_name, place_address);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

        @Override
        protected String doInBackground(String... params) {

            if (mBound) {
                if (my_loc_service != null) {
                    mylocation = my_loc_service.getlocation();
                }
            }
            String type = "";
            String query = "";
            ArrayList<String> todoArrayList = tinyDB.getListString(Utilities.TO_DO_LIST_STRING);
//            String todoJson = new Gson().toJson(todoArrayList);
            if (todoArrayList.size() == 0) {
                type = "store";
                query = "pen";
            } else {
                type = "restaurant";
                query += todoArrayList.get(0);
            }
            String api_url = getResources().getString(R.string.google_api);
            String parameters = "";
            parameters += "location=" + mylocation[0] + "," + mylocation[1] +
                    "&radius=1500&type=" + type + "&query=" + query + "&key=" + getResources().getString(R.string.key);
            StringBuilder url_builder = new StringBuilder();
            url_builder.append(api_url);
            url_builder.append(parameters);
            String login_url = url_builder.toString();
            HttpURLConnection conn = null;
            String response = "";

            try {
                URL url = new URL(login_url);
                conn = (HttpURLConnection) url.openConnection();

//                conn.setRequestMethod("POST");
//                conn.setDoOutput(true);

//                String urlparameters = "";
//                Timestamp time = new Timestamp(System.currentTimeMillis());
//                double timestamp = time.getTime()/(60*60*1000);
//                urlparameters += "Username=" + params[0] + "&Latitude=" + mylocation[0] + "&Longitude=" + mylocation[1]+ "&Timestamp=" + timestamp;
//
//
//                OutputStream outputpost = new BufferedOutputStream(conn.getOutputStream());
//                outputpost.write(urlparameters.getBytes());
//                outputpost.flush();
//                outputpost.close();

                int responseCode = conn.getResponseCode();
                String line = "";
                StringBuilder builder = new StringBuilder();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader b_reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    while ((line = b_reader.readLine()) != null) {
                        builder.append(line);
                    }
                    b_reader.close();

                    response = builder.toString();
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
            return response;
        }
    }

    private class GetNotifyAsync extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.equals("Success")) {
                Toast toast = Toast.makeText(getApplicationContext(), "Notification Sent Successfully!", Toast.LENGTH_SHORT);
                toast.show();
            } else {
//                Toast toast = Toast.makeText(getApplicationContext(), "Notification Sending Failed!", Toast.LENGTH_SHORT);
//                toast.show();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            String ip = getResources().getString(R.string.ip_address);
            String page = "mc_PushNotifications.php";
            StringBuilder url_builder = new StringBuilder();
            url_builder.append(ip);
            url_builder.append(page);
            String login_url = url_builder.toString();
            HttpURLConnection conn = null;
            String response = "";

            try {
                URL url = new URL(login_url);
                conn = (HttpURLConnection) url.openConnection();

                conn.setRequestMethod("POST");
                conn.setDoOutput(true);

                String urlparameters = "";
//                Timestamp time = new Timestamp(System.currentTimeMillis());
//                double timestamp = time.getTime()/(60*60*1000);
                String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date());
                urlparameters += "Name=" + params[0] + "&Address=" + params[1] + "&Time=" + timeStamp;

                OutputStream outputpost = new BufferedOutputStream(conn.getOutputStream());
                outputpost.write(urlparameters.getBytes());
                outputpost.flush();
                outputpost.close();

                int responseCode = conn.getResponseCode();
                String line = "";
                StringBuilder builder = new StringBuilder();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader b_reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    while ((line = b_reader.readLine()) != null) {
                        builder.append(line);
                    }
                    b_reader.close();
                    JSONObject jsonObject = new JSONObject(builder.toString());
                    Log.i("Notification", "Response" + jsonObject);
                    int result = Integer.parseInt(jsonObject.getString("success"));
                    if (result > 0) {
                        response = "Success";
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
            return response;
        }
    }

    /*
     * Code For Prompting User For Location Permission if it is not already given
     * */
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Permission please")
                        .setMessage("Please")
                        .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(NavigationDrawerActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        Log.i("Callback", "okay....");
    }
}
