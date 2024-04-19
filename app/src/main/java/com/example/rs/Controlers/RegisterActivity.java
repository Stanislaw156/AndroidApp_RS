package com.example.rs.Controlers;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
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
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
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

public class RegisterActivity extends AppCompatActivity {
        private Button loginBtn, registerBtn;
        private EditText reg_username, reg_email, reg_password, reg_phoneNumber;
        private String userName, email, password, phoneNumber;
        UtilService utilService;
        SharedPreferenceClass sharedPreferenceClass;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        loginBtn = findViewById(R.id.loginBtn);
        reg_username = findViewById(R.id.reg_username);
        reg_email = findViewById(R.id.reg_email);
        reg_password = findViewById(R.id.reg_password);
        reg_phoneNumber = findViewById(R.id.reg_phoneNumber);
        registerBtn = findViewById(R.id.registerBtn);
        utilService = new UtilService();
        sharedPreferenceClass = new SharedPreferenceClass(this);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                utilService.hideKeyboard(view, RegisterActivity.this);
                userName = reg_username.getText().toString();
                email = reg_email.getText().toString();
                password = reg_password.getText().toString();
                phoneNumber = reg_phoneNumber.getText().toString();
                if (validate(view)) {
                    registerUser(view);
                }
            }
        });
    }

    private void registerUser(View view) {

        final HashMap<String, String> params = new HashMap<>();
        params.put("userName", userName);
        params.put("email", email);
        params.put("password", password);
        params.put("phoneNumber", phoneNumber);

        String apiKey = "http://10.0.2.2:4000/api/RS/auth/register";

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
                        Toast.makeText(RegisterActivity.this, "Registrácia úspešná", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(RegisterActivity.this, "Chyba: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError) {
                    Toast.makeText(RegisterActivity.this, "Chyba časového limitu: Server neodpovedá. Skúste to prosím neskôr.", Toast.LENGTH_LONG).show();
                } else if (error instanceof ServerError && error.networkResponse != null) {
                    try {
                        String res = new String(error.networkResponse.data, HttpHeaderParser.parseCharset(error.networkResponse.headers, "utf-8"));
                        JSONObject obj = new JSONObject(res);
                        Toast.makeText(RegisterActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                    } catch (JSONException | UnsupportedEncodingException je) {
                        je.printStackTrace();
                        Toast.makeText(RegisterActivity.this, "Chyba: " + je.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(RegisterActivity.this, "Vyskytla sa chyba: " + error.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }

    public boolean validate(View view) {
        boolean isValid;

        if(!TextUtils.isEmpty(userName)) {
            if(!TextUtils.isEmpty(email)) {
                if(!TextUtils.isEmpty(password)) {
                    isValid = true;
                } else {
                    utilService.showSnackBar(view,"prosím zadajte heslo...");
                    isValid = false;
                }
            } else {
                utilService.showSnackBar(view,"prosím zadajte email...");
                isValid = false;
            }
        } else {
            utilService.showSnackBar(view,"prosím zadajte pooužívateľské meno...");
            isValid = false;
        }
        return  isValid;
    }

    protected void onStart(){
        super.onStart();
        SharedPreferences rs_pref = getSharedPreferences("user_rs", MODE_PRIVATE);
        if(rs_pref.contains("token")) {
            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
            finish();
        }
    }
}
