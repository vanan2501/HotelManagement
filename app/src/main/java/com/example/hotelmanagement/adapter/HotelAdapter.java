package com.example.hotelmanagement.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hotelmanagement.R;
import com.example.hotelmanagement.model.Hotel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HotelAdapter extends RecyclerView.Adapter<HotelAdapter.ViewHolder> {

    public interface OnHotelClickListener {
        void onHotelClick(Hotel hotel);
    }

    private final Context context;
    private final List<Hotel> list;
    private final OnHotelClickListener listener;

    public HotelAdapter(Context context, List<Hotel> list) {
        this(context, list, null);
    }

    public HotelAdapter(Context context, List<Hotel> list, OnHotelClickListener listener) {
        this.context = context;
        this.list = list != null ? list : new ArrayList<>();
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtName;
        TextView txtAddress;
        TextView txtPrice;
        TextView txtRating;
        ImageView imgHotel;

        public ViewHolder(View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            txtAddress = itemView.findViewById(R.id.txtAddress);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            txtRating = itemView.findViewById(R.id.txtRating);
            imgHotel = itemView.findViewById(R.id.imgHotel);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_hotel, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Hotel hotel = list.get(position);

        holder.txtName.setText(safeText(hotel.name, "Serenity Suites"));
        holder.txtAddress.setText(safeText(hotel.address, "New York City"));
        holder.txtRating.setText(hotel.rating > 0 ? String.format(Locale.US, "%.1f", hotel.rating) : "4.8");
        int displayPrice = hotel.price > 0 ? hotel.price : 399;
        holder.txtPrice.setText(String.format(Locale.US, "$%d/night", displayPrice));

        if (hotel.images != null && !hotel.images.trim().isEmpty()) {
            Glide.with(context)
                    .load(hotel.images)
                    .placeholder(R.drawable.room2)
                    .error(R.drawable.room2)
                    .into(holder.imgHotel);
        } else {
            holder.imgHotel.setImageResource(R.drawable.room2);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onHotelClick(hotel);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void replaceData(List<Hotel> hotels) {
        list.clear();
        if (hotels != null) {
            list.addAll(hotels);
        }
        notifyDataSetChanged();
    }

    private String safeText(String value, String fallback) {
        return value == null || value.trim().isEmpty() ? fallback : value;
    }
}
