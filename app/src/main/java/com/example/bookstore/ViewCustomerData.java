package com.example.bookstore;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ViewCustomerData extends AppCompatActivity {

    RecyclerView recycler;
    ArrayList<Customer> customers;
    MySQLiteHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_customers);

        recycler = findViewById(R.id.recyclerCustomers);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        dbHelper = new MySQLiteHelper(this);

        customers = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT customerID, customerName FROM customer", null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String name = cursor.getString(1);

                customers.add(new Customer(id, name));
            } while (cursor.moveToNext());
        }
        cursor.close();

        CustomerListAdapter adapter = new CustomerListAdapter(customers, customer -> {
            Intent intent = new Intent(ViewCustomerData.this, CustomerDetails.class);
            intent.putExtra("customer_id", customer.getId());
            startActivity(intent);
        });

        recycler.setAdapter(adapter);
    }
}
