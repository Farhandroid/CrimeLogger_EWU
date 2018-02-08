package tanvir.crimelogger_aust.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
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
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.sdsmdg.tastytoast.TastyToast;
import com.viewpagerindicator.CirclePageIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import tanvir.crimelogger_aust.HelperClass.CustomSwipeAdapterForPostView;
import tanvir.crimelogger_aust.HelperClass.MySingleton;
import tanvir.crimelogger_aust.MOdelClass.UserPostMC;
import tanvir.crimelogger_aust.R;

public class PostViewActivity extends AppCompatActivity {



    CustomSwipeAdapterForPostView customSwipeAdapter;
    ViewPager viewPager;
    AlertDialog alertDialog;

    CardView imageCardViewInPostViewActivity;


    ArrayList<String> imageName;

    ArrayList<UserPostMC> userPostMCS;
    private String postDateAndTime;
    private String userName;

    CircleImageView userImageInPostView;

    int NUM_PAGES = 0;

    KProgressHUD hud;


    private int positionIfNeedToBeDeleted = 0;

    boolean successInUserPostDataDelete = false;
    boolean successInUserPostImageDelete = false;

    private String cameFromWhichActivity = "";
    private FloatingActionButton postEditBTNImageCardView;
    private FloatingActionButton postEditBTNDataCardView;


