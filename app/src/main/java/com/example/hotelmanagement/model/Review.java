package com.example.hotelmanagement.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.PropertyName;
import java.io.Serializable;
import java.util.Date;

public class Review implements Serializable {
    private String comment;
    private int rating;
    private Object create_at; // Sử dụng Object để lưu cả Timestamp và Date
    private String user_id;
    private String hotel_id;


    public Review() {
        // Required for Firebase
    }

    public Review(String comment, int rating, Object create_at, String user_id, String hotel_id) {
        this.comment = comment;
        this.rating = rating;
        this.create_at = create_at;
        this.user_id = user_id;
        this.hotel_id = hotel_id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    @PropertyName("create_at")
    public Object getCreate_at() {
        return create_at;
    }

    @PropertyName("create_at")
    public void setCreate_at(Object create_at) {
        this.create_at = create_at;
    }

    // Hàm helper để lấy Date từ Timestamp
    public Date getCreateAtDate() {
        if (create_at instanceof Timestamp) {
            return ((Timestamp) create_at).toDate();
        } else if (create_at instanceof Date) {
            return (Date) create_at;
        }
        return null;
    }

    @PropertyName("user_id")
    public String getUser_id() {
        return user_id;
    }

    @PropertyName("user_id")
    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    @PropertyName("hotel_id")
    public String getHotel_id() {
        return hotel_id;
    }

    @PropertyName("hotel_id")
    public void setHotel_id(String hotel_id) {
        this.hotel_id = hotel_id;
    }


}
