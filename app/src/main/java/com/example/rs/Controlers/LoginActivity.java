package com.example.rs.Controlers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import com.example.rs.R;
import com.example.rs.UtilsService.SharedPreferenceClass;
import com.example.rs.UtilsService.UtilService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private Button registerBtn;
    private Button loginBtn;
    private EditText reg_email, reg_password;
    private String email, password;
    UtilService utilService;
    SharedPreferenceClass sharedPreferenceClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        registerBtn = findViewById(R.id.registerBtn);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        loginBtn = findViewById(R.id.loginBtn);
        reg_email = findViewById(R.id.reg_email);
        reg_password = findViewById(R.id.reg_password);
        utilService = new UtilService();

        sharedPreferenceClass = new SharedPreferenceClass(this);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                utilService.hideKeyboard(view, LoginActivity.this);
                email = reg_email.getText().toString();
                password = reg_password.getText().toString();
                if(validate(view)) {
                    loginUser(view);
                }
            }
        });
    }

    private void loginUser(View view) {

        final HashMap<String, String> params = new HashMap<>();
        params.put("email", email);
        params.put("password", password);

        String apiKey = "http://10.0.2.2:4000/api/RS/auth/login";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                apiKey, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getBoolean("success")) {
                        String token = response.getString("token");
                        String userId = response.getString("userId");
                        sharedPreferenceClass.setValue_string("token", token);
                        sharedPreferenceClass.setValue_string("userId", userId);
                        Toast.makeText(LoginActivity.this, "Úspešné prihlásenie", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse response = error.networkResponse;
                if (error instanceof ServerError && response != null) {
                    try {
                        String res = new String(response.data, HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                        JSONObject obj = new JSONObject(res);
                        Toast.makeText(LoginActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                    } catch (JSONException | UnsupportedEncodingException je) {
                        je.printStackTrace();
                    }
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        int socketTime = 10000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTime,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(policy);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }

    private boolean validate(View view) {
        boolean isValid;

        if(!TextUtils.isEmpty(email)) {
            if(!TextUtils.isEmpty(password)) {
                isValid = true;
            } else {
                utilService.showSnackBar(view,"zadajte heslo prosím...");
                isValid = false;
            }
        } else {
            utilService.showSnackBar(view,"zadajte e-mail prosím...");
            isValid = false;
        }

        return  isValid;
    }

    protected void onStart(){
        super.onStart();

        SharedPreferences rs_pref = getSharedPreferences("user_rs", MODE_PRIVATE);
        if(rs_pref.contains("token")) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
    }

    private void checkUserToken(){
        if (sharedPreferenceClass.getValue_string("token") != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
    }
}



