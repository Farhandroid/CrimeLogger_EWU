package tanvir.crimelogger_ewu.Activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
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
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import com.android.volley.toolbox.StringRequest;
import com.sangcomz.fishbun.FishBun;
import com.sangcomz.fishbun.define.Define;
import com.sdsmdg.tastytoast.TastyToast;
import com.viewpagerindicator.CirclePageIndicator;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import id.zelory.compressor.Compressor;
import tanvir.crimelogger_ewu.HelperClass.AppController;
import tanvir.crimelogger_ewu.HelperClass.CustomSwipeAdapterUserCreatePost;
import tanvir.crimelogger_ewu.HelperClass.MyCommand;
import tanvir.crimelogger_ewu.HelperClass.MySingleton;
import tanvir.crimelogger_ewu.HelperClass.ProgressDialog;
import tanvir.crimelogger_ewu.R;

public class UserCreatePost extends AppCompatActivity {

    private EditText  crimeDateET, crimeTimeET, crimeDescriptionET;
    private MultiAutoCompleteTextView crimeTypeET;
    private AutoCompleteTextView crimePlaceET;
    private String postDateAndTime;
    private ArrayList<Uri> originalPath = new ArrayList<>();
    private Uri defaultEmptymage;
    private ViewPager viewPager;
    private static int NUM_PAGES = 0;
    private CustomSwipeAdapterUserCreatePost customSwipeAdapteraUserCreatePost;
    private CirclePageIndicator indicator;
    private Boolean successInPostDataInsert = false;
    private Boolean successInPostImageInsert = false;
    private Boolean exceptionInPostImageInsert = false;
    private Boolean exceptionInPostDataInsert = false;
    private Boolean showedSuccessInsertionDialog = false;
    private Boolean showedTimeOutError = false;
    private String userType = "";
    private String userName;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_create_post);
        crimeTimeET = findViewById(R.id.crimeTimeET);
        crimeDateET = findViewById(R.id.crimeDateET);
        crimePlaceET = findViewById(R.id.crimePlaceET);
        crimeTypeET = findViewById(R.id.crimeTypeET);
        crimeDescriptionET = findViewById(R.id.crimeDescET);
        viewPager = findViewById(R.id.imageViewPager);
        indicator = findViewById(R.id.indicator);
        progressDialog=new ProgressDialog(UserCreatePost.this);
        setCrimeTypeAutocomplete();
        setCrimePlaceAutoComplete();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userType = extras.getString("userType");
            if (userType.contains("user")) {
                userName = extras.getString("userName");
            } else
                userName = "Anonymous";
            ///Toast.makeText(this, "userName : "+userName, Toast.LENGTH_SHORT).show();
        } else {
            TastyToast.makeText(getApplicationContext(), "Data is null in UserCreatePostActivity \n please contact with devloper or try later", TastyToast.LENGTH_LONG, TastyToast.ERROR);
        }

        initializeViewPager();
    }

    public void initializeViewPager() {
        NUM_PAGES = originalPath.size();
        if (originalPath.size() == 0) {
            Uri uri = Uri.parse(Integer.toString(R.drawable.empty_image));
            defaultEmptymage = uri;
            originalPath.add(uri);
            NUM_PAGES = 1;
        } else if (originalPath.get(0).equals(defaultEmptymage)) {
            originalPath.remove(0);
        }
        customSwipeAdapteraUserCreatePost = new CustomSwipeAdapterUserCreatePost(this, originalPath, defaultEmptymage, NUM_PAGES);
        viewPager.setAdapter(customSwipeAdapteraUserCreatePost);
        indicator.setViewPager(viewPager);
        final float density = getResources().getDisplayMetrics().density;
        indicator.setRadius(5 * density);
    }

    public void slectCrimeTime(View view) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String mint = Integer.toString(minute);
                String format;
                if (hourOfDay == 0) {
                    hourOfDay += 12;
                    format = "AM";
                }
                else if (hourOfDay == 12) {
                    format = "PM";
                }
                else if (hourOfDay > 12) {
                    hourOfDay -= 12;
                    format = "PM";
                }
                else {
                    format = "AM";
                }
                String hour = Integer.toString(hourOfDay);
                crimeTimeET.setText(hour + " : " + mint+" "+format);
            }
        }, hour, minute, false);
        timePickerDialog.show();
    }

    public void selectCrimeDate(View view) {

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
    public void addImageInUserPOstACTVT(View view) {
        //progressDialog.showProgressDialog();
        FishBun.with(UserCreatePost.this)
                .MultiPageMode()
                .setCamera(true)
                .setActionBarColor(Color.parseColor("#607D8B"),
                        Color.parseColor("#607D8B"),
                        false)
                .setActionBarTitleColor(Color.parseColor("#ffffff"))
                .startAlbum();
    }
    public String getCurrentDateAndTime() {
        Calendar c = Calendar.getInstance();
        java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("dd-MM-yyyy HH:mm:s");
        String formattedDate = df.format(c.getTime());
        return formattedDate;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent imageData) {
        /*if (progressDialog.getAlertDialog()!=null)
                progressDialog.hideProgressDialog();*/
        switch (requestCode) {
            case Define.ALBUM_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    ArrayList<Uri> path = imageData.getParcelableArrayListExtra(Define.INTENT_PATH);
                    originalPath.addAll(path);
                    initializeViewPager();
                }
                break;
        }
    }

    public void createUserPost(View view) {
        String crimePlace = crimePlaceET.getText().toString();
        String crimeDate = crimeDateET.getText().toString();
        String crimeTime = crimeTimeET.getText().toString();
        String crimeType = crimeTypeET.getText().toString();
        String crimeDescription = crimeDescriptionET.getText().toString();
        if (crimePlace.length() > 0  && crimeType.length() > 0 && crimeDescription.length() > 0) {
            crimeDescription = crimeDescription.replaceAll("'", "");
            sendUserPostDataToServer();
        } else {
                    Toast.makeText(UserCreatePost.this, "Please fill up all field", Toast.LENGTH_SHORT).show();
        }

    }

    public void startMainActivity() {
        Intent myIntent = new Intent(this, MainActivity.class);
        myIntent.putExtra("cameFromWhichActivity", "UserCreatePost");
        myIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        this.startActivity(myIntent);
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
        finish();
    }

    public void userpostImageUploadToServer() {
        int i;
        final ArrayList<String> responseAL = new ArrayList<>();
        Bitmap bitmap = null;
        MyCommand myCommand = new MyCommand(UserCreatePost.this);

        for (i = 0; i < originalPath.size(); i++) {
            final int position = i;
            int quality = 0;
            String filepath = getRealPathFromDocumentUri(UserCreatePost.this, originalPath.get(position));
            File file = new File(filepath);
            try {
                bitmap = new Compressor(UserCreatePost.this).setQuality(60)
                        .setMaxWidth(640)
                        .setMaxHeight(480)
                        .compressToBitmap(file);

                final int bitmapByteCount = BitmapCompat.getAllocationByteCount(bitmap);
                quality = getImageQuality(bitmapByteCount);
            } catch (IOException e) {
                exceptionInPostImageInsert = true;

            }
            String url = "http://www.farhandroid.com/CrimeLogger/Script/userPostImageUpload.php";
            final Bitmap finalBitmap = bitmap;
            final int finalQuality = quality;
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(final String response) {
                                    responseAL.add(response);
                                    if (responseAL.size() == originalPath.size() && showedSuccessInsertionDialog == false) {
                                        successInPostImageInsert = true;
                                        showDataInsertionResult();
                                    }
                                    if (response.contains("Image upload success") && response.contains("Data insertion Succes in image table")) {
                                        successInPostImageInsert = true;
                                    } else if (response.contains("Data insertion Fail in image table")) {
                                        successInPostImageInsert = false;
                                        showErrorInMainThread("Problem in image table Database \n Please contact with devloper or Try again later");
                                    } else if (response.contains("Image upload failed")) {
                                        successInPostImageInsert = false;
                                        showErrorInMainThread("Problem in image image upload \n Please contact with devloper or Try again later");
                                    }
                        }
                    }
                    ,
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(final VolleyError error) {
                          progressDialog.hideProgressDialog();
                            deleteUserPostData();
                            if (responseAL.size() == 0 && showedSuccessInsertionDialog == false && showedTimeOutError == false) {
                                showedTimeOutError = true;
                                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                                    showErrorInMainThread("Time out or no connection error image \n Please check connection");

                                } else if (error instanceof AuthFailureError) {
                                    showErrorInMainThread("Authentication failure error \n Please contact with devloper or try later");
                                } else if (error instanceof ServerError) {
                                    showErrorInMainThread("Server error\n Please contact with devloper or Try later");

                                } else if (error instanceof NetworkError) {
                                    showErrorInMainThread("Network error\n Please contact with devloper or Try later");
                                } else if (error instanceof ParseError) {
                                    showErrorInMainThread("Parse error\n Please contact with devloper or Try later");

                                }
                            } else {
                                if (showedSuccessInsertionDialog == false) {
                                    showDataInsertionResult();
                                }
                            }
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams() {

                    Map<String, String> params = new HashMap<>();
                    params.put("image", ImageToString(finalBitmap, finalQuality));
                    params.put("imageName", getRandomNumber());
                    params.put("postDateAndTime", postDateAndTime);
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

    public String getRandomNumber() {
        SecureRandom random = new SecureRandom();
        int num = random.nextInt(1000000000);
        String formatted = String.format("%09d", num);
        return formatted;
    }

    public void sendUserPostDataToServer() {
        String Type = crimeTypeET.getText().toString();
        Type = Type.trim();
        if (Type.charAt(Type.length() - 1) == ',') {
            Type = Type.substring(0, Type.length() - 1);
        }
        final String crimeType = Type;
        progressDialog.showProgressDialog();
        new Thread(new Runnable() {
            public void run() {
                String url = "http://www.farhandroid.com/CrimeLogger/Script/insertDataInUserPostTable.php";
                final StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(final String response) {
                                if (response.contains("Data insertion Success")) {
                                    if (originalPath.size() == 1 && originalPath.get(0).equals(defaultEmptymage)) {
                                        UserCreatePost.this.runOnUiThread(new Runnable() {
                                            public void run() {
                                                progressDialog.hideProgressDialog();
                                                TastyToast.makeText(getApplicationContext(), "Post Success", TastyToast.LENGTH_LONG, TastyToast.SUCCESS);
                                                startMainActivity();
                                            }
                                        });
                                    }
                                    successInPostDataInsert = true;

                                } else if (response.contains("problen in query")) {
                                    progressDialog.hideProgressDialog();
                                    showErrorInMainThread("Problem in Database \n Please contact with devloper or Try again later");
                                } else if (response.contains("Data insertion Fail")) {
                                    progressDialog.hideProgressDialog();
                                    showErrorInMainThread("User Post failed \n please contact with devloper or try later");
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                progressDialog.hideProgressDialog();
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
                        Map<String, String> params = new HashMap<String, String>();
                        try {
                            postDateAndTime = getCurrentDateAndTime();
                            params.put("userName", userName);
                            params.put("crimePlace", crimePlaceET.getText().toString());
                            if (crimeDateET.getText().toString().length()>0)
                            {
                                params.put("crimeDate", crimeDateET.getText().toString());
                            }
                            else
                                params.put("crimeDate", "Unknown");

                            if (crimeTimeET.getText().toString().length()>0)
                            {
                                params.put("crimeTime", crimeTimeET.getText().toString());
                            }
                            else
                                params.put("crimeTime", "Unknown");

                            params.put("crimeType", crimeType);
                            params.put("crimeDesc", crimeDescriptionET.getText().toString().replaceAll("'", ""));
                            params.put("postDateAndTime", postDateAndTime);

                            if (originalPath.get(0).equals(defaultEmptymage))
                                params.put("howManyImage", "0");
                            else {
                                params.put("howManyImage", Integer.toString(originalPath.size()));
                                userpostImageUploadToServer();
                            }

                        } catch (Exception e) {
                            showErrorInMainThread("exception  : " + e.toString());
                        }

                        return params;
                    }
                };

                postRequest.setRetryPolicy(new DefaultRetryPolicy(60000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                AppController.getInstance().addToRequestQueue(postRequest);
            }
        }).start();
    }

    public void showErrorInMainThread(final String response) {
        progressDialog.hideProgressDialog();
    }

    public void showDataInsertionResult() {
        progressDialog.hideProgressDialog();
        if (showedSuccessInsertionDialog == false) {
            UserCreatePost.this.runOnUiThread(new Runnable() {
                public void run() {
                    showedSuccessInsertionDialog = true;
                    if (successInPostDataInsert == true && successInPostImageInsert == true) {
                        startMainActivity();
                        TastyToast.makeText(getApplicationContext(), "Post Succcess !", TastyToast.LENGTH_LONG, TastyToast.SUCCESS);
                    } else if (successInPostImageInsert == false) {
                        TastyToast.makeText(getApplicationContext(), "Eror in Image upload in show \nPlease contact with devloper or Try later\n", TastyToast.LENGTH_LONG, TastyToast.WARNING);
                    } else if (successInPostDataInsert == false) {
                        TastyToast.makeText(getApplicationContext(), "Eror in Data insertion in show \nPlease contact with devloper or Try later\n", TastyToast.LENGTH_LONG, TastyToast.WARNING);
                    } else
                        TastyToast.makeText(getApplicationContext(), "else in show result", TastyToast.LENGTH_LONG, TastyToast.WARNING);
                }
            });
        }

    }



    public void onBackPressed() {
        super.onBackPressed();
        startMainActivity();
    }


    public void deleteUserPostData() {
        String url = "http://www.farhandroid.com/CrimeLogger/Script/deleteUserPost.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String response) {
                        UserCreatePost.this.runOnUiThread(new Runnable() {
                            public void run() {
                                if (response.contains("Post delete Success")) {
                                } else if (response.contains("Post delete  Fail")) {
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
                        progressDialog.hideProgressDialog();
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
        MySingleton.getInstance(UserCreatePost.this).addToRequestQueue(stringRequest);
    }

    public void setCrimeTypeAutocomplete()
    {
        ArrayList<String> stringArrayList = new ArrayList<String>();
        Resources res = getResources();
        Collections.addAll(stringArrayList, res.getStringArray(R.array.crime_type));
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, stringArrayList);
        crimeTypeET.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        crimeTypeET.setThreshold(1);
        crimeTypeET.setAdapter(adapter);
    }

    public void setCrimePlaceAutoComplete()
    {
        ArrayList<String> stringArrayList = new ArrayList<String>();
        Resources res = getResources();
        Collections.addAll(stringArrayList, res.getStringArray(R.array.place_arrays));
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, stringArrayList);
        crimePlaceET.setThreshold(1);
        crimePlaceET.setAdapter(adapter);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("entercreate","entercreate");
        if (progressDialog.getAlertDialog()!=null)
            progressDialog.hideProgressDialog();
    }

    @Override
    public void onResume(){
        super.onResume();
        progressDialog=new ProgressDialog(UserCreatePost.this);
        Log.d("enterres","enterres");
        if (progressDialog.getAlertDialog()!=null)
            progressDialog.hideProgressDialog();
    }
}
