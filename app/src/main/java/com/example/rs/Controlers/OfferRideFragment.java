package com.example.rs.Controlers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.rs.R;
import com.example.rs.UtilsService.SharedPreferenceClass;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class OfferRideFragment extends Fragment{
    private int currentStep = 1;
    private FrameLayout stepContainer;
    private Button nextButton, backButton;
    private TimePicker timePicker;
    private EditText editTextStartPoint, editTextEndPoint, editTextSeats, editTextPrice;
    private SharedPreferenceClass sharedPreferenceClass;

    private String startPoint = "";
    private String endPoint = "";
    private String date = "";
    private String time = "";
    private String seats = "1";
    private String price = "";
    public OfferRideFragment(){

    }

    @SuppressLint("SetTextI18n")
    private void updateSummaryView(){

        EditText summaryStartPointEditText = stepContainer.findViewById(R.id.editSummaryStartPoint);
        EditText summaryEndPointEditText = stepContainer.findViewById(R.id.editSummaryEndPoint);
        EditText summaryDateEditText = stepContainer.findViewById(R.id.editSummaryDate);
        EditText summaryTimeEditText = stepContainer.findViewById(R.id.editTextSummaryTime);
        EditText summarySeatCountEditText = stepContainer.findViewById(R.id.editTextSummarySeatCount);
        EditText summaryPriceEditText = stepContainer.findViewById(R.id.editTextSummaryPrice);

        summaryStartPointEditText.setText(startPoint);
        summaryEndPointEditText.setText(endPoint);
        summaryDateEditText.setText(date);
        summaryTimeEditText.setText(time);
        summarySeatCountEditText.setText(String.valueOf(seats));
        summaryPriceEditText.setText(price);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_offer_ride_stepwise, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedPreferenceClass = new SharedPreferenceClass(requireContext());

        stepContainer = view.findViewById(R.id.stepContainer);
        backButton = view.findViewById(R.id.button_back);
        nextButton = view.findViewById(R.id.button_next);

        backButton.setOnClickListener(v -> processPreviousStep());
        nextButton.setOnClickListener(v -> processNextStep());

        updateStepView(LayoutInflater.from(requireContext()));
    }

    private void processPreviousStep(){
        if (!isAdded()) {
            return;
        }

        Context context = requireContext();

        if(currentStep > 1){
            currentStep--;
        }else {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStackImmediate();
            }
        }
        if (isAdded()) {
            updateStepView(LayoutInflater.from(requireContext()));
        }
    }

    private void processNextStep() {
        if (!isAdded()) {
            return;
        }

        Context context = requireContext();

        if (!validateCurrentStep()) {
            return;
        }

        if (currentStep == 3) {
            DatePicker datePicker = stepContainer.findViewById(R.id.datePicker);
            date = getDateString(datePicker);
        }

        if (currentStep == 4 && timePicker != null) {
            int hour = timePicker.getCurrentHour();
            int minute = timePicker.getCurrentMinute();
            time = String.format(Locale.getDefault(), "%02d:%02d", hour, minute);
        }

        if (currentStep == 7) {
            sendOfferRideDataToServer();
        } else {
            currentStep++;
            updateStepView(LayoutInflater.from(requireContext()));
        }
    }

    private String getDateString(DatePicker datePicker) {
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth() + 1;
        int year = datePicker.getYear();
        return String.format(Locale.US, "%04d-%02d-%02d", year, month, day);
    }

    private boolean validateCurrentStep() {
        boolean isValid = true;
        switch (currentStep) {
            case 1:
                EditText startPointEditText = stepContainer.findViewById(R.id.editText_startPoint);
                if (startPointEditText == null || startPointEditText.getText().toString().trim().isEmpty()) {
                    Toast.makeText(getActivity(), "Prosím, zadajte štartovací bod.", Toast.LENGTH_SHORT).show();
                    return false;
                }
                startPoint = startPointEditText.getText().toString().trim();
                break;
            case 2:
                EditText endPointEditText = stepContainer.findViewById(R.id.editText_endPoint);
                if (endPointEditText.getText().toString().trim().isEmpty()) {
                    Toast.makeText(getActivity(), "Prosím, vyberte cieľové miesto.", Toast.LENGTH_SHORT).show();
                    return false;
                }
                endPoint = endPointEditText.getText().toString().trim();
                break;
            case 3:
                DatePicker datePicker = stepContainer.findViewById(R.id.datePicker);
                if (datePicker == null) {
                    return false;
                }
                Calendar today = Calendar.getInstance();
                Calendar selectedDate = Calendar.getInstance();
                selectedDate.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
                if (selectedDate.before(today)) {
                    Toast.makeText(getActivity(), "Dátum cesty nemôže byť v minulosti.", Toast.LENGTH_SHORT).show();
                    return false;
                }
                break;
            case 4:
                if (timePicker == null || !isTimePickerTimeSet(timePicker)) {
                    Toast.makeText(getActivity(), "Prosím, nastavte čas odchodu.", Toast.LENGTH_SHORT).show();
                    return false;
                }
                break;
            case 5:
                EditText seatsEditText = stepContainer.findViewById(R.id.editTextPlaces);
                String seatsInput = seatsEditText.getText().toString();
                try {
                    int seatsInt = Integer.parseInt(seatsInput);
                    if (seatsInt < 1 || seatsInt > 4) {
                        Toast.makeText(getActivity(), "Zadajte platný počet miest (1-4).", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    seats = seatsInput;
                } catch (NumberFormatException e) {
                    Toast.makeText(getActivity(), "Zadajte platný počet miest.", Toast.LENGTH_SHORT).show();
                    return false;
                }
                break;
            case 6:
                EditText editTextPrice = stepContainer.findViewById(R.id.editTextPrice);
                if (editTextPrice != null && editTextPrice.getText().toString().trim().isEmpty()) {
                    Toast.makeText(getActivity(), "Prosím, zadajte cenu za cestu.", Toast.LENGTH_SHORT).show();
                    return false;
                }else{
                    price = editTextPrice.getText().toString().trim();
                }
                break;
            case 7:
                updateSummaryView();
                break;
            default:
                break;
        }
        return isValid;
    }

    private boolean isTimePickerTimeSet(TimePicker timePicker) {
        int defaultHour = 0;
        int defaultMinute = 0;

        int pickedHour = timePicker.getCurrentHour();
        int pickedMinute = timePicker.getCurrentMinute();

        return !(pickedHour == defaultHour && pickedMinute == defaultMinute);
    }

    @SuppressLint("MissingInflatedId")
    private void updateStepView(LayoutInflater inflater) {
        if (!isAdded()) {
            return;
        }

        stepContainer.removeAllViews();
        int layoutId = 0;
        View stepView = null;
        switch (currentStep) {
            case 1:
                layoutId = R.layout.step_start_point;
                break;
            case 2:
                layoutId = R.layout.step_end_point;
                break;
            case 3:
                layoutId = R.layout.step_date;
                break;
            case 4:
                layoutId = R.layout.step_time;
                break;
            case 5:
                layoutId = R.layout.step_places;
                break;
            case 6:
                layoutId = R.layout.step_price;
                break;
            case 7:
                layoutId = R.layout.step_summary;
                break;
        }
        if(layoutId != 0) {
            stepView = inflater.inflate(layoutId, stepContainer, false);
            stepContainer.addView(stepView);
        }

        if (currentStep == 4) {
            timePicker = stepView.findViewById(R.id.timePicker);
            if (timePicker != null) {
                timePicker.setIs24HourView(true);
            }
        } else if (currentStep == 7) {
            updateSummaryView();
        }
        nextButton.setText(currentStep == 7 ? "Potvrdiť" : "Ďalej");
    }

    private void returnToHomeFragment(){
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    private void sendOfferRideDataToServer(){
        final Map<String, String> params = new HashMap<>();

        params.put("startPoint", startPoint);
        params.put("endPoint", endPoint);
        params.put("date", date);
        params.put("time", time);
        params.put("seats", seats);
        params.put("price", price);
        String userName = getCurrentUserName();
        params.put("userName",userName);

        String url = "http://10.0.2.2:4000/api/RS/shareride/createRide";
        String token = sharedPreferenceClass.getValue_string("token");

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (response.optBoolean("success", false)) {
                            Toast.makeText(getActivity(), "Jazda bola úspešne vytvorená", Toast.LENGTH_SHORT).show();
                            returnToHomeFragment();
                        } else {
                            Toast.makeText(getActivity(), "Jazda bola úspešne vytvorená", Toast.LENGTH_LONG).show();
                        }
                        returnToHomeFragment();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "Chyba: " + error.toString(), Toast.LENGTH_SHORT).show();
                returnToHomeFragment();
            }
        }
        ){
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

    private String getCurrentUserName() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("AppNamePreferences", Context.MODE_PRIVATE);
        return sharedPreferences.getString("userName", null);
    }
}