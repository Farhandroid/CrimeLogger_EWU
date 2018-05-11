package tanvir.crimelogger_ewu.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.signature.ObjectKey;
import com.google.gson.Gson;
import com.sdsmdg.tastytoast.TastyToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import de.hdodenhof.circleimageview.CircleImageView;
import ru.whalemare.sheetmenu.SheetMenu;
import tanvir.crimelogger_ewu.HelperClass.AppController;
import tanvir.crimelogger_ewu.HelperClass.MySingleton;
import tanvir.crimelogger_ewu.HelperClass.ProgressDialog;
import tanvir.crimelogger_ewu.HelperClass.RecyclerAdapter;
import tanvir.crimelogger_ewu.MOdelClass.UserPostMC;
import tanvir.crimelogger_ewu.R;

public class UserProfile extends AppCompatActivity {
    private CircleImageView circleImageView;
    private ArrayList<UserPostMC> userPostMCS;
    private ArrayList<UserPostMC> userPostData;
    RelativeLayout relativeLayout;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private TextView nameTV, emailTV, phoneTV;
    private EditText passwordET, retypePasswordET;
    private Button userProfileMenuBTN;
    private String cameFromWhichActivity = "";
    private Context context;
    private String name, email, phone, isPCavailable;
    private String userName;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        nameTV = findViewById(R.id.nameAfterProfileImage);
        emailTV = findViewById(R.id.emailAfterProfileImage);
        phoneTV = findViewById(R.id.phoneAfterProfileImage);
        relativeLayout = findViewById(R.id.emptyRelativeLayoutUserPost);
        recyclerView = findViewById(R.id.recyclerViewInUP);
        userPostData = new ArrayList<>();
        context = UserProfile.this;
        circleImageView = findViewById(R.id.profile_image);
        cameFromWhichActivity = getIntent().getStringExtra("cameFromWhichActivity");
        userProfileMenuBTN = findViewById(R.id.userProfileMenuBTN);
        userProfileEditMenuShouldHideOrNot();
        progressDialog = new ProgressDialog(UserProfile.this);
        if (cameFromWhichActivity.contains("don't show edit button")) {
            userName = getIntent().getStringExtra("userName");
            if (userName == null)
                Toast.makeText(context, "userName not fount in user profile", Toast.LENGTH_SHORT).show();
            retrieveUserInformationFromServer();
        } else {
            checkLoginSharedPrefference();
        }
    }

    public void updateRecyclerView(ArrayList<UserPostMC> userPostMCS) {
        SharedPreferences.Editor editor = getSharedPreferences("whichActivity", MODE_PRIVATE).edit();
        editor.putString("activity", "profile");
        editor.apply();
        String activity;
        if (cameFromWhichActivity.equals("don't show edit button")) {
            activity = "don't show edit button";
        } else activity = "show edit menu";
        if (userPostMCS.size() > 0) {
            recyclerView.setVisibility(View.VISIBLE);
            adapter = new RecyclerAdapter(this, userPostMCS, activity);
            layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(adapter);
        } else {
            relativeLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            ///Toast.makeText(this, "No post yet", Toast.LENGTH_SHORT).show();
        }
    }

    public void retriveDataFromArraylist() {
        for (int i = 0; i < userPostMCS.size(); i++) {
            if (userPostMCS.get(i).getUserName().equals(userName)) {
                userPostData.add(userPostMCS.get(i));
            }
        }
        updateRecyclerView(userPostData);
    }

    public void retrivesharedpreference() {
        SharedPreferences prefs = getSharedPreferences("PostData", MODE_PRIVATE);
        Gson gson = new Gson();
        String jsonText = prefs.getString("PostData", null);
        if (jsonText != null) {
            UserPostMC[] text = gson.fromJson(jsonText, UserPostMC[].class);
            userPostMCS = new ArrayList<>(Arrays.asList(text));
            retriveDataFromArraylist();
        } else {
            relativeLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            ///Toast.makeText(this, "No post yet", Toast.LENGTH_SHORT).show();
        }
    }

    public void showUserProfileMenu(View view) {
        showSheetMenu();
    }

    public void showSheetMenu() {
        SheetMenu.with(this).setTitle("Select Option").setMenu(R.menu.user_profile_option).setAutoCancel(true).setClick(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.editProfile) {
                    Intent myIntent = new Intent(getApplicationContext(), UserRegistration.class);
                    myIntent.putExtra("cameFromWhichActivity", "userProfile");
                    ///myIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    UserProfile.this.startActivity(myIntent);
                    overridePendingTransition(R.anim.left_in, R.anim.left_out);
                    finish();
                    ///Toast.makeText(UserProfile.this, "Edit profile click", Toast.LENGTH_SHORT).show();
                } else if (item.getItemId() == R.id.changePassword) {
                    showPasswordChangeAlertDialog();
                    ///Toast.makeText(UserProfile.this, "changePassword click", Toast.LENGTH_SHORT).show();
                } else if (item.getItemId() == R.id.logOut) {
                    setLogginnInformation();
                    TastyToast.makeText(getApplicationContext(), "Logged out successfull", TastyToast.LENGTH_LONG, TastyToast.ERROR);
                    startMainActivity();
                }
                return false;
            }
        }).show();
    }

    public void checkLoginSharedPrefference() {
        SharedPreferences prefs = getSharedPreferences("logginInformation", MODE_PRIVATE);
        String isLogged = prefs.getString("isLogged?", null);
        if (isLogged != null && isLogged.contains("yes")) {
            userName = prefs.getString("userName", null);
            name = prefs.getString("name", null);
            phone = prefs.getString("phoneNumber", null);
            email = prefs.getString("email", null);
            isPCavailable = prefs.getString("isPCavailable", null);
            setProfileData();
        } else {
            TastyToast.makeText(getApplicationContext(), "Logged in shared prefference data not found \n please contact with devloper or try later", TastyToast.LENGTH_LONG, TastyToast.ERROR);
        }
    }

    public void setProfileData() {
        nameTV.setText(name);
        emailTV.setText(email);
        phoneTV.setText(phone);
        retrivesharedpreference();
        if (isPCavailable.equals("1")) {
            progressDialog.showProgressDialog();
            String url = "http://www.farhandroid.com/CrimeLogger/Script/UserProfilePic/" + userName + ".jpg";
            RequestOptions options = new RequestOptions();
            options.centerCrop();
            options.placeholder(R.drawable.empty_profile);
            options.signature(new ObjectKey(System.currentTimeMillis()));
            Glide.with(UserProfile.this).load(url).apply(options).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    progressDialog.hideProgressDialog();
                    TastyToast.makeText(getApplicationContext(), "Problem in Image retrieve \n Please try later", TastyToast.LENGTH_LONG, TastyToast.ERROR);
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    progressDialog.hideProgressDialog();
                    return false;
                }
            }).into(circleImageView);
        }
    }

    public void setLogginnInformation() {
        SharedPreferences.Editor editor = getSharedPreferences("logginInformation", MODE_PRIVATE).edit();
        editor.putString("isLogged?", "no");
        editor.apply();
    }

    public void startMainActivity() {
        Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
        myIntent.putExtra("cameFromWhichActivity", "UserProfile");
        myIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        this.startActivity(myIntent);
        overridePendingTransition(R.anim.left_in, R.anim.left_out);
        finish();
    }

    public void startLoginActivity() {
        Intent myIntent = new Intent(getApplicationContext(), UserLogin.class);
        myIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        this.startActivity(myIntent);
        overridePendingTransition(R.anim.left_in, R.anim.left_out);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startMainActivity();
    }

    public void showPasswordChangeAlertDialog() {
        final AlertDialog alertDialog;
        final View changePasswordDialogView;
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(UserProfile.this);
        LayoutInflater inflater = (LayoutInflater) UserProfile.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        changePasswordDialogView = inflater.inflate(R.layout.change_password, null);
        final Button changePasswordBTN = changePasswordDialogView.findViewById(R.id.changePassword);
        passwordET = changePasswordDialogView.findViewById(R.id.passwordInChangePassword);
        retypePasswordET = changePasswordDialogView.findViewById(R.id.retypePasswordInChangePassword);
        changePasswordBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (passwordET.length() > 0 && retypePasswordET.length() > 0) {
                    changePassword();
                } else
                    TastyToast.makeText(getApplicationContext(), "Please fill up all field", TastyToast.LENGTH_LONG, TastyToast.SUCCESS);
            }
        });
        dialogBuilder.setView(changePasswordDialogView);
        alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    public void changePassword() {
       progressDialog.hideProgressDialog();
                String url = "http://www.farhandroid.com/CrimeLogger/Script/changePassword.php";
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String response) {
                        if (response.contains("Password successfully updated")) {
                            UserProfile.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    TastyToast.makeText(getApplicationContext(), "Password successfully updated", TastyToast.LENGTH_LONG, TastyToast.SUCCESS);
                                    setLogginnInformation();
                                    startLoginActivity();
                                }
                            });

                        } else if (response.contains("password doesn't match")) {
                            showErrorImageInMainThread("Password does't match \n please enter password again");
                        } else if (response.contains("Password update failed ")) {
                            TastyToast.makeText(getApplicationContext(), "Password update failed \nPlease contact with devloper or Try later\n", TastyToast.LENGTH_LONG, TastyToast.WARNING);
                        } else if (response.contains("user not found ")) {
                            TastyToast.makeText(getApplicationContext(), "user not found \nPlease contact with devloper or Try later\n", TastyToast.LENGTH_LONG, TastyToast.WARNING);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(final VolleyError error) {
progressDialog.hideProgressDialog();
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            showErrorImageInMainThread("Time out or no connection error \n Please check connection");
                        } else if (error instanceof AuthFailureError) {
                            showErrorImageInMainThread("Authentication failure error \n Please contact with devloper or try later");
                        } else if (error instanceof ServerError) {
                            showErrorImageInMainThread("Server error\n Please contact with devloper or Try later");

                        } else if (error instanceof NetworkError) {
                            showErrorImageInMainThread("Network error\n Please contact with devloper or Try later");

                        } else if (error instanceof ParseError) {

                            showErrorImageInMainThread("Parse error\n Please contact with devloper or Try later");
                        }
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<>();
                        params.put("oldPassword", passwordET.getText().toString());
                        params.put("newPassword", retypePasswordET.getText().toString());
                        params.put("userName", userName);
                        return params;
                    }
                };
                stringRequest.setRetryPolicy(new DefaultRetryPolicy(60000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                AppController.getInstance().addToRequestQueue(stringRequest);
            }


    public void showErrorImageInMainThread(final String response) {
        progressDialog.hideProgressDialog();
        UserProfile.this.runOnUiThread(new Runnable() {
            public void run() {
                TastyToast.makeText(getApplicationContext(), response, TastyToast.LENGTH_LONG, TastyToast.ERROR);
            }
        });
    }
    public void userProfileEditMenuShouldHideOrNot() {
        if (cameFromWhichActivity != null) {
            if (cameFromWhichActivity.contains("don't show edit button")) {
                userProfileMenuBTN.setVisibility(View.GONE);
            } else {
                userProfileMenuBTN.setVisibility(View.VISIBLE);
            }
        } else {
            TastyToast.makeText(getApplicationContext(), "cameFromWhichActivity not found in postViewActivity \n please contact with devloper or try later", TastyToast.LENGTH_LONG, TastyToast.ERROR);
        }
    }

    public void retrieveUserInformationFromServer() {
progressDialog.showProgressDialog();
        String url = "http://www.farhandroid.com/CrimeLogger/Script/retrieveUserInforMation.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(final String response) {
progressDialog.hideProgressDialog();

                if (response.contains("problen in query")) {
                    showErrorImageInMainThread("Problem in Database \n Please contact with devloper or Try again later");

                } else if (response.contains("user not found")) {
                    showErrorImageInMainThread("user data not found \n Please contact with devloper or Try again later");
                } else {
                    final JSONObject userInfo;
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        userInfo = jsonArray.getJSONObject(0);
                        name = userInfo.getString("name");
                        email = userInfo.getString("email");
                        phone = userInfo.getString("phoneNumber");
                        isPCavailable = userInfo.getString("isPCavailable");
                        setProfileData();
                    } catch (final JSONException e) {
                        showErrorImageInMainThread("Json Exception \n please contact with devloper or try later");
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(final VolleyError error) {
progressDialog.hideProgressDialog();
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    showErrorImageInMainThread("Time out or no connection error \n Please check connection");
                } else if (error instanceof AuthFailureError) {
                    showErrorImageInMainThread("Authentication failure error \n Please contact with devloper or try later");
                } else if (error instanceof ServerError) {
                    showErrorImageInMainThread("Server error\n Please contact with devloper or Try later");

                } else if (error instanceof NetworkError) {
                    showErrorImageInMainThread("Network error\n Please contact with devloper or Try later");
                } else if (error instanceof ParseError) {
                    showErrorImageInMainThread("Parse error\n Please contact with devloper or Try later");
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                try {
                    params.put("userName", userName);
                } catch (final Exception e) {
                    showErrorImageInMainThread("Eror in Data insertion \nPlease contact with devloper or Try later");
                }
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(60000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }
}
