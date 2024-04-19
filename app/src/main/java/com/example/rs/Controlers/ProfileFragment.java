package com.example.rs.Controlers;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

public class ProfileFragment extends Fragment {

    private SharedPreferenceClass sharedPreferenceClass;
    private View rootView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        sharedPreferenceClass = new SharedPreferenceClass(requireContext());

        checkUserRoleAndRedirect();
        initializeRoleSelectionUI();

        return rootView;
    }

    private void checkUserRoleAndRedirect() {
        String url = "http://10.0.2.2:4000/api/RS/userrole/getRole";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
            try {
                String role = response.getString("role");
                navigateToFragmentBasedOnRole(role);
            } catch (JSONException e) {
                Log.e("ProfileFragment", "Chyba pri spracovaní odpovede API", e);
                showRoleSelectionUI();
            }
        }, error -> {
            Log.e("ProfileFragment", "Žiadosť API zlyhala", error);
            showRoleSelectionUI();
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + sharedPreferenceClass.getValue_string("token"));
                return headers;
            }
        };
        Volley.newRequestQueue(requireContext()).add(request);
    }

    private void navigateToFragmentBasedOnRole(String role) {
        Fragment targetFragment = null;
        if ("Driver".equals(role)) {
            targetFragment = new DriverFragment();
        } else if ("Passenger".equals(role)) {
            targetFragment = new PassengerFragment();
        }

        if (targetFragment != null) {
            hideRoleSelectionUI();
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, targetFragment)
                    .commit();
        } else {
            showRoleSelectionUI();
        }
    }

    private void hideRoleSelectionUI() {
        rootView.findViewById(R.id.groupRoleSelection).setVisibility(View.GONE);
        rootView.findViewById(R.id.fragment_container).setVisibility(View.VISIBLE);
    }

    private void showRoleSelectionUI() {
        rootView.findViewById(R.id.groupRoleSelection).setVisibility(View.VISIBLE);
        rootView.findViewById(R.id.fragment_container).setVisibility(View.GONE);
    }

    private void initializeRoleSelectionUI() {
        Button buttonDriver = rootView.findViewById(R.id.buttonDriver);
        Button buttonPassenger = rootView.findViewById(R.id.buttonPassenger);

        buttonDriver.setOnClickListener(v -> updateRole(true, false));
        buttonPassenger.setOnClickListener(v -> updateRole(false, true));
    }

    private void updateRole(boolean isDriver, boolean isPassenger) {
        hideRoleSelectionUI();
        String url = "http://10.0.2.2:4000/api/RS/userrole/assignRole";
        JSONObject params = new JSONObject();
        try {
            params.put("isDriver", isDriver);
            params.put("isPassenger", isPassenger);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, params, response -> {
            navigateToFragmentBasedOnRole(isDriver ? "Driver" : "Passenger");
        }, error -> {
            Log.e("ProfileFragment", "Žiadosť API zlyhala", error);
            if (getContext() != null) {
                Toast.makeText(getContext(), "Chyba: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
            showRoleSelectionUI();
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + sharedPreferenceClass.getValue_string("token"));
                return headers;
            }
        };
        Volley.newRequestQueue(requireContext()).add(request);
    }
}
