package com.example.rs.Controlers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.rs.R;
import com.example.rs.UtilsService.SharedPreferenceClass;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DriverFragment extends Fragment {
    private EditText etFirstName, etLastName, etBirthDate, etVehicle, etRegistrationNumber, etNote;
    private Button btnEdit, btnSubmit;
    private SharedPreferenceClass sharedPreferenceClass;
    private boolean isEditMode = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_driver, container, false);

        sharedPreferenceClass = new SharedPreferenceClass(getContext());

        initUI(view);
        fetchDriverProfileFromServer();

        return view;
    }

    private void initUI(View view) {
        etFirstName = view.findViewById(R.id.etFirstName);
        etLastName = view.findViewById(R.id.etLastName);
        etBirthDate = view.findViewById(R.id.etBirthDate);
        etVehicle = view.findViewById(R.id.etVehicle);
        etRegistrationNumber = view.findViewById(R.id.etRegistrationNumber);
        etNote = view.findViewById(R.id.etNote);
        btnEdit = view.findViewById(R.id.btnEdit);
        btnSubmit = view.findViewById(R.id.btnSubmit);

        btnEdit.setOnClickListener(v -> {
            toggleEditMode(true);
            btnEdit.setText(isEditMode ? "Aktualizovať" : "Upraviť");
        });
        btnSubmit.setOnClickListener(v -> {
            submitOrUpdateDriverProfile();
            if(isEditMode) {
                btnEdit.setText("Upraviť");
            }
        });
        enableEditing(false);
    }

    private void fetchDriverProfileFromServer() {
        String url = "http://10.0.2.2:4000/api/RS/driver/myDriverInfo";
        String token = sharedPreferenceClass.getValue_string("token");

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    if (response.length() == 0) {
                        enableEditing(true);
                        btnEdit.setVisibility(View.GONE);
                        btnSubmit.setVisibility(View.VISIBLE);
                    } else {
                        fillProfileFields(response);
                        btnEdit.setVisibility(View.VISIBLE);
                        btnSubmit.setVisibility(View.GONE);
                        isEditMode = false;
                    }
                },
                error -> {
                    Toast.makeText(getActivity(), "Chyba pri načítaní profilu vodiča: " + error.toString(), Toast.LENGTH_SHORT).show();
                    enableEditing(true);
                    btnEdit.setVisibility(View.GONE);
                    btnSubmit.setVisibility(View.VISIBLE);
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };
        Volley.newRequestQueue(getActivity()).add(jsonObjectRequest);
    }

    private void fillProfileFields(JSONObject profile) {
        try {
            etFirstName.setText(profile.getString("firstName"));
            etLastName.setText(profile.getString("lastName"));
            etBirthDate.setText(profile.getString("birthDate"));
            etVehicle.setText(profile.getString("vehicle"));
            etRegistrationNumber.setText(profile.getString("registrationNumber"));
            etNote.setText(profile.getString("note"));
            isEditMode = true;
            enableEditing(false);
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Chyba pri analýze údajov profilu", Toast.LENGTH_SHORT).show();
        }
    }

    private void toggleEditMode(boolean enable) {
        isEditMode = enable;
        enableEditing(enable);
        btnSubmit.setVisibility(enable ? View.VISIBLE : View.GONE);
        btnEdit.setVisibility(enable ? View.GONE : View.VISIBLE);
    }

    private void enableEditing(boolean isEnabled) {
        etFirstName.setEnabled(isEnabled);
        etLastName.setEnabled(isEnabled);
        etBirthDate.setEnabled(isEnabled);
        etVehicle.setEnabled(isEnabled);
        etRegistrationNumber.setEnabled(isEnabled);
        etNote.setEnabled(isEnabled);
    }

    private void submitOrUpdateDriverProfile() {
        String url = isEditMode ? "http://10.0.2.2:4000/api/RS/driver/updateMyDriverInfo" :
                                  "http://10.0.2.2:4000/api/RS/driver/createDriver";
        int method = isEditMode ? Request.Method.PUT : Request.Method.POST;

        try {
            JSONObject params = new JSONObject();
            params.put("firstName", etFirstName.getText().toString().trim());
            params.put("lastName", etLastName.getText().toString().trim());
            params.put("birthDate", etBirthDate.getText().toString().trim());
            params.put("vehicle", etVehicle.getText().toString().trim());
            params.put("registrationNumber", etRegistrationNumber.getText().toString().trim());
            params.put("note", etNote.getText().toString().trim());

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(method, url, params,
                    response -> {
                        Toast.makeText(getActivity(), isEditMode ? "Profil bol úspešne aktualizovaný" :
                                                                   "Profil bol úspešne vytvorený", Toast.LENGTH_SHORT).show();
                        toggleEditMode(false);
                    },
                    error -> Toast.makeText(getActivity(), "Error: " + error.toString(), Toast.LENGTH_SHORT).show()
            ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    headers.put("Authorization", "Bearer " + sharedPreferenceClass.getValue_string("token"));
                    return headers;
                }
            };
            Volley.newRequestQueue(getActivity()).add(jsonObjectRequest);
        } catch (JSONException e) {
            Toast.makeText(getActivity(), "Chyba pri vytváraní objektu JSON", Toast.LENGTH_SHORT).show();
        }
    }
}