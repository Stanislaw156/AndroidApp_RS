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

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<User> userList;
    private Context context;

    public UserAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.ride_joined_users, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.userNameTextView.setText(user.getUserName());
        holder.userEmailTextView.setText(user.getEmail());
        holder.userPhoneNumberTextView.setText(user.getPhoneNumber());
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView userNameTextView;
        TextView userEmailTextView;
        TextView userPhoneNumberTextView;

        public UserViewHolder(View itemView) {
            super(itemView);
            userNameTextView = itemView.findViewById(R.id.textView_userNameItem);
            userEmailTextView = itemView.findViewById(R.id.textView_emailItem);
            userPhoneNumberTextView = itemView.findViewById(R.id.textView_phoneNumberItem);
        }
    }
}