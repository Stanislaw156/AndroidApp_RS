package com.example.rs.Controlers;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.rs.Adapters.RideAdapter;
import com.example.rs.R;
import com.example.rs.UtilsService.SharedPreferenceClass;
import com.example.rs.Model.Ride;
import com.example.rs.Model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SearchRideFragment extends Fragment implements RideAdapter.RideJoinListener {
    private RecyclerView recyclerView;
    private RideAdapter rideAdapter;
    private List<Ride> rideList = new ArrayList<>();
    private RequestQueue requestQueue;
    private Button redirectButton;
    private SharedPreferenceClass sharedPreferenceClass;

    public SearchRideFragment(){

    }

    public void onJoinRide(String rideId) {
        joinRide(rideId);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_ride, container, false);

        sharedPreferenceClass = new SharedPreferenceClass(requireContext());
        recyclerView = view.findViewById(R.id.ridesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        rideAdapter = new RideAdapter(getContext(), rideList, this);
        recyclerView.setAdapter(rideAdapter);

        requestQueue = Volley.newRequestQueue(getContext());
        loadRidesFromApi();

        EditText startPointEditText = view.findViewById(R.id.editText_startPoint);
        EditText endPointEditText = view.findViewById(R.id.editText_endPoint);
        EditText dateEditText = view.findViewById(R.id.editText_date);
        dateEditText.setOnClickListener(v -> showDatePickerDialog(dateEditText));
        Button searchButton = view.findViewById(R.id.button_searchRide);

        searchButton.setOnClickListener(v -> {
            String startPoint = startPointEditText.getText().toString();
            String endPoint = endPointEditText.getText().toString();
            String date = dateEditText.getText().toString();
            searchRides(startPoint, endPoint, date);
        });

        redirectButton = view.findViewById(R.id.button_redirect);
        redirectButton.setOnClickListener(v -> {
            Uri uri = Uri.parse("https://cp.hnonline.sk/vlakbusmhd/spojenie/");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        });
        return view;
    }

    private void showDatePickerDialog(EditText dateEditText) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (view, year1, monthOfYear, dayOfMonth) -> {
            String formattedDate = String.format(Locale.getDefault(), "%d-%02d-%02d", year1, monthOfYear + 1, dayOfMonth);
            dateEditText.setText(formattedDate);
        }, year, month, day);
        datePickerDialog.show();
    }

    private void searchRides(String startPoint, String endPoint, String date) {
        String baseUrl = "http://10.0.2.2:4000/api/RS/shareride/searchRides";
        StringBuilder urlBuilder = new StringBuilder(baseUrl);

        urlBuilder.append("?startPoint=").append(Uri.encode(startPoint));
        urlBuilder.append("&endPoint=").append(Uri.encode(endPoint));
        urlBuilder.append("&date=").append(Uri.encode(date));

        String url = urlBuilder.toString();

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    rideList.clear(); // Clear existing rides before loading new ones
                    parseRides(response);
                    rideAdapter.notifyDataSetChanged();
                    if(rideList.isEmpty()) {
                        redirectButton.setVisibility(View.VISIBLE);
                    } else {
                        redirectButton.setVisibility(View.GONE);
                    }
                },
                error -> {
                    Toast.makeText(getContext(), "Chyba pri načítaní údajov: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    redirectButton.setVisibility(View.VISIBLE);
                });

        requestQueue.add(jsonArrayRequest);
    }

    private void parseRides(JSONArray response) {
        try {
            for (int i = 0; i < response.length(); i++) {
                JSONObject rideObject = response.getJSONObject(i);

                JSONObject userObject = rideObject.optJSONObject("user");
                User user = new User(
                        userObject.optString("id", ""),
                        userObject.optString("userName", "Unknown"),
                        userObject.optString("email", "No email provided"),
                        userObject.optString("phoneNumber", "No phone provided")
                );

                Ride ride = new Ride(
                        rideObject.optString("_id", ""),
                        rideObject.optString("startPoint", ""),
                        rideObject.optString("endPoint", ""),
                        rideObject.optString("date", ""),
                        rideObject.optString("time", ""),
                        rideObject.optString("seats", ""),
                        rideObject.optString("price", ""),
                        user,
                        new ArrayList<>()
                );
                rideList.add(ride);
            }
            rideAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Chyba pri analyzovaní JSON", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadRidesFromApi(){
        String url = "http://10.0.2.2:4000/api/RS/shareride/getAllRides";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                this::parseRides,
                error -> Toast.makeText(getContext(), "Chyba pri načítaní údajov: " + error.getMessage(), Toast.LENGTH_SHORT).show());
        requestQueue.add(jsonArrayRequest);
    }

    private void joinRide(String rideId){
        String url = "http://10.0.2.2:4000/api/RS/shareride/joinRide/" + rideId;
        String token = sharedPreferenceClass.getValue_string("token");
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, null,
                response -> {
                    try {
                        String message = response.getString("message");
                        JSONObject ride = response.getJSONObject("ride");
                        String newSeats = ride.getString("seats");
                        rideAdapter.updateRideSeats(rideId, newSeats);

                        JSONObject user = ride.getJSONObject("user");
                        String userName = user.getString("userName");
                        String phoneNumber = user.getString("phoneNumber");
                        String email = user.getString("email");

                        showDriverDetailsDialog(userName, phoneNumber, email);
                        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Chybné analyzovanie odpovede", Toast.LENGTH_SHORT).show();
                    }
                }, error -> {
            Toast.makeText(getContext(), "Chyba: " + error.toString(), Toast.LENGTH_SHORT).show();
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }

    private void showDriverDetailsDialog(String userName, String phoneNumber, String email) {
        DriverDetailsDialogFragment dialog = DriverDetailsDialogFragment.newInstance(userName, phoneNumber, email);
        dialog.show(getChildFragmentManager(), "DriverDetailsDialog" );
    }
}
