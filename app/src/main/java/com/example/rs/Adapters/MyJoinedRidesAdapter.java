package com.example.rs.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.rs.R;
import com.example.rs.Model.Ride;
import com.example.rs.Model.User;

import java.util.ArrayList;
import java.util.List;

public class MyJoinedRidesAdapter extends RecyclerView.Adapter<MyJoinedRidesAdapter.ViewHolder> {

    private Context context;
    private List<Ride> joinedRidesList;

    public MyJoinedRidesAdapter(Context context, List<Ride> joinedRidesList) {
        this.context = context;
        this.joinedRidesList = joinedRidesList != null ? joinedRidesList : new ArrayList<>();
    }

    public interface OnPhoneNumberClickListener {
        void onPhoneNumberClick(String phoneNumber);
    }

    private OnPhoneNumberClickListener phoneNumberClickListener;

    public void setOnPhoneNumberClickListener(OnPhoneNumberClickListener listener) {
        this.phoneNumberClickListener = listener;
    }

    public interface OnRideLeaveListener {
        void onRideLeave(String rideId);
    }

    private OnRideLeaveListener listener;

    public void setOnRideLeaveListener(OnRideLeaveListener listener) {
        this.listener = listener;
    }

    public interface OnEmailClickListener {
        void onEmailClick(String email);
    }

    private OnEmailClickListener emailClickListener;

    public void setOnEmailClickListener(OnEmailClickListener listener) {
        this.emailClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.my_joined_rides, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Ride ride = joinedRidesList.get(position);
        holder.startPoint.setText(ride.getStartPoint());
        holder.endPoint.setText(ride.getEndPoint());
        holder.date.setText(ride.getDate());
        holder.time.setText(ride.getTime());
        holder.seats.setText(String.format("Miesta: %s", ride.getSeats()));
        holder.price.setText(String.format("Cena: %s", ride.getPrice()));

        User creator = ride.getUser();
        if (creator != null) {
            holder.userName.setText(creator.getUserName());
            holder.email.setText(creator.getEmail());
            holder.phoneNumber.setText(creator.getPhoneNumber());

            holder.phoneNumber.setOnClickListener(v -> {
                if (phoneNumberClickListener != null) {
                    phoneNumberClickListener.onPhoneNumberClick(creator.getPhoneNumber());
                }
            });
            holder.email.setOnClickListener(v -> {
                if (emailClickListener != null) {
                    emailClickListener.onEmailClick(creator.getEmail());
                }
            });
            holder.btnLeaveRide.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onRideLeave(ride.getRideId());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return joinedRidesList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView startPoint, endPoint, date, time, seats, price, userName, email, phoneNumber;
        Button btnLeaveRide;

        public ViewHolder(View itemView) {
            super(itemView);
            startPoint = itemView.findViewById(R.id.textView_startPoint);
            endPoint = itemView.findViewById(R.id.textView_endPoint);
            date = itemView.findViewById(R.id.textView_date);
            time = itemView.findViewById(R.id.textView_time);
            seats = itemView.findViewById(R.id.textView_seats);
            price = itemView.findViewById(R.id.textView_price);
            userName = itemView.findViewById(R.id.textView_userName);
            email = itemView.findViewById(R.id.textView_email);
            phoneNumber = itemView.findViewById(R.id.textView_phoneNumber);
            btnLeaveRide = itemView.findViewById(R.id.btnLeaveRide);

        }
    }
}
