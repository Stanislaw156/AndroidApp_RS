package com.example.rs.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.rs.R;
import com.example.rs.Model.User;
import java.util.List;

public class JoinedUsersAdapter extends RecyclerView.Adapter<JoinedUsersAdapter.ViewHolder> {

    private List<User> joinedUsersList;
    private Context context;

    public JoinedUsersAdapter(Context context, List<User> joinedUsersList) {
        this.context = context;
        this.joinedUsersList = joinedUsersList;
    }

    public interface OnEmailClickListener {
        void onEmailClick(String email);
    }

    public interface OnPhoneNumberClickListener {
        void onPhoneNumberClick(String phoneNumber);
    }

    private OnEmailClickListener emailClickListener;
    private OnPhoneNumberClickListener phoneNumberClickListener;

    public void setOnEmailClickListener(OnEmailClickListener listener) {
        this.emailClickListener = listener;
    }

    public void setOnPhoneNumberClickListener(OnPhoneNumberClickListener listener) {
        this.phoneNumberClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.ride_joined_users, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = joinedUsersList.get(position);
        holder.userName.setText(user.getUserName());
        holder.email.setText(user.getEmail());
        holder.phoneNumber.setText(user.getPhoneNumber());
        holder.email.setOnClickListener(v -> {
            if (emailClickListener != null) {
                emailClickListener.onEmailClick(user.getEmail());
            }
        });
        holder.phoneNumber.setOnClickListener(v -> {
            if (phoneNumberClickListener != null) {
                phoneNumberClickListener.onPhoneNumberClick(user.getPhoneNumber());
            }
        });
    }

    @Override
    public int getItemCount() {
        return joinedUsersList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView userName, email, phoneNumber;

        public ViewHolder(View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.textView_userNameItem);
            email = itemView.findViewById(R.id.textView_emailItem);
            phoneNumber = itemView.findViewById(R.id.textView_phoneNumberItem);
        }
    }
}