    TextView crimePlaceTV, crimeDateTV, crimeTimeTV, crimeDescriptionTV, crimeTypeTV;
    TextView postDateTV, postTimeTV, postedByTV;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_view);

        crimePlaceTV = findViewById(R.id.crimePlaceETPA);
        crimeDateTV = findViewById(R.id.crimeDateETPA);
        crimeTimeTV = findViewById(R.id.crimeTimeETPA);
        ///crimeTypeTV=findViewById(R.id.crimeTypeETPA);
        crimeDescriptionTV = findViewById(R.id.crimeDescETPA);
        postDateTV = findViewById(R.id.postDateTVPA);
        postTimeTV = findViewById(R.id.postTimeTVPA);
        postedByTV = findViewById(R.id.postedByTVPA);

        imageName = new ArrayList<>();


        imageCardViewInPostViewActivity = findViewById(R.id.imageCardViewInPostViewActivity);
        postEditBTNDataCardView = findViewById(R.id.postEditBTNDataCardView);
        postEditBTNImageCardView = findViewById(R.id.postEditBTNImageCardView);
        userImageInPostView = findViewById(R.id.userImageInPostView);


   ;


        postDateAndTime = getIntent().getStringExtra("postDateAndTime");

       //// Toast.makeText(this, "postDateAndTime : "+ postDateAndTime, Toast.LENGTH_LONG).show();

        cameFromWhichActivity = getIntent().getStringExtra("cameFromWhichActivity");


        if (postDateAndTime.length() > 0) {

            retrivePostDataFromSharedpreference();
            ///Toast.makeText(this, "postDateAndTime : "+postDateAndTime, Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(this, "postDateAndTime not found in Postview activity", Toast.LENGTH_SHORT).show();

        ///UserPostMC userPostMC = databaseHelper.getPostFromDB(postDateAndTime);

        ///crimePlaceTV.setText(userPostMC.getCrimePlace());*/

        initializeKHUDprogress();


        setPostData();


    }

    public void initialView() {

        viewPager = findViewById(R.id.imageViewPagerInPostView);
        NUM_PAGES = imageName.size();



        customSwipeAdapter = new CustomSwipeAdapterForPostView(this, imageName, 0, hud);
        viewPager.setAdapter(customSwipeAdapter);

        CirclePageIndicator indicator = findViewById(R.id.indicator);

        indicator.setViewPager(viewPager);

        final float density = getResources().getDisplayMetrics().density;

        indicator.setRadius(5 * density);
    }

    public void retrivePostDataFromSharedpreference() {
        SharedPreferences prefs = getSharedPreferences("PostData", MODE_PRIVATE);
        Gson gson = new Gson();
        String jsonText = prefs.getString("PostData", null);
        UserPostMC[] text = gson.fromJson(jsonText, UserPostMC[].class);
        userPostMCS = new ArrayList<>(Arrays.asList(text));

        if (userPostMCS.size() > 0) {
           /* Toast.makeText(this, "Arraylist Found", Toast.LENGTH_SHORT).show();

            for (int i = 0; i < userPostMCS.size(); i++) {
                Toast.makeText(this, "i = " + Integer.toString(i) + "Value = " + userPostMCS.get(i).getPostDateAndTime(), Toast.LENGTH_SHORT).show();
            }*/
        } else
            Toast.makeText(this, "Arraylist did'nt Found in postview activity", Toast.LENGTH_SHORT).show();

    }

    public void setPostData() {

        int position = 0;

        for (int i = 0; i < userPostMCS.size(); i++) {
            if (userPostMCS.get(i).getPostDateAndTime().equals(postDateAndTime)) {
                position = i;
                positionIfNeedToBeDeleted = i;
                crimeDateTV.setText(userPostMCS.get(i).getCrimeDate());
                crimeTimeTV.setText(userPostMCS.get(i).getCrimeTime());
                crimePlaceTV.setText(userPostMCS.get(i).getCrimePlace());
                crimeDescriptionTV.setText(userPostMCS.get(i).getCrimeDesc());
                postedByTV.setText(userPostMCS.get(i).getUserName());
                userName = userPostMCS.get(i).getUserName();

                getPositionForSeparateDateAndTime();

                postDateTV.setText(postDateAndTime.substring(0, getPositionForSeparateDateAndTime()));
                postTimeTV.setText(postDateAndTime.substring(getPositionForSeparateDateAndTime()));

                setUserImage();


                break;
            }
        }

        if (userPostMCS.get(position).getHowManyImage().equals("0") == false) {


            imageCardViewInPostViewActivity.setVisibility(View.VISIBLE);
            visibleOrInvisiblePostEditButton(true);

            retrivePostImageDataFromServer();
        } else {
            visibleOrInvisiblePostEditButton(false);
            imageCardViewInPostViewActivity.setVisibility(View.GONE);
        }


    }

    public int getPositionForSeparateDateAndTime() {

        int p = postDateAndTime.indexOf(" ");

        ///Toast.makeText(this, "position : "+Integer.toString(p), Toast.LENGTH_SHORT).show();

        return p;
    }

    public void retrivePostImageDataFromServer() {

        hud.show();
        new Thread(new Runnable() {
            public void run() {

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

                                        PostViewActivity.this.runOnUiThread(new Runnable() {
                                            public void run() {
                                                initialView();

                                            }
                                        });


                                        //if (jsonArray.length()>0)
                                        /// Toast.makeText(PostViewActivity.this, "Image data found", Toast.LENGTH_SHORT).show();

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

                        params.put("postDateAndTime", postDateAndTime);

                        return params;

                    }
                };

                stringRequest.setRetryPolicy(new DefaultRetryPolicy(90000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


                MySingleton.getInstance(PostViewActivity.this).addToRequestQueue(stringRequest);


            }
        }).start();


    }

    public void showErrorInMainThread(final String response) {
        PostViewActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                TastyToast.makeText(getApplicationContext(), response, TastyToast.LENGTH_LONG, TastyToast.ERROR);


            }
        });
    }

    public void initializeKHUDprogress() {
        hud = KProgressHUD.create(PostViewActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setDimAmount(0.6f)
                .setLabel("Please Wait")
                .setCancellable(false);
    }



    public void startMainActivity() {
        Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
        myIntent.putExtra("cameFromWhichActivity", "PostViewActivity");
        myIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        this.startActivity(myIntent);
        overridePendingTransition(R.anim.left_in, R.anim.left_out);
        finish();

    }


    public void startUserPostEditActivity() {


        Intent myIntent = new Intent(getApplicationContext(), UserPostEdit.class);
        myIntent.putExtra("postDateAndTime", postDateAndTime);
        myIntent.putExtra("userName", userName);

        this.startActivity(myIntent);
        overridePendingTransition(R.anim.left_in, R.anim.left_out);
        finish();

    }

    public void initializePostEditOptionDialog(View view) {


        final View editPostOptioView;

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        editPostOptioView = inflater.inflate(R.layout.user_post_update_delete, null);
        dialogBuilder.setView(editPostOptioView);
        alertDialog = dialogBuilder.create();
        alertDialog.show();

        LinearLayout update = editPostOptioView.findViewById(R.id.updatePost);
        LinearLayout delete = editPostOptioView.findViewById(R.id.deletePost);

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                startUserPostEditActivity();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                hud.show();

                new Thread(new Runnable() {
                    public void run() {

                        deleteUserPostData();


                    }
                }).start();

            }
        });
    }


    public void deleteUserPostData() {
        alertDialog.dismiss();
        String url = "http://www.farhandroid.com/CrimeLogger/Script/deleteUserPost.php";


        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String response) {


                        PostViewActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                ///Toast.makeText(PostViewActivity.this, "post response  : " + response.toString(), Toast.LENGTH_LONG).show();

                                if (response.contains("Post delete Success")) {
                                    successInUserPostDataDelete = true;

                                    if (imageName.size() > 0) {
                                        postImageDelete();
                                    } else {
                                        successInUserPostImageDelete = true;
                                        showSuccessMsg();
                                    }


                                } else if (response.contains("Post delete  Fail")) {
                                    successInUserPostDataDelete = false;
                                    showErrorInMainThread("Problem in post data delete \n Please contact with devloper or Try again later");
                                }


                            }
                        });

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

                params.put("postDateAndTime", postDateAndTime);

                return params;

            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(90000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        MySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }


    public void postImageDelete() {
        int i;

        for (i = 0; i < imageName.size(); i++) {
            final int position = i;

            String url = "http://www.farhandroid.com/CrimeLogger/Script/deleteUserPostImage.php";


            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(final String response) {




                            PostViewActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    /// Toast.makeText(UserCreatePost.this, "image respone : " + response, Toast.LENGTH_SHORT).show();

                                    if (response.contains("ImageName delete Success") && response.contains("image delete success")) {
                                        successInUserPostImageDelete = true;

                                        if (position + 1 == imageName.size()) {
                                            showSuccessMsg();
                                        }

                                    } else if (response.contains("ImageName delete  Fail")) {

                                        showErrorInMainThread("Problem in image table Database \n Please contact with devloper or Try again later");
                                    } else if (response.contains("image delete fail")) {

                                        showErrorInMainThread("Problem in image image delete \n Please contact with devloper or Try again later");

                                    } else if (response.contains("deleteImageName empty")) {
                                        showErrorInMainThread("Problem in send imagename to server \n Please contact with devloper or Try again later");
                                    }



                                }
                            });


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



                    params.put("deleteImageName", imageName.get(position));

                    return params;

                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(90000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


            MySingleton.getInstance(this).addToRequestQueue(stringRequest);

        }



    }


    public void showSuccessMsg() {

        PostViewActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                hud.dismiss();
            }
        });




        if (successInUserPostDataDelete == true && successInUserPostImageDelete == true) {
            TastyToast.makeText(getApplicationContext(), "Post Deleted Successfuly", TastyToast.LENGTH_SHORT, TastyToast.SUCCESS);

            removeDataFromSharedPrefference();
            startUserProfileActivity("show");

        } else if (successInUserPostDataDelete == false) {
            showErrorInMainThread("problem in user post Data delete \nplease contact with devloper or try later");

        } else if (successInUserPostImageDelete == false) {
            showErrorInMainThread("problem in user post image delete \nplease contact with devloper or try later");
        }




    }

    public void removeDataFromSharedPrefference() {
        userPostMCS.remove(positionIfNeedToBeDeleted);

        SharedPreferences preferences = getSharedPreferences("PostData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();

        setSharedPrefference();

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

    public void visibleOrInvisiblePostEditButton(boolean imageContain) {
        if (cameFromWhichActivity.length() > 0) {
            if (cameFromWhichActivity.equals("MainActivity") || cameFromWhichActivity.equals("don't show edit button")) {
                postEditBTNImageCardView.setVisibility(View.GONE);
                postEditBTNDataCardView.setVisibility(View.GONE);
            } else if (cameFromWhichActivity.equals("show edit menu")) {
                if (imageContain) {
                    postEditBTNImageCardView.setVisibility(View.VISIBLE);
                    postEditBTNDataCardView.setVisibility(View.GONE);
                } else {
                    postEditBTNImageCardView.setVisibility(View.GONE);
                    postEditBTNDataCardView.setVisibility(View.VISIBLE);
                }

            }
        } else {
            TastyToast.makeText(getApplicationContext(), "cameFromWhichActivity not found in postViewActivity \n please contact with devloper or try later", TastyToast.LENGTH_LONG, TastyToast.ERROR);
        }
    }

    public void setUserImage() {
        Glide glide = null;
        if (!userName.equals("Anonymous")) {


            RequestOptions options = new RequestOptions();
            options.centerCrop();
            options.signature(new ObjectKey(System.currentTimeMillis()));
            options.placeholder(R.drawable.person2);


            String url = "http://www.farhandroid.com/CrimeLogger/Script/UserProfilePic/" + userName + ".jpg";

            glide.with(PostViewActivity.this)
                    .load(url)
                    .apply(options)
                    .into(userImageInPostView);
        } else {


            Glide.with(PostViewActivity.this)
                    .load(R.drawable.person2)
                    .into(userImageInPostView);


        }
    }


    public void showUserProfile(View view) {

        if (cameFromWhichActivity != null) {
            if (cameFromWhichActivity.equals("MainActivity") && !userName.equals("Anonymous")) {
                startUserProfileActivity("don't show edit button");
            } else {


            }
        } else {
            TastyToast.makeText(getApplicationContext(), "cameFromWhichActivity not found in postViewActivity \n please contact with devloper or try later", TastyToast.LENGTH_LONG, TastyToast.ERROR);
        }


    }

    public void startUserProfileActivity(String activity) {

        Intent myIntent = new Intent(getApplicationContext(), UserProfile.class);
        myIntent.putExtra("cameFromWhichActivity", activity);

        if (activity.contains("don't show edit button"))
            myIntent.putExtra("userName",userName);

        myIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        this.startActivity(myIntent);
        overridePendingTransition(R.anim.left_in, R.anim.left_out);
        finish();
    }

    public void onBackPressed() {
        super.onBackPressed();

        if (cameFromWhichActivity.equals("don't show edit button"))
            startUserProfileActivity("don't show edit button");
        else if (cameFromWhichActivity.equals("show edit menu"))
        {
            startUserProfileActivity("show edit menu");
        }
        else
            startMainActivity();


    }


}
