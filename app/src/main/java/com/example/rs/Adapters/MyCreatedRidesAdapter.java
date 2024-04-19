package com.example.rs.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.rs.R;
import com.example.rs.Model.Ride;
import java.util.List;

public class MyCreatedRidesAdapter extends RecyclerView.Adapter<MyCreatedRidesAdapter.ViewHolder> {

    private Context context;
    private List<Ride> createdRidesList;

    public MyCreatedRidesAdapter(Context context, List<Ride> createdRidesList) {
        this.context = context;
        this.createdRidesList = createdRidesList;
    }

    public interface OnRideDeleteListener {
        void onDeleteRide(String rideId);
    }

    private OnRideDeleteListener onRideDeleteListener;

    public void setOnRideDeleteListener(OnRideDeleteListener onRideDeleteListener) {
        this.onRideDeleteListener = onRideDeleteListener;
    }

    public interface OnUserInteractionListener {
        void onEmailClicked(String email);
        void onPhoneNumberClicked(String phoneNumber);
    }

    private OnUserInteractionListener userInteractionListener;

    public void setOnUserInteractionListener(OnUserInteractionListener listener) {
        this.userInteractionListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.my_created_rides, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Ride ride = createdRidesList.get(position);
        holder.startPoint.setText(ride.getStartPoint());
        holder.endPoint.setText(ride.getEndPoint());
        holder.date.setText(ride.getDate());
        holder.time.setText(ride.getTime());
        holder.seats.setText("Miesta: " + ride.getSeats());
        holder.price.setText("Cena: " + ride.getPrice());

        JoinedUsersAdapter joinedUsersAdapter = new JoinedUsersAdapter(context, ride.getJoinedUsers());
        holder.joinedUsersRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        holder.joinedUsersRecyclerView.setAdapter(joinedUsersAdapter);

        holder.btnDeleteRide.setOnClickListener(v -> {
            if(onRideDeleteListener != null) {
                onRideDeleteListener.onDeleteRide(createdRidesList.get(position).getRideId());
            }
        });
        joinedUsersAdapter.setOnEmailClickListener(email -> {
            if (userInteractionListener != null) {
                userInteractionListener.onEmailClicked(email);
            }
        });
        joinedUsersAdapter.setOnPhoneNumberClickListener(phoneNumber -> {
            if (userInteractionListener != null) {
                userInteractionListener.onPhoneNumberClicked(phoneNumber);
            }
        });

    }

    @Override
    public int getItemCount() {
        return createdRidesList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView startPoint, endPoint, date, time, seats, price;
        RecyclerView joinedUsersRecyclerView;
        Button btnDeleteRide;

        public ViewHolder(View itemView) {
            super(itemView);
            startPoint = itemView.findViewById(R.id.textView_startPoint);
            endPoint = itemView.findViewById(R.id.textView_endPoint);
            date = itemView.findViewById(R.id.textView_date);
            time = itemView.findViewById(R.id.textView_time);
            seats = itemView.findViewById(R.id.textView_seats);
            price = itemView.findViewById(R.id.textView_price);
            joinedUsersRecyclerView = itemView.findViewById(R.id.recyclerView_joinedUsers);
            btnDeleteRide = itemView.findViewById(R.id.btnDeleteRide);
        }
    }
}
