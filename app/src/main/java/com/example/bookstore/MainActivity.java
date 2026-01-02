package com.example.bookstore;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BookAdapter bookAdapter;
    private RadioButton rAll, r0to10, r10to20, r20to30, r30plus;
    private Button searchBtn, purchaseBtn;
    private TextView signOut, viewPurchases;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        recyclerView = findViewById(R.id.recyclerViewBooks);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Radio buttons
        rAll = findViewById(R.id.radioButton);
        r0to10 = findViewById(R.id.radioButton2);
        r10to20 = findViewById(R.id.radioButton3);
        r20to30 = findViewById(R.id.radioButton4);
        r30plus = findViewById(R.id.radioButton5);

        searchBtn = findViewById(R.id.addBookButton2);
        purchaseBtn = findViewById(R.id.purchaseButton);

        signOut = findViewById(R.id.signOutText);
        viewPurchases = findViewById(R.id.viewPurchaseData);

        loadAllBooks(); // initial load

        searchBtn.setOnClickListener(v -> applyFilter());

        purchaseBtn.setOnClickListener(v -> purchaseSelectedBooks());

        signOut.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Intent intent = new Intent(MainActivity.this, Login.class);
               startActivity(intent);
           }
        });

        viewPurchases.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Purchases.class);
                intent.putExtra("customerID", getIntent().getIntExtra("customerID", -1));
                startActivity(intent);
            }
        });

    }

    private void loadAllBooks() {
        ArrayList<Book> books = getAllBooks();
        bookAdapter = new BookAdapter(books);
        recyclerView.setAdapter(bookAdapter);
    }

    private void applyFilter() {
        double min = 0, max = Double.MAX_VALUE;

        if (rAll.isChecked()) { min = 0; max = 9999; }
        if (r0to10.isChecked()) { min = 0; max = 10; }
        if (r10to20.isChecked()) { min = 10; max = 20; }
        if (r20to30.isChecked()) { min = 20; max = 30; }
        if (r30plus.isChecked()) { min = 30; max = 9999; }

        ArrayList<Book> filteredBooks = getFilteredBooks(min, max);

        bookAdapter = new BookAdapter(filteredBooks);
        recyclerView.setAdapter(bookAdapter);
    }

    private void purchaseSelectedBooks() {
        ArrayList<Book> chosen = bookAdapter.getSelectedBooks();

        if (chosen.isEmpty()) {
            Toast.makeText(this, "No books selected", Toast.LENGTH_SHORT).show();
            return;
        }

        int customerID = getIntent().getIntExtra("customerID", -1);

        MySQLiteHelper db = new MySQLiteHelper(this);

        String resultMessage = db.purchaseBooks(chosen, customerID);

        Toast.makeText(this, resultMessage, Toast.LENGTH_LONG).show();

        loadAllBooks();
    }

    private ArrayList<Book> getAllBooks() {

        MySQLiteHelper db = new MySQLiteHelper(this);
        Cursor cursor = db.getAllBooks();

        ArrayList<Book> books = new ArrayList<>();
        while (cursor.moveToNext()) {
            books.add(new Book(
                    cursor.getString(cursor.getColumnIndexOrThrow("bookISBN")),
                    cursor.getString(cursor.getColumnIndexOrThrow("title")),
                    cursor.getDouble(cursor.getColumnIndexOrThrow("price")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("quantity"))
            ));
        }
        cursor.close();
        return books;
    }


    private ArrayList<Book> getFilteredBooks(double min, double max) {
        ArrayList<Book> list = getAllBooks();
        ArrayList<Book> filtered = new ArrayList<>();

        for (Book b : list) {
            if (b.getPrice() >= min && b.getPrice() <= max) {
                filtered.add(b);
            }
        }

        return filtered;
    }

}
