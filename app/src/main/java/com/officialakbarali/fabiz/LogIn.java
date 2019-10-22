package com.officialakbarali.fabiz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.officialakbarali.fabiz.blockPages.AppVersion;
import com.officialakbarali.fabiz.blockPages.ForcePull;
import com.officialakbarali.fabiz.network.VolleyRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static com.officialakbarali.fabiz.data.CommonInformation.SET_DECIMAL_LENGTH;
import static com.officialakbarali.fabiz.data.MyAppVersion.GET_MY_APP_VERSION;

public class LogIn extends AppCompatActivity {
    Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        Button logInBtn = findViewById(R.id.log_in_btn);
        logInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText usernameE, passwordE;
                usernameE = findViewById(R.id.log_in_usr);
                passwordE = findViewById(R.id.log_in_pass);

                String usernameString = usernameE.getText().toString().trim();
                String passwordString = passwordE.getText().toString().trim();

                if (usernameString.matches("")) {
                    showToast("Please enter username");
                } else {
                    if (passwordString.matches("")) {
                        showToast("Please eneter password");
                    } else {
                        performLogIn(usernameString, passwordString);
                    }
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean appVersionProblem = sharedPreferences.getBoolean("version", false);
        if (appVersionProblem) {
            Intent versionIntent = new Intent(this, AppVersion.class);
            versionIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(versionIntent);
            overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
        } else {
            setUpStartAnimation();
        }

    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void performLogIn(final String username, final String password) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("app_version", "" + GET_MY_APP_VERSION());
        hashMap.put("my_username", "" + username);
        hashMap.put("my_password", "" + password);

        final VolleyRequest volleyRequest = new VolleyRequest("login.php", hashMap, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("Response :", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("success")) {
                        proceed(username, password, jsonObject.getInt("precision"), jsonObject.getInt("idOfStaff"));
                    } else {
                        if (jsonObject.getString("status").equals("VERSION")) {
                            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(LogIn.this);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean("version", true);
                            editor.apply();
                            Intent versionIntent = new Intent(LogIn.this, AppVersion.class);
                            versionIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(versionIntent);
                            overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                        } else if (jsonObject.getString("status").equals("USER")) {
                            showToast("Invalid username or password");
                        } else {
                            showToast("Something went wrong");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    showToast("Bad Response From Server");
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof ServerError) {
                    showToast("Server Error");
                } else if (error instanceof TimeoutError) {
                    showToast("Connection Timed Out");
                } else if (error instanceof NetworkError) {
                    showToast("Bad Network Connection");
                }
            }
        });
        requestQueue.add(volleyRequest);
    }

    private void proceed(String username, String password, int precision, int idOfStaff) {
        final SharedPreferences
                sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("my_username", username);
        editor.putString("my_password", password);
        editor.putBoolean("update_data", false);
        editor.putBoolean("force_pull", true);
        editor.putInt("precision", precision);
        editor.putInt("idOfStaff", idOfStaff);

        final Intent mainHomeIntent = new Intent(LogIn.this, ForcePull.class);
        mainHomeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

        LinearLayout precisionContainer = findViewById(R.id.precision_cont);
        LinearLayout logInContainer = findViewById(R.id.log_in_cont);
        logInContainer.setVisibility(View.GONE);
        precisionContainer.setVisibility(View.VISIBLE);

        Button pre2 = findViewById(R.id.pre_2);
        pre2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putInt("decimal_precision", 2);
                SET_DECIMAL_LENGTH(2);
                editor.apply();
                startActivity(mainHomeIntent);
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
            }
        });
        Button pre3 = findViewById(R.id.pre_3);
        pre3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putInt("decimal_precision", 3);
                SET_DECIMAL_LENGTH(3);
                editor.apply();
                startActivity(mainHomeIntent);
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
            }
        });
    }

    private void showToast(String msgForToast) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(this, msgForToast, Toast.LENGTH_LONG);
        toast.show();
    }

    private void setUpStartAnimation() {
        TextView head, belowHead;
        EditText username, password;

        Button logIn = findViewById(R.id.log_in_btn);

        LinearLayout last = findViewById(R.id.last_container);

        head = findViewById(R.id.log_in_head);
        belowHead = findViewById(R.id.log_in_below_head);

        username = findViewById(R.id.log_in_usr);
        password = findViewById(R.id.log_in_pass);


        YoYo.with(Techniques.ZoomInLeft).duration(1000).repeat(0).playOn(head);
        YoYo.with(Techniques.ZoomInLeft).duration(1100).repeat(0).playOn(belowHead);

        YoYo.with(Techniques.ZoomInLeft).duration(1200).repeat(0).playOn(username);
        YoYo.with(Techniques.ZoomInLeft).duration(1300).repeat(0).playOn(password);

        YoYo.with(Techniques.ZoomInLeft).duration(1400).repeat(0).playOn(logIn);

        YoYo.with(Techniques.ZoomInLeft).duration(1500).repeat(0).playOn(last);
    }
}
