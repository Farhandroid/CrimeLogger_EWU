package tanvir.crimelogger_aust.Activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.BitmapCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

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
import com.kaopiz.kprogresshud.KProgressHUD;
import com.sangcomz.fishbun.FishBun;
import com.sangcomz.fishbun.define.Define;
import com.sdsmdg.tastytoast.TastyToast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;
import tanvir.crimelogger_aust.HelperClass.AppController;
import tanvir.crimelogger_aust.R;

public class UserRegistration extends AppCompatActivity {


    private CircleImageView circleImageView;

    private Bitmap bitmap;

    private String name, email, phoneNumber, isPCavailable, userName;

    private KProgressHUD hud;

    private int profilePicCounter = 0;

    private ArrayList<Uri> imagePath;

    private EditText userNameET, emailET, passwordET, nameET, phoneNumberET;
    private Button button;

    private String nameCopy, phoneCopy, emailCopy;

    private TextInputLayout textInputLayout;


    private String cameFromWhichActivity;

    private String[] permissions = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration);


        circleImageView = findViewById(R.id.profile_image);

        userNameET = findViewById(R.id.userNameInSignUpActivity);
        emailET = findViewById(R.id.emailInSignUP);
        passwordET = findViewById(R.id.passwordInSignUP);
        nameET = findViewById(R.id.nameInSignUpActivity);
        phoneNumberET = findViewById(R.id.phoneNumberInSignUpActivity);

        textInputLayout = findViewById(R.id.passwordLayoutInSignUpPage);

        button = findViewById(R.id.signUpBTN);

        initializeKHUDprogress();

        imagePath = new ArrayList<>();

        Intent extras = getIntent();

        if (extras != null) {

            cameFromWhichActivity = extras.getStringExtra("cameFromWhichActivity");


            if (cameFromWhichActivity != null) {
                if (cameFromWhichActivity.contains("userProfile")) {

                    checkLoginSharedPrefference();
                    setDataIfDataNeedToBeUpdated();



                }

            }

        }


        checkPermissions();
    }


    public void showImagePickerDialog(View view) {


        FishBun.with(UserRegistration.this).MultiPageMode().setCamera(true).
                setMaxCount(1).
                setActionBarColor(Color.parseColor("#607D8B"), Color.parseColor("#607D8B"), false)
                .setActionBarTitleColor(Color.parseColor("#ffffff")).startAlbum();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageData) {


        switch (requestCode) {
            case Define.ALBUM_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    imagePath = imageData.getParcelableArrayListExtra(Define.INTENT_PATH);
                    Glide.with(UserRegistration.this).load(imagePath.get(0)).into(circleImageView);
                    profilePicCounter = 1;

                    break;
                }
            default:
                profilePicCounter = 0;
                ///TastyToast.makeText(getApplicationContext(), "Image Cancelled ", TastyToast.LENGTH_SHORT, TastyToast.INFO);
                break;

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


    public void insertUserRegistrationDataToServer(View view) {

        String email = emailET.getText().toString();
        String phone = phoneNumberET.getText().toString();

        if (nameET.getText().toString().length() > 0 && email.length() > 0 && phone.length() > 0) {

            if (email.contains("@") && email.contains(".com")) {

                if (phone.length() == 11 && (phone.startsWith("017") || phone.startsWith("018") || phone.startsWith("019") || phone.startsWith("016") || phone.startsWith("015"))) {
                    if (cameFromWhichActivity.contains("userProfile")) {
                        if (checkDataNedToBeUpdatedOrNot() == true) {
                            insertUserDataToServer();
                        } else {
                            TastyToast.makeText(getApplicationContext(), "No change found", TastyToast.LENGTH_LONG, TastyToast.WARNING);
                        }
                    } else {
                        ///Toast.makeText(this, "cameFromWhichActivity : " + cameFromWhichActivity, Toast.LENGTH_SHORT).show();
                        insertUserDataToServer();
                    }
                } else {
                    TastyToast.makeText(getApplicationContext(), "Please insert valid phone number .", TastyToast.LENGTH_LONG, TastyToast.WARNING);

                }


            } else {

                TastyToast.makeText(getApplicationContext(), "Please insert valid email .", TastyToast.LENGTH_LONG, TastyToast.WARNING);

            }


        } else {
            TastyToast.makeText(getApplicationContext(), "Please fill up all field !", TastyToast.LENGTH_LONG, TastyToast.WARNING);

        }


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

    public void startLoginActivity(View view) {
        startLoginActivityAfterReg();

    }


    public void startLoginActivityAfterReg() {
        Intent myIntent = new Intent(getApplicationContext(), UserLogin.class);
        myIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        this.startActivity(myIntent);
        overridePendingTransition(R.anim.left_in, R.anim.left_out);
        finish();
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
            quality = 8;
        else if (bitmapByteCount > 40000000)
            quality = 13;
        else if (bitmapByteCount > 30000000)
            quality = 23;
        else if (bitmapByteCount > 20000000)
            quality = 33;
        else
            quality = 48;

        return quality;
    }

    public void showErrorImageInMainThread(final String response) {
        UserRegistration.this.runOnUiThread(new Runnable() {
            public void run() {
                TastyToast.makeText(getApplicationContext(), response, TastyToast.LENGTH_LONG, TastyToast.ERROR);


            }
        });
    }

    public void initializeKHUDprogress() {
        hud = KProgressHUD.create(UserRegistration.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setDimAmount(0.6f)
                .setLabel("Please Wait")
                .setCancellable(false);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (cameFromWhichActivity.contains("userProfile")) {
            if (hud != null)
                hud.dismiss();

            startUserProfileActivity();

        } else
            startMainActivity();


    }

    public void startMainActivity() {
        Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
        myIntent.putExtra("cameFromWhichActivity", "UserRegistration");
        myIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        this.startActivity(myIntent);
        overridePendingTransition(R.anim.left_in, R.anim.left_out);
        finish();

    }

    public void checkLoginSharedPrefference() {
        SharedPreferences prefs = getSharedPreferences("logginInformation", MODE_PRIVATE);
        String isLogged = prefs.getString("isLogged?", null);


        if (isLogged != null && isLogged.contains("yes")) {

            userName = prefs.getString("userName", null);
            name = prefs.getString("name", null);
            phoneNumber = prefs.getString("phoneNumber", null);
            email = prefs.getString("email", null);
            isPCavailable = prefs.getString("isPCavailable", null);


        } else {
            TastyToast.makeText(getApplicationContext(), "Logged in shared prefference data not found \n please contact with devloper or try later", TastyToast.LENGTH_LONG, TastyToast.ERROR);
        }
    }

    public void setDataIfDataNeedToBeUpdated() {
        nameET.setText(name);
        emailET.setText(email);
        phoneNumberET.setText(phoneNumber);


        nameCopy = name;
        emailCopy = email;
        phoneCopy = phoneNumber;

        textInputLayout.setVisibility(View.GONE);
        userNameET.setVisibility(View.GONE);
        passwordET.setVisibility(View.GONE);
        button.setText("Update information");

        LinearLayout linearLayout = findViewById(R.id.memberLogin);
        linearLayout.setVisibility(View.GONE);

        if (isPCavailable.contains("1")) {
            hud.show();

            String url = "http://www.farhandroid.com/CrimeLogger/Script/UserProfilePic/" + userName + ".jpg";

            RequestOptions options = new RequestOptions();
            options.centerCrop();
            options.placeholder(R.drawable.empty_profile);
            options.signature(new ObjectKey(System.currentTimeMillis()));

            Glide.with(UserRegistration.this)
                    .load(url)
                    .apply(options)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {

                            hud.dismiss();
                            TastyToast.makeText(getApplicationContext(), "Problem in Image retrieve \n Please try later", TastyToast.LENGTH_LONG, TastyToast.ERROR);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                            hud.dismiss();
                            return false;
                        }
                    })
                    .into(circleImageView);
        }
    }


    public void setLoggedInSharedPrefference() {
        SharedPreferences.Editor editor = getSharedPreferences("logginInformation", MODE_PRIVATE).edit();
        editor.putString("isLogged?", "yes");
        editor.putString("userName", userName);
        editor.putString("name", nameET.getText().toString());
        editor.putString("phoneNumber", phoneNumberET.getText().toString());
        editor.putString("email", emailET.getText().toString());


        editor.putString("isPCavailable", isPCavailable);


        editor.apply();
    }

    public void startUserProfileActivity() {
        Intent myIntent = new Intent(getApplicationContext(), UserProfile.class);
        myIntent.putExtra("cameFromWhichActivity", "UserRegistration");
        myIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        this.startActivity(myIntent);
        overridePendingTransition(R.anim.left_in, R.anim.left_out);
        finish();
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        hud.dismiss();

    }

    public boolean checkDataNedToBeUpdatedOrNot() {
        if (nameCopy.equals(nameET.getText().toString()) && phoneCopy.equals(phoneNumberET.getText().toString()) && emailCopy.equals(emailET.getText().toString()) && profilePicCounter == 0) {
            return false;
        } else
            return true;
    }

    public void insertUserDataToServer() {


        hud.show();




        String url = "http://www.farhandroid.com/CrimeLogger/Script/userRegistration.php";


        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String response) {

                        hud.dismiss();


                        ///TastyToast.makeText(getApplicationContext(), "response :  " + response, TastyToast.LENGTH_SHORT, TastyToast.SUCCESS);


                        if (response.contains("Data insertion Fail")) {
                            showErrorImageInMainThread("Problem in Database \n Please contact with devloper or Try again later");

                        } else if (response.contains("Image upload failed")) {
                            showErrorImageInMainThread("Problem in Image Upload \n Please contact with devloper or Try again later");
                        } else if (response.contains("User alredy exist")) {
                            showErrorImageInMainThread("User alredy exist");
                        } else if (response.contains("Update Fail")) {
                            showErrorImageInMainThread("Problem in Update information \n Please contact with devloper or Try again later");
                        } else if (response.contains("Update Success")) {
                            setLoggedInSharedPrefference();

                            TastyToast.makeText(getApplicationContext(), "Information update Success ", TastyToast.LENGTH_SHORT, TastyToast.SUCCESS);
                            startUserProfileActivity();


                        } else if (response.contains("Email already taken by a user"))

                        {
                            showErrorImageInMainThread("This email is already taken by a user \n Please use another email");
                        } else
                        {
                            TastyToast.makeText(getApplicationContext(), "Registration Success ", TastyToast.LENGTH_SHORT, TastyToast.SUCCESS);
                            startLoginActivityAfterReg();

                        }

                    }
                },
                new Response.ErrorListener()

                {
                    @Override
                    public void onErrorResponse(final VolleyError error) {

                        hud.dismiss();

                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {

                            showErrorImageInMainThread("Time out or no connection error \n Please check connection" + error.toString());

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
        )

        {
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<String, String>();


                int quality = 0;

                params.put("name", nameET.getText().toString());
                params.put("email", emailET.getText().toString());
                params.put("phoneNumber", phoneNumberET.getText().toString());

                if (imagePath.size() > 0) {

                    String filepath = getRealPathFromDocumentUri(UserRegistration.this, imagePath.get(0));

                    File file = new File(filepath);


                    try {
                        bitmap = new Compressor(UserRegistration.this).setQuality(70)
                                .setMaxWidth(320)
                                .setMaxHeight(240)
                                .compressToBitmap(file);
                    } catch (IOException e1) {
                        hud.dismiss();
                        showErrorImageInMainThread("Eror in bitmap\nPlease contact with devloper or Try later");
                    }

                    final int bitmapByteCount = BitmapCompat.getAllocationByteCount(bitmap);
                    quality = getImageQuality(bitmapByteCount);


                }


                if (cameFromWhichActivity != null) {
                    if (cameFromWhichActivity.contains("userProfile")) {




                        params.put("update", "yes");
                        params.put("userName", userName);

                        if (profilePicCounter == 0 && isPCavailable.contains("0")) {

                            params.put("isPCavailable", "0");
                        } else {
                            isPCavailable = "1";
                            params.put("isPCavailable", "1");
                        }

                        if (imagePath.size() > 0)
                            params.put("image", ImageToString(bitmap, quality));


                    } else {


                        params.put("userName", userNameET.getText().toString());
                        params.put("update", "no");
                        params.put("password", passwordET.getText().toString());
                        params.put("image", ImageToString(bitmap, quality));

                        if (profilePicCounter == 0) {
                            params.put("isPCavailable", "0");
                        } else {
                            params.put("isPCavailable", "1");
                        }
                    }
                } else
                    showErrorImageInMainThread("camefrom which activity null \nPlease contact with devloper or Try later");


                return params;

            }
        };

        postRequest.setRetryPolicy(new

                DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));




        AppController.getInstance().

                addToRequestQueue(postRequest);


    }


}
