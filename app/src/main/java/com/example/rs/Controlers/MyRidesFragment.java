package com.example.rs.Controlers;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.rs.Adapters.MyCreatedRidesAdapter;
import com.example.rs.Adapters.MyJoinedRidesAdapter;
import com.example.rs.R;
import com.example.rs.UtilsService.SharedPreferenceClass;
import com.example.rs.Model.Ride;
import com.example.rs.Model.User;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyRidesFragment extends Fragment {

    private RecyclerView createdRidesRecyclerView;
    private RecyclerView joinedRidesRecyclerView;
    private MyCreatedRidesAdapter createdRidesAdapter;
    private MyJoinedRidesAdapter joinedRidesAdapter;
    private List<Ride> createdRidesList = new ArrayList<>();
    private List<Ride> joinedRidesList = new ArrayList<>();
    private SharedPreferenceClass sharedPreferenceClass;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_rides, container, false);
        sharedPreferenceClass = new SharedPreferenceClass(getContext());

        setupRecyclerViews(view);

        loadCreatedRides();
        loadJoinedRides();
        return view;
    }

    private void setupRecyclerViews(View view) {
        createdRidesRecyclerView = view.findViewById(R.id.createdRidesRecyclerView);
        createdRidesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        createdRidesAdapter = new MyCreatedRidesAdapter(getContext(), createdRidesList);
        createdRidesRecyclerView.setAdapter(createdRidesAdapter);
        createdRidesAdapter.setOnRideDeleteListener(rideId -> deleteRide(rideId));
        createdRidesAdapter.setOnUserInteractionListener(new MyCreatedRidesAdapter.OnUserInteractionListener() {
            @Override
            public void onEmailClicked(String email) {
                showEmailOptions(email);
            }

            @Override
            public void onPhoneNumberClicked(String phoneNumber) {
                showCallSmsOptions(phoneNumber);
            }
        });

        joinedRidesRecyclerView = view.findViewById(R.id.joinedRidesRecyclerView);
        joinedRidesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        joinedRidesAdapter = new MyJoinedRidesAdapter(getContext(), joinedRidesList);
        joinedRidesRecyclerView.setAdapter(joinedRidesAdapter);
        joinedRidesAdapter.setOnRideLeaveListener(rideId -> {
            leaveRide(rideId);
        });
        joinedRidesAdapter.setOnPhoneNumberClickListener(phoneNumber -> {
            showCallSmsOptions(phoneNumber);
        });
        joinedRidesAdapter.setOnEmailClickListener(email -> {
            showEmailOptions(email);
        });
    }

    private void showEmailOptions(String email) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:" + email));
        startActivity(Intent.createChooser(emailIntent, "Odoslať email pomocou..."));
    }
    private void showCallSmsOptions(String number) {
        CharSequence options[] = new CharSequence[] {"Volanie", "SMS"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Vyberte akciu pre " + number);
        builder.setItems(options, (dialog, item) -> {
            if (item == 0) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + number));
                startActivity(callIntent);
            } else if (item == 1) {
                Intent smsIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + number));
                startActivity(smsIntent);
            }
        });
        builder.show();
    }

    private void loadCreatedRides() {
        String url = "http://10.0.2.2:4000/api/RS/shareride/myCreatedRides";
        RequestQueue queue = Volley.newRequestQueue(requireContext());
        String token = sharedPreferenceClass.getValue_string("token");

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, response -> {
            try {
                createdRidesList.clear();
                for (int i = 0; i < response.length(); i++) {
                    JSONObject rideObj = response.getJSONObject(i);
                    String rideId = rideObj.optString("_id", "");
                    String startPoint = rideObj.optString("startPoint", "");
                    String endPoint = rideObj.optString("endPoint", "");
                    String date = rideObj.optString("date", "");
                    String time = rideObj.optString("time", "");
                    String seats = rideObj.optString("seats", "");
                    String price = rideObj.optString("price", "");

                    JSONObject creatorObj = rideObj.optJSONObject("createdBy");
                    User creator = User.fromJsonObject(creatorObj);

                    JSONArray joinedUsersArray = rideObj.getJSONArray("joinedUsers");
                    List<User> joinedUsers = new ArrayList<>();
                    for (int j = 0; j < joinedUsersArray.length(); j++) {
                        JSONObject userObj = joinedUsersArray.getJSONObject(j);
                        User user = User.fromJsonObject(userObj);
                        joinedUsers.add(user);
                    }
                    Ride ride = new Ride(rideId, startPoint, endPoint, date, time, seats, price, creator, joinedUsers);
                    createdRidesList.add(ride);
                }
                createdRidesAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Chybné analyzovanie údajov o vytvorených jazdách", Toast.LENGTH_LONG).show();
            }
        }, error -> {
            if (isAdded()) {
                Toast.makeText(getContext(), "Chyba pri načítaní vytvorených jázd", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };
        queue.add(jsonArrayRequest);
    }

    private void loadJoinedRides() {
        String url = "http://10.0.2.2:4000/api/RS/shareride/myJoinedRides";
        RequestQueue queue = Volley.newRequestQueue(requireContext());
        String token = sharedPreferenceClass.getValue_string("token");

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, response -> {
            try {
                joinedRidesList.clear();
                for (int i = 0; i < response.length(); i++) {
                    JSONObject responseObj = response.getJSONObject(i);
                    JSONObject rideDetails = responseObj.getJSONObject("rideDetails");
                    JSONObject createdBy = responseObj.getJSONObject("createdBy");

                    String rideId = rideDetails.getString("_id");
                    String startPoint = rideDetails.getString("startPoint");
                    String endPoint = rideDetails.getString("endPoint");
                    String date = rideDetails.getString("date");
                    String time = rideDetails.getString("time");
                    String seats = rideDetails.getString("seats");
                    String price = rideDetails.getString("price");

                    User creator = new User(createdBy.getString("_id"), createdBy.getString("userName"), createdBy.getString("email"), createdBy.getString("phoneNumber"));

                    joinedRidesList.add(new Ride(rideId, startPoint, endPoint, date, time, seats, price, creator, new ArrayList<>()));
                }
                joinedRidesAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Chyba pri analyzovaní údajov o pripojených jazdách", Toast.LENGTH_LONG).show();
            }
        }, error -> {
            Log.e("MyRidesFragment", "Chyba: " + error.toString());
            Toast.makeText(getContext(), "Chyba pri načítavaní pripojených jázd", Toast.LENGTH_LONG).show();
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };
        queue.add(jsonArrayRequest);
    }

    private void leaveRide(String rideId) {
        String url = "http://10.0.2.2:4000/api/RS/shareride/leaveRide/" + rideId;
        RequestQueue queue = Volley.newRequestQueue(requireContext());
        String token = sharedPreferenceClass.getValue_string("token");

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, null,
                response -> {
                    try {
                        String message = response.getString("message");
                        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();

                        loadCreatedRides();
                        loadJoinedRides();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Error parsing response", Toast.LENGTH_LONG).show();
                    }
                }, error -> {
            Log.e("LeaveRideError", "Chyba: " + error.toString());
            Toast.makeText(getContext(), "Chyba pri analyzovaní odpovede", Toast.LENGTH_LONG).show();
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };
        queue.add(jsonObjectRequest);
    }

    private void deleteRide(String rideId) {
        String url = "http://10.0.2.2:4000/api/RS/shareride/deleteRide/" + rideId;
        RequestQueue queue = Volley.newRequestQueue(requireContext());
        String token = sharedPreferenceClass.getValue_string("token");

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, url, null,
                response -> {
                    Toast.makeText(getContext(), "Jazda bola odstránená.", Toast.LENGTH_LONG).show();
                    loadCreatedRides();
                }, error -> Toast.makeText(getContext(), "Chyba pri odstraňovaní jazdy", Toast.LENGTH_LONG).show()) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };
        queue.add(jsonObjectRequest);
    }
}
