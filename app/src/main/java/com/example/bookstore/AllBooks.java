package com.example.bookstore;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AllBooks extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BookAdapter adapter;
    private ArrayList<Book> bookList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_books);

        recyclerView = findViewById(R.id.recyclerViewBooks);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        bookList = new ArrayList<>();

        // Get data from SQLite database
        MySQLiteHelper dbHelper = new MySQLiteHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT bookISBN, title, price, quantity FROM book", null);

        if (cursor.moveToFirst()) {
            do {
                String isbn = cursor.getString(0);
                String title = cursor.getString(1);
                double price = cursor.getDouble(2);
                int qty = cursor.getInt(3);

                bookList.add(new Book(isbn, title, price, qty));
            } while (cursor.moveToNext());
        }
        cursor.close();

        adapter = new BookAdapter(bookList);
        recyclerView.setAdapter(adapter);
    }
}
