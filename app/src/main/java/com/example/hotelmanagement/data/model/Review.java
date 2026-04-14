package com.example.hotelmanagement.data.model;

public class Review {
    public int id;
    public int user_id;
    public int hotel_id;
    public int rating;
    public String comment;

    public Review(){
        this.user_id=user_id;
        this.hotel_id=hotel_id;
        this.rating=rating;
        this.comment=comment;
    }
}
