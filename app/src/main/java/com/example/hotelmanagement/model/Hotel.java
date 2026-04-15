package com.example.hotelmanagement.model;

public class Hotel {
    public String id;
    public String name;
    public String address;
    public String description;
    public String images;
    public int price;
    public int category_id;
    public double rating;

    public Hotel() {
    }

    public Hotel(String id, String name, String address, String description, String images,
                 int price, int category_id, double rating) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.description = description;
        this.images = images;
        this.price = price;
        this.category_id = category_id;
        this.rating = rating;
    }

    public Hotel(String name, String address, String description, String images,
                 int price, int category_id, double rating) {
        this(null, name, address, description, images, price, category_id, rating);
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getDescription() { return description; }
    public String getImages() { return images; }
    public int getPrice() { return price; }
    public int getCategoryId() { return category_id; }
    public double getRating() { return rating; }
}
