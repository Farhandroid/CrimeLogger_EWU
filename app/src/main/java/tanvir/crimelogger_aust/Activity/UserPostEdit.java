package tanvir.crimelogger_aust.Activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.graphics.BitmapCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TimePicker;
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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.sangcomz.fishbun.FishBun;
import com.sangcomz.fishbun.define.Define;
import com.sdsmdg.tastytoast.TastyToast;
import com.viewpagerindicator.CirclePageIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import id.zelory.compressor.Compressor;
import tanvir.crimelogger_aust.HelperClass.CustomSwipeAdapterForEditPost;
import tanvir.crimelogger_aust.HelperClass.MyCommand;
import tanvir.crimelogger_aust.HelperClass.MySingleton;
import tanvir.crimelogger_aust.MOdelClass.UserPostMC;
import tanvir.crimelogger_aust.R;

public class UserPostEdit extends AppCompatActivity {

    private CustomSwipeAdapterForEditPost customSwipeAdapter;

    private EditText crimePlaceET, crimeDateET, crimeTimeET, crimeDescriptionET;
    private MultiAutoCompleteTextView crimeTypeET;

    private String defaultEmptymage;
    private String userName;

    private Receiver mReceiver = null;

    int NUM_PAGES = 0;

    private ViewPager viewPager;
    private CirclePageIndicator indicator;
    private ArrayList<UserPostMC> userPostMCS;
    private ArrayList<UserPostMC> userPostMCSCOPY;

    private KProgressHUD hud;

    private ArrayList<String> imageName;
    private ArrayList<String> imageNameCOPY;
    private ArrayList<String> deleteImage;
    private ArrayList<Uri> uploadImage;


    private Boolean successInPostDataUpdate = false;
    private Boolean successInPostImageInsert = false;
    private Boolean successInPostImageDelete = false;

    private Boolean showedSuccessInsertionDialog=false;
    private Boolean showedTimeOutError = false;

