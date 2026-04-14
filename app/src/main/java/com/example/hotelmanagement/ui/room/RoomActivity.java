package com.example.hotelmanagement.ui.room;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hotelmanagement.R;
import com.example.hotelmanagement.model.Room;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class RoomActivity extends AppCompatActivity {
    EditText edtName, edtType, edtPrice;
    Button btnAdd;
    ListView listRoom;

    FirebaseFirestore db;

    ArrayList<String> list;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        edtName = findViewById(R.id.edtName);
        edtType = findViewById(R.id.edtType);
        edtPrice = findViewById(R.id.edtPrice);
        btnAdd = findViewById(R.id.btnAdd);
        listRoom = findViewById(R.id.listRoom);

        db = FirebaseFirestore.getInstance();

        list = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
        listRoom.setAdapter(adapter);

        loadRooms();

        // ADD ROOM
        btnAdd.setOnClickListener(v -> {

            String name = edtName.getText().toString().trim();
            String type = edtType.getText().toString().trim();
            String priceStr = edtPrice.getText().toString().trim();

            if (name.isEmpty() || type.isEmpty() || priceStr.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            double price;

            try {
                price = Double.parseDouble(priceStr);
            } catch (Exception e) {
                Toast.makeText(this, "Price invalid", Toast.LENGTH_SHORT).show();
                return;
            }

            String id = db.collection("rooms").document().getId();

            Room r = new Room(
                    id,
                    "HOTEL_01",
                    name,
                    type,
                    price,
                    "0",
                    "",
                    ""
            );

            db.collection("rooms").document(id)
                    .set(r)
                    .addOnSuccessListener(a -> {
                        Toast.makeText(this, "Add room success", Toast.LENGTH_SHORT).show();
                        loadRooms(); // 🔥 tốt hơn finish()
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });

        // DELETE ROOM
        listRoom.setOnItemLongClickListener((parent, view, position, id) -> {

            String roomId = list.get(position).split(" - ")[0];

            db.collection("rooms").document(roomId).delete()
                    .addOnSuccessListener(a -> loadRooms());

            return true;
        });
    }

    private void loadRooms() {
        list.clear();

        db.collection("rooms").get()
                .addOnSuccessListener(task -> {

                    for (DocumentSnapshot doc : task) {

                        Room r = doc.toObject(Room.class);

                        if (r != null) {
                            list.add(
                                    r.getId() + " - " +
                                            r.getHotel_name() + " - " +
                                            r.getType() + " - " +
                                            r.getPrice()
                            );
                        }
                    }

                    adapter.notifyDataSetChanged();
                });
    }
}
