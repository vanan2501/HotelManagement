package com.example.hotelmanagement.model;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class BookingManager {
    private static BookingManager instance;
    private List<Trip> bookings;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private BookingManager() {
        bookings = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    public static synchronized BookingManager getInstance() {
        if (instance == null) {
            instance = new BookingManager();
        }
        return instance;
    }

    /**
     * Lấy danh sách bookings từ Firebase dựa trên user_id tùy chỉnh (ví dụ: "user_1")
     * Bước 1: Lấy UID hiện tại.
     * Bước 2: Truy vấn bảng "users" để lấy field "user_id". Nếu null, dùng UID của Firebase làm thay thế.
     * Bước 3: Dùng query_id đó để lọc bảng "bookings".
     */
    public void fetchBookings(OnBookingsLoadedListener listener) {
        if (auth.getCurrentUser() == null) {
            if (listener != null) listener.onError("User not logged in");
            return;
        }

        String uid = auth.getCurrentUser().getUid();

        // 1. Lấy thông tin user_id từ collection "users"
        db.collection("users").document(uid).get().addOnSuccessListener(documentSnapshot -> {
            String customUserId = null;
            if (documentSnapshot.exists()) {
                customUserId = documentSnapshot.getString("user_id");
            }

            // Nếu user_id null hoặc empty, sử dụng UID (auth.uid) làm thay thế
            final String queryUserId = (customUserId != null && !customUserId.isEmpty()) ? customUserId : uid;

            // 2. Truy vấn bảng "bookings" bằng queryUserId
            db.collection("bookings")
                    .whereEqualTo("user_id", queryUserId)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        bookings.clear();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Trip trip = document.toObject(Trip.class);
                            trip.setId(document.getId());
                            bookings.add(trip);
                        }
                        if (listener != null) listener.onSuccess(bookings);
                    })
                    .addOnFailureListener(e -> {
                        if (listener != null) listener.onError(e.getMessage());
                    });

        }).addOnFailureListener(e -> {
            if (listener != null) listener.onError(e.getMessage());
        });
    }

    public List<Trip> getBookings() {
        return bookings;
    }
    
    public void setBookings(List<Trip> bookings) {
        this.bookings = bookings;
    }

    public interface OnBookingsLoadedListener {
        void onSuccess(List<Trip> tripList);
        void onError(String error);
    }
}