    private String postDateAndTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_post_edit);

        imageName = new ArrayList<>();
        imageNameCOPY = new ArrayList<>();

        Uri uri = Uri.parse(Integer.toString(R.drawable.empty_image));
        defaultEmptymage = "`" + uri.toString();


        crimeTimeET = findViewById(R.id.crimeTimeETInUserPostEdit);
        crimeDateET = findViewById(R.id.crimeDateETInUserPostEdit);
        crimePlaceET = findViewById(R.id.crimePlaceETInUserPostEdit);
        crimeTypeET = findViewById(R.id.crimeTypeETInUserPostEdit);
        crimeDescriptionET = findViewById(R.id.crimeDescETInUserPostEdit);


        indicator = findViewById(R.id.indicatorInUserPostEdit);

        initializeKHUDprogress();

        postDateAndTime = getIntent().getStringExtra("postDateAndTime");
        userName = getIntent().getStringExtra("userName");

        ArrayList<String> stringArrayList = new ArrayList<String>();
        Resources res = getResources();
        Collections.addAll(stringArrayList, res.getStringArray(R.array.crime_type));

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, stringArrayList);

        crimeTypeET.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        crimeTypeET.setThreshold(1);
        crimeTypeET.setAdapter(adapter);

        if (postDateAndTime.length() > 0) {

            retrivePostDataFromSharedpreference();
            setPostData();
            ///Toast.makeText(this, "postDateAndTime : "+postDateAndTime, Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(this, "postDateAndTime not found in Postview activity", Toast.LENGTH_SHORT).show();


    }


    public void initialView() {


        viewPager = findViewById(R.id.imageViewPagerInUserPostEdit);
        NUM_PAGES = imageName.size();

        if (imageName.size() == 0) {

            imageName.add(defaultEmptymage);
            NUM_PAGES = 1;
        }


        customSwipeAdapter = new CustomSwipeAdapterForEditPost(this, imageName, 0, hud, defaultEmptymage);
        viewPager.setAdapter(customSwipeAdapter);

        CirclePageIndicator indicator = findViewById(R.id.indicatorInUserPostEdit);

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
        userPostMCSCOPY = new ArrayList<>();
        userPostMCSCOPY.addAll(userPostMCS);

        if (userPostMCS.size() > 0) {
           /* Toast.makeText(this, "Arraylist Found", Toast.LENGTH_SHORT).show();

            for (int i = 0; i < userPostMCS.size(); i++) {
                Toast.makeText(this, "i = " + Integer.toString(i) + "Value = " + userPostMCS.get(i).getPostDateAndTime(), Toast.LENGTH_SHORT).show();
            }*/
        } else
            Toast.makeText(this, "Arraylist did'nt Found in UserPostEdit  activity", Toast.LENGTH_SHORT).show();

    }

    public void setPostData() {

        int position = 0;

        for (int i = 0; i < userPostMCS.size(); i++) {
            if (userPostMCS.get(i).getPostDateAndTime().equals(postDateAndTime)) {
                position = i;
                crimeDateET.setText(userPostMCS.get(i).getCrimeDate());
                crimeTimeET.setText(userPostMCS.get(i).getCrimeTime());
                crimeDescriptionET.setText(userPostMCS.get(i).getCrimeDesc());
                crimePlaceET.setText(userPostMCS.get(i).getCrimePlace());
                crimeTypeET.setText(userPostMCS.get(i).getCrimeType());
                /// postedByET.setText(userPostMCS.get(i).getUserName());

                getPositionForSeparateDateAndTime();

                //postDateET.setText(postDateAndTime.substring(0, getPositionForSeparateDateAndTime()));
                ///postTimeET.setText(postDateAndTime.substring(getPositionForSeparateDateAndTime()));
                break;
            }
        }

        if (userPostMCS.get(position).getHowManyImage().equals("0") == false) {
            retrivePostImageDataFromServer();
        } else {
            initialView();
        }


    }

    public int getPositionForSeparateDateAndTime() {

        int p = postDateAndTime.indexOf(" ");

        ///Toast.makeText(this, "position : "+Integer.toString(p), Toast.LENGTH_SHORT).show();

        return p;
    }

    public void retrivePostImageDataFromServer() {



        new Thread(new Runnable() {
            public void run() {

                String url = "http://www.farhandroid.com/CrimeLogger/Script/retrivePostImageData.php";

                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(final String response) {
                                hud.dismiss();

                                ///crimeDescriptionTV.setText(response);

                                try {
                                    JSONArray jsonArray = new JSONArray(response);

                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                                        if (jsonObject.has("imageName")) {
                                            imageName.add(jsonObject.getString("imageName"));
                                        }
                                    }

                                    imageNameCOPY.addAll(imageName);

                                    UserPostEdit.this.runOnUiThread(new Runnable() {
                                        public void run() {
                                            initialView();

                                        }
                                    });


                                    //if (jsonArray.length()>0)
                                    /// Toast.makeText(PostViewActivity.this, "Image data found", Toast.LENGTH_SHORT).show();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    hud.dismiss();
                                    showErrorImageInMainThread("Json Exception \n please contact with devloper or try later  " + e.toString());
                                }


                            }
                        }
                        ,
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(final VolleyError error) {
                                hud.dismiss();

                                if (error instanceof TimeoutError || error instanceof NoConnectionError) {

                                    showErrorImageInMainThread("Time out or no connection error  \n Please check connection");

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

                        params.put("postDateAndTime", postDateAndTime);

                        return params;

                    }
                };

                stringRequest.setRetryPolicy(new DefaultRetryPolicy(60000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


                MySingleton.getInstance(UserPostEdit.this).addToRequestQueue(stringRequest);


            }
        }).start();

    }

    public void showErrorImageInMainThread(final String response) {
        hud.dismiss();
        UserPostEdit.this.runOnUiThread(new Runnable() {
            public void run() {
                TastyToast.makeText(getApplicationContext(), response, TastyToast.LENGTH_LONG, TastyToast.ERROR);


            }
        });
    }

    public void initializeKHUDprogress() {
        hud = KProgressHUD.create(UserPostEdit.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setDimAmount(0.6f)
                .setLabel("Please Wait")
                .setCancellable(false);
    }


    public void slectCrimeTimeInPostEdit(View view) {

        Calendar calendar = Calendar.getInstance();

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);


        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                String hour = Integer.toString(hourOfDay);
                String mint = Integer.toString(minute);

                if (hourOfDay < 10)
                    hour = "0" + hour;

                if (minute < 10)
                    mint = "0" + mint;


                crimeTimeET.setText(hour + " : " + mint);


            }
        }, hour, minute, true);


        timePickerDialog.show();

    }

    public void selectCrimeDateInPostEdit(View view) {

        DatePickerDialog datePickerDialog;

        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);


        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                crimeDateET.setText(dayOfMonth + "/"
                        + (month + 1) + "/" + year);

            }
        }, mYear, mMonth, mDay);

        datePickerDialog.getDatePicker().setMaxDate(c.getTimeInMillis());
        datePickerDialog.show();

    }


    public void addImageInUserPOstEditACTVT(View view) {


        FishBun.with(UserPostEdit.this)
                .MultiPageMode()
                .setCamera(true)
                .setActionBarColor(Color.parseColor("#607D8B"),
                        Color.parseColor("#607D8B"),
                        false)
                .setActionBarTitleColor(Color.parseColor("#ffffff"))
                .startAlbum();

    }


    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent imageData) {

        hud.dismiss();

        switch (requestCode) {
            case Define.ALBUM_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    ArrayList<Uri> path = imageData.getParcelableArrayListExtra(Define.INTENT_PATH);


                    ///originalPath.addAll(path);


                    if (imageName.get(0).contains(defaultEmptymage))
                        imageName.remove(0);


                    for (int i = 0; i < path.size(); i++) {
                        String s = path.get(i).toString();
                        s = "`" + s;
                        imageName.add(s);
                    }

                    ////Toast.makeText(this, "onactvt : " + Integer.toString(imageName.size()), Toast.LENGTH_SHORT).show();

                    /// Toast.makeText(this, "original path : "+originalPath.get(0), Toast.LENGTH_LONG).show();

                    customSwipeAdapter.notifyDataSetChanged();
                    ////initializeViewPager();

                }
                break;

        }
    }

    public String getRandomNumber() {
        SecureRandom random = new SecureRandom();
        int num = random.nextInt(1000000000);
        String formatted = String.format("%09d", num);
        return formatted;
    }


    public void updateUserPost(View view) {

        hud.show();

        findToBeDeletedImage();
        ///findImageToBeUpload();
        ///updatePostDataToServer();
        ///showSuccessMsg();

        ///findImageNeedToBeDeletedOrUploadedToServer();


    }


    public void findToBeDeletedImage() {


        deleteImage = new ArrayList<>();
        uploadImage = new ArrayList<>();

        ///Toast.makeText(this, "onupdate : " + Integer.toString(imageNameCOPY.size()), Toast.LENGTH_SHORT).show();


        String name = "";
        for (int i = 0; i < imageNameCOPY.size(); i++) {
            boolean found = false;
            name = imageNameCOPY.get(i);
            for (int j = 0; j < imageName.size(); j++) {

                if (imageName.get(j).contains(name)) {
                    //// Toast.makeText(this, "Found ", Toast.LENGTH_SHORT).show();
                    found = true;
                    break;
                }
            }

            if (found == false) {

                deleteImage.add(name);
            }
        }

        if (deleteImage.size() > 0) {


            new Thread(new Runnable() {
                public void run() {


                    ///Toast.makeText(UserPostEdit.this, "Found name image", Toast.LENGTH_SHORT).show();
                    deleteImageFromServer();


                }
            }).start();

            /// Toast.makeText(UserPostEdit.this, "Found name image", Toast.LENGTH_SHORT).show();

        } else
        {
            ///Toast.makeText(this, "Enter image delete", Toast.LENGTH_LONG).show();



            successInPostImageDelete = true;


            new Thread(new Runnable() {
                public void run() {

                    ///Toast.makeText(UserPostEdit.this, "Found name image", Toast.LENGTH_SHORT).show();
                    findImageToBeUpload();


                }
            }).start();
        }





    }


    public void findImageToBeUpload() {

        String name = "";
        for (int i = 0; i < imageName.size(); i++) {
            boolean found = false;
            name = imageName.get(i);
            for (int j = 0; j < imageNameCOPY.size(); j++) {
                if (imageNameCOPY.get(j).contains(name)) {
                    //// Toast.makeText(this, "Found ", Toast.LENGTH_SHORT).show();
                    found = true;
                    break;
                }
            }

            if (found == false) {

                /// Toast.makeText(this, "Enter", Toast.LENGTH_SHORT).show();

                if (name.charAt(0) == '`' && !name.contains(defaultEmptymage)) {
                    String sbstrng = name.substring(1);
                    Uri uri = Uri.parse(sbstrng);
                    uploadImage.add(uri);
                }
            }
        }

        if (uploadImage.size() > 0) {


            new Thread(new Runnable() {
                public void run() {




                    userpostImageUploadToServer();


                }
            }).start();
        } else
        {

            successInPostImageInsert = true;


            new Thread(new Runnable() {
                public void run() {


                    ///Toast.makeText(UserPostEdit.this, "Found name image", Toast.LENGTH_SHORT).show();
                    //deleteImageFromServer();
                    updatePostDataToServer();


                }
            }).start();


        }



    }





    public void deleteImageFromServer() {
        int i;

        for (i = 0; i < deleteImage.size(); i++) {
            final int position = i;

            String url = "http://www.farhandroid.com/CrimeLogger/Script/deleteUserPostImage.php";


            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(final String response) {




                            UserPostEdit.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    /// Toast.makeText(UserCreatePost.this, "image respone : " + response, Toast.LENGTH_SHORT).show();

                                    if (response.contains("ImageName delete Success") && response.contains("image delete success")) {
                                        successInPostImageDelete = true;

                                        if (position+1==deleteImage.size())
                                            findImageToBeUpload();
                                        ///crimeTypeET.setText("image delete  respone : " + response);
                                        //Toast.makeText(UserCreatePost.this, "Success", Toast.LENGTH_SHORT).show();
                                    } else if (response.contains("ImageName delete  Fail")) {
                                        successInPostImageDelete = false;
                                        showErrorImageInMainThread("Problem in image table Database \n Please contact with devloper or Try again later");
                                    } else if (response.contains("image delete fail")) {
                                        successInPostImageDelete = false;
                                        showErrorImageInMainThread("Problem in image image delete \n Please contact with devloper or Try again later");

                                    } else if (response.contains("deleteImageName empty")) {
                                        showErrorImageInMainThread("Problem in send imagename to server \n Please contact with devloper or Try again later");
                                    }

                                    ///Toast.makeText(UserCreatePost.this, "Failed", Toast.LENGTH_SHORT).show();

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





                    params.put("deleteImageName", deleteImage.get(position));

                    return params;

                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(50000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


            MySingleton.getInstance(this).addToRequestQueue(stringRequest);

        }

    }


    public void userpostImageUploadToServer() {

        int i;

        final ArrayList<String> responseAL=new ArrayList<>();

        MyCommand myCommand=new MyCommand(UserPostEdit.this);

        for (i = 0; i < uploadImage.size(); i++) {
            final int position = i;

            String url = "http://www.farhandroid.com/CrimeLogger/Script/userPostImageUpload.php";


            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(final String response) {


                            responseAL.add(response);

                            if (responseAL.size()==uploadImage.size() && showedSuccessInsertionDialog==false)
                            {
                                successInPostImageInsert=true;
                                ///showedSuccessInsertionDialog=true;
                                updatePostDataToServer();

                            }





                            UserPostEdit.this.runOnUiThread(new Runnable() {
                                public void run() {


                                    ////crimePlaceET.setText("image respone : " + response);

                                    if (response.contains("Image upload success") && response.contains("Data insertion Succes in image table")) {

                                        ////crimePlaceET.setText("image upload respone : " + response);

                                        ///Toast.makeText(UserPostEdit.this, "image upload  response : " + response, Toast.LENGTH_LONG).show();
                                        successInPostImageInsert = true;
                                        updatePostDataToServer();
                                        //Toast.makeText(UserCreatePost.this, "Success", Toast.LENGTH_SHORT).show();
                                    } else if (response.contains("Data insertion Fail in image table")) {
                                        successInPostImageInsert = false;
                                        showErrorImageInMainThread("Problem in image table Database \n Please contact with devloper or Try again later");
                                    } else if (response.contains("Image upload failed")) {
                                        successInPostImageInsert = false;
                                        showErrorImageInMainThread("Problem in image image upload \n Please contact with devloper or Try again later");
                                    }

                                    ///Toast.makeText(UserCreatePost.this, "Failed", Toast.LENGTH_SHORT).show();

                                }
                            });


                        }
                    }
                    ,
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(final VolleyError error) {

                            hud.dismiss();

                            if (responseAL.size()==0 && showedSuccessInsertionDialog == false && showedTimeOutError==false)
                            {
                                showedTimeOutError=true;

                                if (error instanceof TimeoutError || error instanceof NoConnectionError) {

                                    showErrorImageInMainThread("Time out or no connection error image \n Please check connection");

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
                            else
                            {
                                if (showedSuccessInsertionDialog==false)
                                {

                                    updatePostDataToServer();
                                }

                            }



                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams() {

                    Map<String, String> params = new HashMap<>();


                    final int quality;

                    String filepath = getRealPathFromDocumentUri(UserPostEdit.this, uploadImage.get(position));

                    File file = new File(filepath);


                    try {
                        Bitmap bitmap = new Compressor(UserPostEdit.this).setQuality(60)
                                .setMaxWidth(640)
                                .setMaxHeight(480)
                                .compressToBitmap(file);

                        final int bitmapByteCount = BitmapCompat.getAllocationByteCount(bitmap);
                        quality = getImageQuality(bitmapByteCount);

                        params.put("image", ImageToString(bitmap, quality));
                        params.put("imageName", getRandomNumber());
                        params.put("postDateAndTime", postDateAndTime);


                    } catch (IOException e) {
                        ///exceptionInPostImageInsert = true;

                    }


                    return params;

                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(60000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


           myCommand.add(stringRequest);

        }
        myCommand.execute();


    }


    public void updatePostDataToServer() {
      ;



                String url = "http://www.farhandroid.com/CrimeLogger/Script/updateUserPostData.php";

                final StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {




                            @Override
                            public void onResponse(final String response) {




                                if (response.contains("Data Update Success")) {


                                    successInPostDataUpdate = true;

                                    UserPostEdit.this.runOnUiThread(new Runnable() {
                                        public void run() {
                                            showSuccessMsg();
                                        }
                                    });



                                } else if (response.contains("problen in query")) {
                                    hud.dismiss();
                                    showErrorImageInMainThread("Problem in Database \n Please contact with devloper or Try again later");


                                } else if (response.contains("Data Update Fail")) {
                                    hud.dismiss();

                                    showErrorImageInMainThread("User Post Update failed \n please contact with devloper or try later");

                                }




                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

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

                        Map<String, String> params = new HashMap<String, String>();

                        try {



                            params.put("userName", userName);
                            params.put("crimePlace", crimePlaceET.getText().toString());
                            params.put("crimeDate", crimeDateET.getText().toString());
                            params.put("crimeTime", crimeTimeET.getText().toString());
                            params.put("crimeType", crimeTypeET.getText().toString());
                            params.put("crimeDesc", crimeDescriptionET.getText().toString());
                            params.put("postDateAndTime", postDateAndTime);



                            if (imageName.get(0).equals(defaultEmptymage))
                            {
                                params.put("howManyImage", "0");

                            }

                            else {
                                params.put("howManyImage", Integer.toString(imageName.size()));

                            }


                        } catch (Exception e) {

                            showErrorImageInMainThread("exception  : " + e.toString());

                            ///showErrorInMainThread("Userpost Eror in Data insertion \nPlease contact with devloper or Try later");

                        }

                        return params;

                    }
                };

            postRequest.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                MySingleton.getInstance(UserPostEdit.this).addToRequestQueue(postRequest);



    }



    public void showSuccessMsg() {

        UserPostEdit.this.runOnUiThread(new Runnable() {
            public void run() {
                hud.dismiss();
            }
        });



        if (showedSuccessInsertionDialog==false)
        {
            showedSuccessInsertionDialog=true;

            if (successInPostDataUpdate==true && successInPostImageDelete==true && successInPostImageInsert==true) {
                TastyToast.makeText(getApplicationContext(), "Data Update Successfuly", TastyToast.LENGTH_SHORT, TastyToast.SUCCESS);
                RetriveDataFromServer();

            }
            else if(successInPostDataUpdate==false)
            {
                showErrorImageInMainThread("problem in user post data update \nplease contact with devloper or try later");

            }
            else if (successInPostImageDelete==false)
            {
                showErrorImageInMainThread("problem in user post image delete \nplease contact with devloper or try later");
            }
            else if (successInPostImageInsert==false)
            {
                showErrorImageInMainThread("problem in user post image upload\nplease contact with devloper or try later");
            }
        }

    }


    private class Receiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent intent) {
            int positionTobeDeleted = intent.getIntExtra("positionTobeDeleted", 0);


            imageName.remove(positionTobeDeleted);

            if (imageName.size() == 0) {
                initialView();
            } else {
                customSwipeAdapter.notifyDataSetChanged();
            }



        }
    }

    public void onResume() {
        super.onResume();

        IntentFilter filter = new IntentFilter("positionTobeDeleted");
        mReceiver = new Receiver();
        this.registerReceiver(mReceiver, filter);
    }

    public void onPause() {
        super.onPause();
        if (mReceiver != null)
            unregisterReceiver(mReceiver);
    }




    public String ImageToString(Bitmap bitmap, Integer quality) {

        String imgString;

        if (bitmap == null) {
            imgString = "null";
        } else {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream);
            byte[] byteFormat = stream.toByteArray();
            imgString = Base64.encodeToString(byteFormat, Base64.DEFAULT);
        }



        return imgString;

    }


    public static String getRealPathFromDocumentUri(Context context, Uri uri) {
        String filePath = "";

        Pattern p = Pattern.compile("(\\d+)$");
        Matcher m = p.matcher(uri.toString());
        if (!m.find()) {

            return filePath;
        }
        String imgId = m.group();

        String[] column = {MediaStore.Images.Media.DATA};
        String sel = MediaStore.Images.Media._ID + "=?";

        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                column, sel, new String[]{imgId}, null);

        int columnIndex = cursor.getColumnIndex(column[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();

        return filePath;
    }


    public int getImageQuality(int bitmapByteCount) {

        int quality;

        if (bitmapByteCount > 50000000)
            quality = 10;
        else if (bitmapByteCount > 40000000)
            quality = 15;
        else if (bitmapByteCount > 30000000)
            quality = 25;
        else if (bitmapByteCount > 20000000)
            quality = 35;
        else
            quality = 50;

        return quality;
    }


    public void startMainActivity() {

        if (hud!=null)
            hud.dismiss();

        Intent myIntent = new Intent(this, MainActivity.class);
        myIntent.putExtra("cameFromWhichActivity", "UserPostEdit");
        myIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        this.startActivity(myIntent);
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
        finish();
    }

    public void onDestroy() {

        super.onDestroy();
        if (hud!=null)
            hud.dismiss();

    }

    public void onBackPressed() {
        super.onBackPressed();

        startMainActivity();



    }

    public void startUserProfileActivity() {
        Intent myIntent = new Intent(getApplicationContext(), UserProfile.class);
        myIntent.putExtra("cameFromWhichActivity","UserPostEdit");
        myIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        this.startActivity(myIntent);
        overridePendingTransition(R.anim.left_in, R.anim.left_out);
        finish();
    }


    public void RetriveDataFromServer() {



        userPostMCS.clear();
        hud.show();



                String url = "http://www.farhandroid.com/CrimeLogger/Script/retriveUserPostFromDatabase.php";


                JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                        Request.Method.POST, url, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {



                        if (response.length() == 0) {
                            UserPostEdit.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    hud.dismiss();
                                }
                            });

                        }

                        SharedPreferences settings = UserPostEdit.this.getSharedPreferences("PostData", Context.MODE_PRIVATE);
                        settings.edit().clear().commit();


                        for (int i = 0; i < response.length(); i++) {

                            try {
                                JSONObject postInfo = response.getJSONObject(i);


                                //Toast.makeText(MainActivity.this, postInfo.getString("userName")+"\n"+postInfo.getString("crimePlace")+"\n"+postInfo.getString("crimeDate")+"\n"+postInfo.getString("crimeTime")+"\n"+postInfo.getString("crimeType")+"\n"+postInfo.getString("crimeDesc")+"\n"+postInfo.getString("postDateAndTime")+"\n"+postInfo.getString("howManyImage"), Toast.LENGTH_SHORT).show();
                                ///Toast.makeText(MainActivity.this, "  JSONObject postInf : "+Integer.toString(userPostMCS.size()), Toast.LENGTH_SHORT).show();

                                UserPostMC userPostMC = new UserPostMC(postInfo.getString("userName"), postInfo.getString("crimePlace"), postInfo.getString("crimeDate"), postInfo.getString("crimeTime"), postInfo.getString("crimeType"), postInfo.getString("crimeDesc"), postInfo.getString("postDateAndTime"), postInfo.getString("howManyImage"),postInfo.getString("howManyReport"));
                                userPostMCS.add(userPostMC);

                                ///databaseHelper.insertDataInUserPostTable(userPostMC);

                                ///Toast.makeText(MainActivity.this, "i = "+Integer.toString(i)+"\n"+"User post "+Integer.toString(userPostMCS.size()), Toast.LENGTH_SHORT).show();

                                if (i + 1 == response.length()) {

                                    ///Toast.makeText(UserPostEdit.this, "enter array", Toast.LENGTH_SHORT).show();
                                    UserPostEdit.this.runOnUiThread(new Runnable() {
                                        public void run() {

                                            if (hud != null)
                                                hud.dismiss();
                                        }
                                    });
                                    setSharedPrefference();
                                    ///retrivePostDataFromSharedpreference();
                                    ///updateRecyclerView(userPostMCS);
                                    ///adapter.notifyDataSetChanged();

                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                                hud.dismiss();

                                UserPostEdit.this.runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(UserPostEdit.this, "Json Exception", Toast.LENGTH_SHORT).show();
                                    }
                                });


                            }

                        }


                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(final VolleyError error) {
                        hud.dismiss();

                        UserPostEdit.this.runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(UserPostEdit.this, "Volley Error : " + error.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });


                    }
                }
                );

                jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(60000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


                MySingleton.getInstance(UserPostEdit.this).addToRequestQueue(jsonArrayRequest);


    }


    public void setSharedPrefference() {



        SharedPreferences.Editor editor = getSharedPreferences("PostData", MODE_PRIVATE).edit();
        Gson gson = new Gson();
        ArrayList<UserPostMC> userPostMCSP = new ArrayList<>();
        userPostMCSP.addAll(userPostMCS);
        String jsonText = gson.toJson(userPostMCSP);
        editor.putString("PostData", jsonText);
        editor.commit();

        startUserProfileActivity();

    }




}
