package com.example.hotelmanagement.model;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.PropertyName;
import java.io.Serializable;

public class Trip implements Serializable {
    private String id;
    private String checkin_date;
    private String checkout_date;
    private String hotel_id;
    private String special_request;
    private String status;
    private double total_price;
    private String user_id;

    public Trip() {
        // Required for Firebase
    }

    public Trip(String checkin_date, String checkout_date, String hotel_id, String special_request, 
                String status, double total_price, String user_id) {
        this.checkin_date = checkin_date;
        this.checkout_date = checkout_date;
        this.hotel_id = hotel_id;
        this.special_request = special_request;
        this.status = status;
        this.total_price = total_price;
        this.user_id = user_id;
    }

    @Exclude
    public String getId() { return id; }
    @Exclude
    public void setId(String id) { this.id = id; }

    @PropertyName("checkin_date")
    public String getCheckin_date() { return checkin_date; }
    @PropertyName("checkin_date")
    public void setCheckin_date(String checkin_date) { this.checkin_date = checkin_date; }

    @PropertyName("checkout_date")
    public String getCheckout_date() { return checkout_date; }
    @PropertyName("checkout_date")
    public void setCheckout_date(String checkout_date) { this.checkout_date = checkout_date; }

    @PropertyName("hotel_id")
    public String getHotel_id() { return hotel_id; }
    @PropertyName("hotel_id")
    public void setHotel_id(String hotel_id) { this.hotel_id = hotel_id; }

    @PropertyName("special_request")
    public String getSpecial_request() { return special_request; }
    @PropertyName("special_request")
    public void setSpecial_request(String special_request) { this.special_request = special_request; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @PropertyName("total_price")
    public double getTotal_price() { return total_price; }
    @PropertyName("total_price")
    public void setTotal_price(double total_price) { this.total_price = total_price; }

    @PropertyName("user_id")
    public String getUser_id() { return user_id; }
    @PropertyName("user_id")
    public void setUser_id(String user_id) { this.user_id = user_id; }

    @Exclude
    public String getDates() { return checkin_date + " - " + checkout_date; }
}
