package com.example.hotelmanagement.data.model;

public class Payment {
    public int id;
    public int booking_id;
    public double amount;
    public String method;
    public String status;
    public Payment(){
        this.booking_id=booking_id;
        this.amount=amount;
        this.method=method;
        this.status="PENDING";
    }
}
