package com.example.bookstore;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class BookDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);

        TextView title = findViewById(R.id.detailTitle);
        TextView isbn = findViewById(R.id.detailIsbn);
        TextView price = findViewById(R.id.detailPrice);
        TextView qty = findViewById(R.id.detailQuantity);

        // Receive book data from intent
        String t = getIntent().getStringExtra("title");
        String i = getIntent().getStringExtra("isbn");
        double p = getIntent().getDoubleExtra("price", 0.0);
        int q = getIntent().getIntExtra("quantity", 0);

        title.setText(t);
        isbn.setText("ISBN: " + i);
        price.setText("Price: $" + p);
        qty.setText("Available: " + q);
    }
}
