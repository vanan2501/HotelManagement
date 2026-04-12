package com.example.hotelmanagement.model;

import com.google.firebase.firestore.PropertyName;
import java.io.Serializable;

public class Room implements Serializable {
    private String id;
    private String hotel_id; // Field hotel_id từ Firebase
    private String hotel_name;
    private String type;
    private double price;
    private String rating;
    private String imageUrl;
    private String amenities;

    public Room() {
        // Required for Firebase
    }

    public Room(String id, String hotel_id, String hotel_name, String type, double price, String rating, String imageUrl, String amenities) {
        this.id = id;
        this.hotel_id = hotel_id;
        this.hotel_name = hotel_name;
        this.type = type;
        this.price = price;
        this.rating = rating;
        this.imageUrl = imageUrl;
        this.amenities = amenities;
    }

    // Getters
    public String getId() { return id; }
    
    @PropertyName("hotel_id")
    public String getHotel_id() { return hotel_id; }

    @PropertyName("hotel_name")
    public String getHotel_name() { return hotel_name; }
    
    public String getName() { return hotel_name; }

    public String getType() { return type; }
    public double getPrice() { return price; }
    public String getRating() { return rating; }
    public String getImageUrl() { return imageUrl; }
    public String getAmenities() { return amenities; }
    
    // Setters
    public void setId(String id) { this.id = id; }

    @PropertyName("hotel_id")
    public void setHotel_id(String hotel_id) { this.hotel_id = hotel_id; }

    @PropertyName("hotel_name")
    public void setHotel_name(String hotel_name) { this.hotel_name = hotel_name; }

    public void setType(String type) { this.type = type; }
    public void setPrice(double price) { this.price = price; }
    public void setRating(String rating) { this.rating = rating; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setAmenities(String amenities) { this.amenities = amenities; }
}
