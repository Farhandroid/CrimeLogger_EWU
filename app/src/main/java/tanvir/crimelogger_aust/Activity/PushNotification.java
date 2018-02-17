package tanvir.crimelogger_aust.Activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;

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
import com.kaopiz.kprogresshud.KProgressHUD;
import com.sdsmdg.tastytoast.TastyToast;
import com.viewpagerindicator.CirclePageIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import tanvir.crimelogger_aust.HelperClass.CustomSwipeAdapterForPushNotification;
import tanvir.crimelogger_aust.HelperClass.MySingleton;
import tanvir.crimelogger_aust.MOdelClass.UserPostMC;
import tanvir.crimelogger_aust.R;

public class PushNotification extends AppCompatActivity {

    CustomSwipeAdapterForPushNotification customSwipeAdapter;
    ViewPager viewPager;


    Context context = PushNotification.this;

    CardView imageCardViewInPushNotification;
    ArrayList<String> imageName;


    ArrayList<UserPostMC> userPostMCS;
    

    int NUM_PAGES = 0;

    KProgressHUD hud;
    String push_data;

    TextView breakingNewsTV;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push_notification);


        imageName = new ArrayList<>();
        breakingNewsTV = findViewById(R.id.breakingNewsTV);

        ///breakingNewsTV.setText("fuck");

        initializeKHUDprogress();

        imageCardViewInPushNotification = findViewById(R.id.imageCardViewInPushNotification);

        if (getIntent().getExtras() != null) {

            push_data = getIntent().getStringExtra("push_data");
            retrievePushNotificationDataFromServer();
            
        } else
            Toast.makeText(this, "Not found ma2", Toast.LENGTH_SHORT).show();
        





        ///setPostData();




    }

    public void initialView() {

        viewPager = findViewById(R.id.imageViewPagerInPostView);
        NUM_PAGES = imageName.size();


        customSwipeAdapter = new CustomSwipeAdapterForPushNotification(this, imageName, 0, hud);
        viewPager.setAdapter(customSwipeAdapter);

        CirclePageIndicator indicator = findViewById(R.id.indicator);

        indicator.setViewPager(viewPager);

        final float density = getResources().getDisplayMetrics().density;

        indicator.setRadius(5 * density);
    }


    public void setPostData() {

        int position = 0;



        if (userPostMCS.get(position).getHowManyImage().equals("0") == false) {

            imageCardViewInPushNotification.setVisibility(View.VISIBLE);
            retrivePostImageDataFromServer();
        } else {

            imageCardViewInPushNotification.setVisibility(View.GONE);
        }


    }


    public void retrivePostImageDataFromServer() {

        hud.show();

        String url = "http://www.farhandroid.com/CrimeLogger/Script/retrivePostImageData.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String response) {
                        hud.dismiss();


                        if (response.contains("image data not found")) {
                            showErrorInMainThread("Problem in image fetch \n please contact with devloper or try later  ");
                        } else {

                            try {
                                JSONArray jsonArray = new JSONArray(response);

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                                    if (jsonObject.has("imageName")) {
                                        imageName.add(jsonObject.getString("imageName"));
                                    }


                                }

                                PushNotification.this.runOnUiThread(new Runnable() {
                                    public void run() {
                                        initialView();

                                    }
                                });


                            } catch (JSONException e) {
                                e.printStackTrace();
                                hud.dismiss();
                                showErrorInMainThread("Json Exception \n please contact with devloper or try later  " + e.toString());

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

                            showErrorInMainThread("Time out or no connection error \n Please check connection");

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

                params.put("push_data", push_data);

                return params;

            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(40000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        MySingleton.getInstance(context).addToRequestQueue(stringRequest);


    }

    public void showErrorInMainThread(final String response) {
        PushNotification.this.runOnUiThread(new Runnable() {
            public void run() {
                TastyToast.makeText(getApplicationContext(), response, TastyToast.LENGTH_LONG, TastyToast.ERROR);


            }
        });
    }

    public void initializeKHUDprogress() {
        hud = KProgressHUD.create(context)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setDimAmount(0.6f)
                .setLabel("Please Wait")
                .setCancellable(false);
    }


    public void onBackPressed() {
        super.onBackPressed();


    }

    public void retrievePushNotificationDataFromServer()
    {
        hud.show();

        String url = "http://www.farhandroid.com/CrimeLogger/Script/retrivePushNotificationData.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String response) {

                        hud.dismiss();

                        final JSONObject pushDataInfo;

                        JSONArray jsonArray;
                        try {
                            jsonArray = new JSONArray(response);
                            pushDataInfo = jsonArray.getJSONObject(0);

                            String news = pushDataInfo.getString("news_details");

                            breakingNewsTV.setText(news);
                            ///Toast.makeText(context, news, Toast.LENGTH_SHORT).show();

                            String howManyImage = pushDataInfo.getString("howManyImage");

                            if (howManyImage.equals("0") == false) {

                                retrivePostImageDataFromServer();
                            }
                            else
                            {

                            }

                        } catch (JSONException e) {
                            Toast.makeText(context, "json exception : "+e.toString(), Toast.LENGTH_SHORT).show();
                        }



                       /// breakingNewsTV.setText(response);



                    }
                }
                ,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(final VolleyError error) {
                        hud.dismiss();

                        breakingNewsTV.setText("error : "+error.toString());


                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();

                params.put("push_data", push_data);

                return params;

            }
        };


        MySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }



}
