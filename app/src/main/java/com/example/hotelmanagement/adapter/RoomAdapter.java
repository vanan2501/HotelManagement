package com.example.hotelmanagement.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotelmanagement.R;
import com.example.hotelmanagement.model.Room;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.List;
import java.util.Locale;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomViewHolder> {

    private List<Room> roomList;
    private OnRoomClickListener listener;
    private FirebaseFirestore db;

    public interface OnRoomClickListener {
        void onBookNow(Room room);
    }

    public RoomAdapter(List<Room> roomList, OnRoomClickListener listener) {
        this.roomList = roomList;
        this.listener = listener;
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_room, parent, false);
        return new RoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        Room room = roomList.get(position);
        holder.txtName.setText(room.getName());
        holder.txtType.setText(room.getType());
        holder.txtPrice.setText("$" + room.getPrice() + " / night");
        holder.txtAmenities.setText(room.getAmenities());

        // Hiển thị rating mặc định từ Firestore trước khi tính toán trung bình
        holder.txtRating.setText("★ " + room.getRating());

        // Tính toán trung bình cộng rating từ collection "reviews"
        if (room.getHotel_id() != null) {
            db.collection("reviews")
                    .whereEqualTo("hotel_id", room.getHotel_id())
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            double totalRating = 0;
                            int count = 0;
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                Long ratingLong = document.getLong("rating");
                                if (ratingLong != null) {
                                    totalRating += ratingLong;
                                    count++;
                                }
                            }
                            if (count > 0) {
                                double average = totalRating / count;
                                // Hiển thị với 1 chữ số thập phân
                                holder.txtRating.setText(String.format(Locale.getDefault(), "★ %.1f", average));
                            }
                        }
                    });
        }

        // Lấy ảnh từ drawable dựa trên tên ảnh lưu trong Firestore (trường imageUrl)
        if (room.getImageUrl() != null && !room.getImageUrl().isEmpty()) {
            Context context = holder.itemView.getContext();
            int resId = context.getResources().getIdentifier(room.getImageUrl(), "drawable", context.getPackageName());
            if (resId != 0) {
                holder.imgRoom.setImageResource(resId);
            } else {
                holder.imgRoom.setImageResource(R.drawable.room1); // Mặc định nếu không tìm thấy
            }
        } else {
            holder.imgRoom.setImageResource(R.drawable.room1);
        }

        holder.btnViewDetails.setOnClickListener(v -> {
            if (listener != null) {
                listener.onBookNow(room);
            }
        });
    }

    @Override
    public int getItemCount() {
        return roomList.size();
    }

    public static class RoomViewHolder extends RecyclerView.ViewHolder {
        ImageView imgRoom;
        TextView txtName, txtType, txtPrice, txtRating, txtAmenities;
        Button btnViewDetails;

        public RoomViewHolder(@NonNull View itemView) {
            super(itemView);
            imgRoom = itemView.findViewById(R.id.imgRoom);
            txtName = itemView.findViewById(R.id.txtRoomName);
            txtType = itemView.findViewById(R.id.txtRoomType);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            txtRating = itemView.findViewById(R.id.txtRating);
            txtAmenities = itemView.findViewById(R.id.txtAmenities);
            btnViewDetails = itemView.findViewById(R.id.btnViewDetails);
        }
    }
}
