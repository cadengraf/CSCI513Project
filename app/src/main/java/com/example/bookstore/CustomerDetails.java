package com.example.bookstore;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class CustomerDetails extends AppCompatActivity {

    TextView txtId, txtName, txtTotalPaid, txtBooks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_details);

        txtId = findViewById(R.id.detailCustomerId);
        txtName = findViewById(R.id.detailCustomerName);
        txtTotalPaid = findViewById(R.id.detailTotalPaid);
        txtBooks = findViewById(R.id.detailBooks);

        int customerId = getIntent().getIntExtra("customer_id", -1);

        MySQLiteHelper dbHelper = new MySQLiteHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor c = db.rawQuery(
                "SELECT customerID, customerName, totalAmountSpent FROM customer WHERE customerID = ?",
                new String[]{String.valueOf(customerId)}
        );

        if (c.moveToFirst()) {
            txtId.setText("Customer ID: " + c.getInt(0));
            txtName.setText("Name: " + c.getString(1));
            txtTotalPaid.setText(String.format("Total Paid: $%.2f", c.getDouble(2)));
        }
        c.close();

        Cursor books = db.rawQuery(
                "SELECT b.title FROM purchases p JOIN book b ON p.bookISBN = b.bookISBN WHERE p.customerID = ?",
                new String[]{String.valueOf(customerId)}
        );

        StringBuilder bookList = new StringBuilder();

        if (books.moveToFirst()) {
            do {
                bookList.append("• ").append(books.getString(0)).append("\n");
            } while (books.moveToNext());
        } else {
            bookList.append("No purchases.");
        }

        txtBooks.setText(bookList.toString());

        books.close();
    }
}
