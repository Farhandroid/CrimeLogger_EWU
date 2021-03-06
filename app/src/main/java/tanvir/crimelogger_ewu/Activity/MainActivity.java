package tanvir.crimelogger_ewu.Activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.bumptech.glide.Glide;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.gson.Gson;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.sdsmdg.tastytoast.TastyToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import tanvir.crimelogger_ewu.HelperClass.AppController;
import tanvir.crimelogger_ewu.HelperClass.ProgressDialog;
import tanvir.crimelogger_ewu.HelperClass.RecyclerAdapter;
import tanvir.crimelogger_ewu.MOdelClass.UserPostMC;
import tanvir.crimelogger_ewu.R;

public class MainActivity extends AppCompatActivity {

    private FloatingActionMenu floatingActionMenu;
    private FloatingActionButton createAwarenessFAB, policePhoneNumberFAB, viewProfileFAB;
    ProgressDialog progressDialog;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private android.support.v7.widget.Toolbar toolbar;
    private String isLogged = "";
    private int volumeKeyPressed = 0;
    ImageView noInternetImageView;
    private MaterialSearchView searchView;
    private SharedPreferences prefs;
    String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};

    ArrayList<UserPostMC> userPostMCS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        floatingActionMenu = findViewById(R.id.menu);
        progressDialog = new ProgressDialog(MainActivity.this);
        createAwarenessFAB = findViewById(R.id.createAwareness);
        policePhoneNumberFAB = findViewById(R.id.policePhoneNUmber);
        viewProfileFAB = findViewById(R.id.view_profile);
        recyclerView = findViewById(R.id.recyclerView);
        boolean enter = true;
        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                if (key.equals("push_data") || key.equals("push_image_url")) {
                    enter = false;
                    Intent myIntent = new Intent(getApplicationContext(), PushNotification.class);
                    myIntent.putExtra("cameFromWhichActivity", "push_mainActivity");
                    myIntent.putExtra("push_data", getIntent().getExtras().getString("push_data"));
                    myIntent.putExtra("push_image_url", getIntent().getExtras().getString("push_image_url"));
                    myIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    this.startActivity(myIntent);
                    finish();
                }
            }
            if (enter) {
                initializeMainActivity();
            }
        } else {
            initializeMainActivity();
        }
    }

    public void initializeMainActivity() {
        userPostMCS = new ArrayList<>();
        checkPermissions();
        toolbar = findViewById(R.id.toolbarlayoutinmainactivity);
        setSupportActionBar(toolbar);
        searchView = findViewById(R.id.search_view);
        noInternetImageView = findViewById(R.id.no_internet_image);
        checkLoginSharedPrefference();
        checkOnLineOrNOt();
    }

    public void startViewProfileActivity(View view) {
        floatingActionMenu.close(true);
        if (isLogged != null) {
            if (isLogged.contains("yes")) {
                startUserProfileActivity();
            } else startLoginActivity();
        } else startLoginActivity();
    }

    public void startViewPolicePhoneNoActivity(View view) {
        floatingActionMenu.close(true);
        Intent myIntent = new Intent(getApplicationContext(), PoliceNumberActivity.class);
        myIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        this.startActivity(myIntent);
        overridePendingTransition(R.anim.left_in, R.anim.left_out);
        finish();
    }

    public void startCreateAwareNessActivity(View view) {
        floatingActionMenu.close(true);
        final AlertDialog alertDialog;
        final View imageDeleteDialogView;
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = (LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageDeleteDialogView = inflater.inflate(R.layout.create_awareness_as, null);
        dialogBuilder.setView(imageDeleteDialogView);
        alertDialog = dialogBuilder.create();
        alertDialog.show();
        Button anonymousBTN = imageDeleteDialogView.findViewById(R.id.anonymousBTN);
        Button loginBTN = imageDeleteDialogView.findViewById(R.id.loginBTN);
        anonymousBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                Intent myIntent = new Intent(getApplicationContext(), UserCreatePost.class);
                myIntent.putExtra("userType", "anonymous");
                myIntent.putExtra("userName", "");
                MainActivity.this.startActivity(myIntent);
                overridePendingTransition(R.anim.left_in, R.anim.left_out);
                finish();
            }
        });

        loginBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                checkLoginSharedPrefferenceForCreateAwareness();
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_items, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                floatingActionMenu.close(true);
                ArrayList<UserPostMC> userPostMCS1 = new ArrayList<>();
                for (int i = 0; i < userPostMCS.size(); i++) {
                    if (userPostMCS.get(i).getCrimePlace().toLowerCase().contains(newText.toLowerCase())) {
                        userPostMCS1.add(userPostMCS.get(i));
                    }
                }
                updateRecyclerView(userPostMCS1);
                return false;
            }
        });
        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {

                floatingActionMenu.close(true);
                floatingActionMenu.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onSearchViewClosed() {
                if (isOnline()) {
                    floatingActionMenu.setVisibility(View.VISIBLE);
                    updateRecyclerView(userPostMCS);
                } else {

                }
            }
        });
        return true;
    }

    public void updateRecyclerView(ArrayList<UserPostMC> userPostMCS) {
        adapter = new RecyclerAdapter(MainActivity.this, userPostMCS, "MainActivity");
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }

    public void RetriveDataFromServer() {
        progressDialog.showProgressDialog();
        String url = "http://www.farhandroid.com/CrimeLogger/Script/retriveUserPostFromDatabase.php";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if (response.length() == 0) {
                    MainActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            progressDialog.hideProgressDialog();
                        }
                    });
                    SharedPreferences settings = MainActivity.this.getSharedPreferences("PostData", Context.MODE_PRIVATE);
                    settings.edit().clear().commit();
                }
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject postInfo = response.getJSONObject(i);
                        UserPostMC userPostMC = new UserPostMC(postInfo.getString("userName"), postInfo.getString("crimePlace"), postInfo.getString("crimeDate"), postInfo.getString("crimeTime"), postInfo.getString("crimeType"), postInfo.getString("crimeDesc"), postInfo.getString("postDateAndTime"), postInfo.getString("howManyImage"), postInfo.getString("howManyReport"));
                        userPostMCS.add(userPostMC);
                        if (i + 1 == response.length()) {
                            progressDialog.hideProgressDialog();
                            setSharedPrefference();
                            updateRecyclerView(userPostMCS);
                            adapter.notifyDataSetChanged();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressDialog.hideProgressDialog();
                        Toast.makeText(MainActivity.this, "Json Exception " + e.toString(), Toast.LENGTH_SHORT).show();
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(final VolleyError error) {
                progressDialog.hideProgressDialog();
                Toast.makeText(MainActivity.this, "Volley Error : " + error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(40000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonArrayRequest);
    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }

    private boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 100);
            return false;
        }
        return true;
    }

    public void setSharedPrefference() {
        SharedPreferences.Editor editor = getSharedPreferences("PostData", MODE_PRIVATE).edit();
        Gson gson = new Gson();
        ArrayList<UserPostMC> userPostMCSP = new ArrayList<>();
        userPostMCSP.addAll(userPostMCS);
        String jsonText = gson.toJson(userPostMCSP);
        editor.putString("PostData", jsonText);
        editor.commit();
    }

    public void retrivesharedpreference() {
        SharedPreferences prefs = getSharedPreferences("PostData", MODE_PRIVATE);
        Gson gson = new Gson();
        String jsonText = prefs.getString("PostData", null);
        if (jsonText != null) {
            UserPostMC[] text = gson.fromJson(jsonText, UserPostMC[].class);
            userPostMCS = new ArrayList<>(Arrays.asList(text));
            updateRecyclerView(userPostMCS);
        }
    }

    public void startLoginActivity() {
        Intent myIntent = new Intent(getApplicationContext(), UserLogin.class);
        myIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        this.startActivity(myIntent);
        overridePendingTransition(R.anim.left_in, R.anim.left_out);
        finish();
    }

    public void checkLoginSharedPrefferenceForCreateAwareness() {
        if (isLogged != null) {
            if (isLogged.contains("yes")) {
                String userName = prefs.getString("userName", null);
                Intent myIntent = new Intent(getApplicationContext(), UserCreatePost.class);
                myIntent.putExtra("userType", "user");
                myIntent.putExtra("userName", userName);
                this.startActivity(myIntent);
                overridePendingTransition(R.anim.left_in, R.anim.left_out);
                finish();
            } else {
                Intent myIntent = new Intent(getApplicationContext(), UserLogin.class);
                myIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                myIntent.putExtra("cameFromWhichActivity", "MainActivity");
                myIntent.putExtra("userType", "user");
                this.startActivity(myIntent);
                overridePendingTransition(R.anim.left_in, R.anim.left_out);
                finish();
            }
        } else {
            Intent myIntent = new Intent(getApplicationContext(), UserLogin.class);
            myIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            myIntent.putExtra("cameFromWhichActivity", "MainActivity");
            myIntent.putExtra("userType", "user");
            this.startActivity(myIntent);
            overridePendingTransition(R.anim.left_in, R.anim.left_out);
            finish();
        }
    }

    public void checkLoginSharedPrefference() {
        prefs = getSharedPreferences("logginInformation", MODE_PRIVATE);
        isLogged = prefs.getString("isLogged?", null);
    }

    public void startUserProfileActivity() {
        Intent myIntent = new Intent(getApplicationContext(), UserProfile.class);
        myIntent.putExtra("cameFromWhichActivity", "MainActivity");
        myIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        this.startActivity(myIntent);
        overridePendingTransition(R.anim.left_in, R.anim.left_out);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    public void onDestroy() {
        super.onDestroy();
        if (progressDialog.getAlertDialog() != null) progressDialog.hideProgressDialog();
    }

    public void startShowStatisticsActivity(View view) {
        Intent myIntent = new Intent(getApplicationContext(), ShowStatistics.class);
        myIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        this.startActivity(myIntent);
        overridePendingTransition(R.anim.left_in, R.anim.left_out);
        finish();
    }

    public void checkOnLineOrNOt() {
        if (isOnline()) {
            floatingActionMenu.setVisibility(View.VISIBLE);
            userPostMCS.clear();
            noInternetImageView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            Intent extras = getIntent();
            if (extras != null) {
                String cameFromWhichActivity = extras.getStringExtra("cameFromWhichActivity");
                if (cameFromWhichActivity == null) {
                    RetriveDataFromServer();
                    clearPostDataSharedPrefference();
                } else if (cameFromWhichActivity.contains("UserCreatePost") || cameFromWhichActivity.contains("UserPostEdit") || cameFromWhichActivity.contains("PostViewActivityWithReport")) {
                    RetriveDataFromServer();
                } else {
                    retrivesharedpreference();
                }

            } else {
                TastyToast.makeText(getApplicationContext(), "Data is null in UserCreatePostActivity \n please contact with devloper or try later", TastyToast.LENGTH_LONG, TastyToast.ERROR);
            }
        } else {
            floatingActionMenu.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
            noInternetImageView.setVisibility(View.VISIBLE);
            Glide.with(MainActivity.this).load(R.drawable.no_internet_image).into(noInternetImageView);
        }
    }

    public void relodDtaFromServer(View view) {
        checkOnLineOrNOt();
    }

    public void clearPostDataSharedPrefference() {
        SharedPreferences preferences = getSharedPreferences("PostData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)) {
            volumeKeyPressed++;
            if (volumeKeyPressed == 2) {
                volumeKeyPressed = 0;
                //Toast.makeText(this, "clicked", Toast.LENGTH_SHORT).show();
                LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    checkPermissions();
                }
                Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if(location==null){
                    location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    Log.d("locationKey",location.toString());
                    Geocoder geocoder;
                    List<Address> addresses;
                    geocoder=new Geocoder(MainActivity.this, Locale.getDefault());
                    try {
                        addresses=geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                        Log.d("city",addresses.get(0).getLocality());
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.d("locationxcptn",e.toString());
                    }
                }
                else
                    Log.d("locationKey","null");

            }
            return true;
        } else return super.onKeyDown(keyCode, event);
    }
}
