package com.example.rs.Controlers;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.example.rs.R;

public class DriverDetailsDialogFragment extends DialogFragment {

    private static final String ARG_USER_NAME = "userName";
    private static final String ARG_PHONE_NUMBER = "phoneNumber";
    private static final String ARG_EMAIL = "email";

    public static DriverDetailsDialogFragment newInstance(String userName, String phoneNumber, String email){
        DriverDetailsDialogFragment frag = new DriverDetailsDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER_NAME, userName);
        args.putString(ARG_PHONE_NUMBER, phoneNumber);
        args.putString(ARG_EMAIL, email);
        frag.setArguments(args);
        return frag;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState){
        if (getArguments() == null) {
            throw new IllegalStateException("Argumenty nemôžu byť nulové");
        }

        String userName = getArguments().getString(ARG_USER_NAME);
        String phoneNumber = getArguments().getString(ARG_PHONE_NUMBER);
        String email = getArguments().getString(ARG_EMAIL);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_driver_details, null);

        TextView userNameView = view.findViewById(R.id.userName);
        TextView phoneNumberView = view.findViewById(R.id.userPhone);
        TextView emailView = view.findViewById(R.id.userEmail);

        userNameView.setText(userName);
        phoneNumberView.setText(phoneNumber);
        emailView.setText(email);

        phoneNumberView.setOnClickListener(v -> {
            showCallSmsOptions(phoneNumber);
        });

        emailView.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:" + email));
            startActivity(Intent.createChooser(intent, "Send Email"));
        });

        Bundle args = getArguments();
        if (args != null) {
            userNameView.setText(args.getString(ARG_USER_NAME));
            phoneNumberView.setText(args.getString(ARG_PHONE_NUMBER));
            emailView.setText(args.getString(ARG_EMAIL));
        }

        builder.setView(view)
                .setPositiveButton("OK", (dialog, id) -> dialog.dismiss());

        return builder.create();
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
}


