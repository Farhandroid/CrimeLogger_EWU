package tanvir.crimelogger_aust.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.kaopiz.kprogresshud.KProgressHUD;
import com.sdsmdg.tastytoast.TastyToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import tanvir.crimelogger_aust.HelperClass.MySingleton;
import tanvir.crimelogger_aust.MOdelClass.UserInfoMC;
import tanvir.crimelogger_aust.R;

public class UserLogin extends AppCompatActivity {


    private EditText userNameET, passwordET;
    private UserInfoMC userInfoMC;

    private KProgressHUD hud;

    private String cameFromWhichActivity = "";
    private String userType = "";
    private String forgotEmail = "";


    AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);


        userNameET = findViewById(R.id.input_username);
        passwordET = findViewById(R.id.input_password);

        checkLogINSharedPrefference();
        initializeKHUDprogress();


        cameFromWhichActivity = getIntent().getStringExtra("cameFromWhichActivity");
        userType = getIntent().getStringExtra("userType");


    }


    public void startRegisterActivity(View view) {

        Intent myIntent = new Intent(this, UserRegistration.class);
        myIntent.putExtra("cameFromWhichActivity", "userLogin");
        this.startActivity(myIntent);
        overridePendingTransition(R.anim.left_in, R.anim.left_out);
        finish();
    }


    public void checkUserLoginInformationFromServer(View view) {

        if (userNameET.getText().toString().length() > 0 && passwordET.getText().toString().length() > 0) {
            hud.show();
            new Thread(new Runnable() {
                public void run() {

                    userLogin();

                }
            }).start();
        } else {
            TastyToast.makeText(getApplicationContext(), "Please fill up all the field", TastyToast.LENGTH_LONG, TastyToast.WARNING);
        }


    }


    public void userLogin() {


        String url = "http://www.farhandroid.com/CrimeLogger/Script/userLogin.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String response) {

                        hud.dismiss();


                        /// Toast.makeText(UserLogin.this, "response : "+response, Toast.LENGTH_SHORT).show();

                        if (response.contains("problen in query")) {

                            showErrorImageInMainThread("Problem in Database \n Please contact with devloper or Try again later");

                        } else if (response.contains("login_failed")) {

                            showErrorImageInMainThread("Login Failed\nPlease check userName and password");

                        } else {

                            final JSONObject userInfo;
                            try {
                                JSONArray jsonArray = new JSONArray(response);

                                userInfo = jsonArray.getJSONObject(0);


                                userInfoMC = new UserInfoMC(userInfo.getString("name"), userInfo.getString("userName"), userInfo.getString("email"), userInfo.getString("phoneNumber"), userInfo.getString("isPCavailable"));

                                UserLogin.this.runOnUiThread(new Runnable() {
                                    public void run() {

                                        setLoggedInformationSharedPrefference();

                                       /// Toast.makeText(UserLogin.this, "came : "+cameFromWhichActivity, Toast.LENGTH_SHORT).show();
                                        ///Toast.makeText(UserLogin.this, "userType : "+userType, Toast.LENGTH_SHORT).show();

                                        if (cameFromWhichActivity != null && userType != null) {

                                            ///Toast.makeText(UserLogin.this, "Enter", Toast.LENGTH_SHORT).show();


                                            if (cameFromWhichActivity.contains("MainActivity") && userType.contains("user")) {
                                                Intent myIntent = new Intent(getApplicationContext(), UserCreatePost.class);

                                                myIntent.putExtra("userType", "user");
                                                myIntent.putExtra("userName", userNameET.getText().toString());
                                                UserLogin.this.startActivity(myIntent);
                                                overridePendingTransition(R.anim.left_in, R.anim.left_out);
                                                finish();
                                            }
                                            else if (cameFromWhichActivity.contains("UserPostView"))
                                            {
                                                String postDateAndTime  = getIntent().getStringExtra("postDateAndTime");

                                                if (postDateAndTime!=null)
                                                {
                                                    Intent myIntent = new Intent(getApplicationContext(), PostViewActivity.class);
                                                    myIntent.putExtra("cameFromWhichActivity","UserLogin");
                                                    myIntent.putExtra("postDateAndTime", postDateAndTime);
                                                    UserLogin.this.startActivity(myIntent);
                                                    overridePendingTransition(R.anim.left_in, R.anim.left_out);
                                                    finish();
                                                }
                                                else
                                                    Toast.makeText(UserLogin.this, "postDateAnd time not found", Toast.LENGTH_SHORT).show();



                                            }
                                        }
                                        else {

                                            Toast.makeText(UserLogin.this, "Enter else", Toast.LENGTH_SHORT).show();
                                            startUserProfileActivity();
                                        }


                                    }
                                });


                            } catch (final JSONException e) {

                                showErrorImageInMainThread("Json Exception \n please contact with devloper or try later");


                            }

                        }


                    }
                }
                ,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(final VolleyError error) {

                        hud.dismiss();

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
                }
        ) {
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();

                try {


                    params.put("userName", userNameET.getText().toString());
                    params.put("password", passwordET.getText().toString());


                } catch (final Exception e) {

                    showErrorImageInMainThread("Eror in Data insertion \nPlease contact with devloper or Try later");

                }

                return params;

            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        MySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }


    public void startUserProfileActivity() {


        Intent myIntent = new Intent(this, UserProfile.class);
        myIntent.putExtra("cameFromWhichActivity", "UserLogin");
        myIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        this.startActivity(myIntent);
        overridePendingTransition(R.anim.left_in, R.anim.left_out);
        finish();
    }


    public void initializeKHUDprogress() {
        hud = KProgressHUD.create(UserLogin.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setDimAmount(0.6f)
                .setLabel("Please Wait")
                .setCancellable(false);
    }


    public void showErrorImageInMainThread(final String response) {
        UserLogin.this.runOnUiThread(new Runnable() {
            public void run() {
                TastyToast.makeText(getApplicationContext(), response, TastyToast.LENGTH_LONG, TastyToast.ERROR);


            }
        });
    }

    public void setLoggedInformationSharedPrefference() {
        SharedPreferences.Editor editor = getSharedPreferences("logginInformation", MODE_PRIVATE).edit();
        editor.putString("isLogged?", "yes");
        editor.putString("userName", userInfoMC.getUserName());
        editor.putString("name", userInfoMC.getName());
        editor.putString("phoneNumber", userInfoMC.getPhoneNumber());
        editor.putString("email", userInfoMC.getEmail());
        editor.putString("isPCavailable", userInfoMC.getIsPCavailable());

        editor.apply();
    }

    public void checkLogINSharedPrefference() {
        SharedPreferences prefs = getSharedPreferences("logginInformation", MODE_PRIVATE);
        String isLogged = prefs.getString("isLogged?", null);

        if (isLogged != null && isLogged.contains("yes")) {
            startUserProfileActivity();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        startMainActivity();


    }

    public void startMainActivity() {
        Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
        myIntent.putExtra("cameFromWhichActivity", "UserLogin");
        myIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        this.startActivity(myIntent);
        overridePendingTransition(R.anim.left_in, R.anim.left_out);
        finish();

    }


    public void forgotPassword(View view) {


        View forgotPasswordView;

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(UserLogin.this);

        LayoutInflater inflater = (LayoutInflater) UserLogin.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        forgotPasswordView = inflater.inflate(R.layout.forgot_password, null);
        dialogBuilder.setView(forgotPasswordView);
        alertDialog = dialogBuilder.create();
        alertDialog.show();


        Button sendMailBTN = forgotPasswordView.findViewById(R.id.sendMail);
        final EditText editText = forgotPasswordView.findViewById(R.id.gmailInForgotPassword);

        sendMailBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                forgotEmail = editText.getText().toString();

                if (forgotEmail.length() > 0) {
                    sendMail();
                } else {
                    TastyToast.makeText(getApplicationContext(), "Please fill up all field", TastyToast.LENGTH_LONG, TastyToast.WARNING);
                }
            }
        });
    }


    public void sendMail() {
        String url = "http://www.farhandroid.com/CrimeLogger/Script/forgotPassword.php";


        hud.show();


        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String response) {

                        hud.dismiss();




                        if (response.contains("mail sent succcess")) {

                            alertDialog.dismiss();

                            TastyToast.makeText(getApplicationContext(), "Please check your email's inbox\n if email not found please check  spam folder", TastyToast.LENGTH_LONG, TastyToast.INFO);


                        } else if (response.contains("Email not found")) {


                            showErrorInMainThread("Email Not Found \n please check your email");

                        } else if (response.contains("mail sent fail")) {

                            showErrorInMainThread("Email can't be sent  \n please contact with devloper or try later  ");


                        }
                    }
                }
                ,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(final VolleyError error) {
                        hud.dismiss();
                        alertDialog.dismiss();

                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {

                            showErrorInMainThread("Time out or no connection error post \n Please check connection");

                        } else if (error instanceof AuthFailureError) {

                            showErrorInMainThread("Authentication failure error \n Please contact with devloper or try later");

                        } else if (error instanceof ServerError) {

                            showErrorInMainThread("Server error\n Please contact with devloper or Try later");

                        } else if (error instanceof NetworkError) {
                            showErrorInMainThread("Network error\n Please contact with devloper or Try later");

                        } else if (error instanceof ParseError) {

                            showErrorInMainThread("Parse error\n Please contact with devloper or Try later");

                        }


                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();

                params.put("email", forgotEmail);

                return params;

            }
        };


        MySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    public void showErrorInMainThread(final String response) {
        UserLogin.this.runOnUiThread(new Runnable() {
            public void run() {
                TastyToast.makeText(getApplicationContext(), response, TastyToast.LENGTH_LONG, TastyToast.ERROR);


            }
        });
    }
}
