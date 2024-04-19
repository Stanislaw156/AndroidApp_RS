package com.example.rs.Controlers;

import android.os.Bundle;
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
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.rs.R;
import com.example.rs.UtilsService.SharedPreferenceClass;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends Fragment {
    Button offerRideButton, searchRideButton;
    private SharedPreferenceClass sharedPreferenceClass;

    public HomeFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        sharedPreferenceClass = new SharedPreferenceClass(requireContext());

        offerRideButton = view.findViewById(R.id.offerRideButton);
        searchRideButton = view.findViewById(R.id.searchRideButton);

        offerRideButton.setOnClickListener(v -> checkUserRoleAndProceed(true));
        searchRideButton.setOnClickListener(v -> checkUserRoleAndProceed(false));

        return view;
    }

    private void checkUserRoleAndProceed(boolean isOfferingRide) {
        String url = "http://10.0.2.2:4000/api/RS/userrole/getRole";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String role = response.getString("role");
                    if ("Driver".equals(role)) {
                        if (isOfferingRide) {
                            navigateToFragment(new OfferRideFragment());
                        } else {
                            navigateToFragment(new SearchRideFragment());
                        }
                    } else if ("Passenger".equals(role) && !isOfferingRide) {
                        navigateToFragment(new SearchRideFragment());
                    } else {
                        Toast.makeText(getContext(), "Jazdy môžu ponúkať len vodiči.", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Chyba pri kontrole roly používateľa", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "Nemáte rolu, najprv si ju musíte vybrať v používateľskom profile", Toast.LENGTH_SHORT).show();
            }
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

    private void navigateToFragment(Fragment fragment) {
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.content, fragment)
                .addToBackStack(null)
                .commit();
    }
}