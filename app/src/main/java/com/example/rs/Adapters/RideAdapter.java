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

import java.util.List;

public class RideAdapter extends RecyclerView.Adapter<RideAdapter.ViewHolder> {
    private RideJoinListener listener;
    private List<Ride> rideList;
    private Context context;

    public RideAdapter(Context context, List<Ride> rideList, RideJoinListener listener) {
        this.context = context;
        this.rideList = rideList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ride_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Ride ride = rideList.get(position);
        holder.joinRideButton.setOnClickListener(view -> listener.onJoinRide(ride.getRideId()));
        User user = ride.getUser();

        String userNameDisplay = user != null ? user.getUserName() : "Neznámy";
        holder.userName.setText("Vytvoril: " + userNameDisplay);
        holder.startPoint.setText("Začiatočná adresa: " + ride.getStartPoint());
        holder.endPoint.setText("Koncová adresa: " + ride.getEndPoint());
        holder.date.setText("Dátum: " + ride.getDate());
        holder.time.setText("Čas: " + ride.getTime());
        holder.seats.setText("Voľné miesta: " + ride.getSeats());
        holder.price.setText("Cena: " + ride.getPrice());
    }

    @Override
    public int getItemCount() {
        return rideList.size();
    }

    public void updateRideSeats(String rideId, String newSeats) {
        for (Ride ride : rideList) {
            if (ride.getRideId().equals(rideId)) {
                ride.setSeats(newSeats);
                notifyDataSetChanged();
                break;
            }
        }
    }

    public interface RideJoinListener {
        void onJoinRide(String rideId);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView startPoint;
        public TextView endPoint;
        public TextView date;
        public TextView time;
        public TextView seats;
        public TextView price;
        public TextView userName;
        public Button joinRideButton;

        public ViewHolder(View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.textView_userName);
            startPoint = itemView.findViewById(R.id.textView_startPoint);
            endPoint = itemView.findViewById(R.id.textView_endPoint);
            date = itemView.findViewById(R.id.textView_date);
            time = itemView.findViewById(R.id.textView_time);
            seats = itemView.findViewById(R.id.textView_seats);
            price = itemView.findViewById(R.id.textView_price);
            joinRideButton = itemView.findViewById(R.id.button_joinRide);
        }
    }
}
