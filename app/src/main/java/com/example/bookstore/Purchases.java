package com.example.bookstore;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Purchases extends AppCompatActivity {

    private TextView totalSpentText;
    private LinearLayout booksListLayout;
    private TextView goBackText;
    private TextView customerIDText;

    private int customerID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_purchases); // use your XML file name

        totalSpentText = findViewById(R.id.totalSpent);
        booksListLayout = findViewById(R.id.booksList);
        goBackText = findViewById(R.id.goBack);
        customerIDText = findViewById(R.id.customerID);


        // Get customerID from intent
        customerID = getIntent().getIntExtra("customerID", -1);
        if (customerID == -1) {
            Toast.makeText(this, "Customer ID not found!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadCustomerPurchases();

        // Go back to MainActivity
        goBackText.setOnClickListener(view -> {
            Intent intent = new Intent(Purchases.this, MainActivity.class);
            intent.putExtra("customerID", customerID); // pass ID back
            startActivity(intent);
            finish();
        });
    }

    private void loadCustomerPurchases() {

        // Set the customer id
        customerIDText.setText("Customer ID: " + customerID + "\n");
        MySQLiteHelper db = new MySQLiteHelper(this);

        // Get total spent
        Cursor cursorTotal = db.getReadableDatabase().rawQuery(
                "SELECT totalAmountSpent FROM customer WHERE customerID = ?",
                new String[]{String.valueOf(customerID)}
        );

        if (cursorTotal.moveToFirst()) {
            double total = cursorTotal.getDouble(cursorTotal.getColumnIndexOrThrow("totalAmountSpent"));
            totalSpentText.setText("Total Amount Spent: $" + String.format("%.2f", total));
        }
        cursorTotal.close();

        // Get purchased books
        Cursor cursorBooks = db.getReadableDatabase().rawQuery(
                "SELECT b.title, b.price, p.quantity " +
                        "FROM purchases p " +
                        "JOIN book b ON p.bookISBN = b.bookISBN " +
                        "WHERE p.customerID = ?",
                new String[]{String.valueOf(customerID)}
        );

        booksListLayout.removeAllViews();

        if (cursorBooks.getCount() == 0) {
            TextView noBooks = new TextView(this);
            noBooks.setText("No purchases yet.");
            booksListLayout.addView(noBooks);
        } else {
            while (cursorBooks.moveToNext()) {
                String title = cursorBooks.getString(cursorBooks.getColumnIndexOrThrow("title"));
                double price = cursorBooks.getDouble(cursorBooks.getColumnIndexOrThrow("price"));
                int quantity = cursorBooks.getInt(cursorBooks.getColumnIndexOrThrow("quantity"));

                TextView bookView = new TextView(this);
                bookView.setText("Title: " + title + "\nQty: " + quantity + "\nPrice per book: $" + String.format("%.2f", price));
                bookView.setTextSize(16);
                bookView.setPadding(0, 8, 0, 8);

                booksListLayout.addView(bookView);
            }
        }

        cursorBooks.close();
    }
}
