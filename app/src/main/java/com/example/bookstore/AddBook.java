package com.example.bookstore;

import androidx.appcompat.app.AppCompatActivity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class AddBook extends AppCompatActivity {

    // Declare input fields and buttons
    private EditText isbnInput, titleInput, priceInput, quantityInput;
    private Button addButton, cancelButton;
    private TextView textView, viewBooks, viewCustomerData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_books);

        // Link Java variables with layout components (by ID)
        isbnInput = findViewById(R.id.bookISBN);
        titleInput = findViewById(R.id.bookTitle);
        priceInput = findViewById(R.id.bookPrice);
        quantityInput = findViewById(R.id.bookQuantity);

        addButton = findViewById(R.id.addBook);
        cancelButton = findViewById(R.id.cancelButton);

        textView = findViewById(R.id.textView9);

        viewBooks = findViewById(R.id.viewBooks);
        viewCustomerData = findViewById(R.id.viewCustomerData);


        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addBookToDatabase();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearFields();
            }
        });

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go to login page
                Intent intent = new Intent(AddBook.this, Admin.class);
                startActivity(intent);
            }
        });

        viewBooks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddBook.this, AllBooks.class);
                startActivity(intent);
            }
        });

        viewCustomerData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddBook.this, ViewCustomerData.class);
                startActivity(intent);
            }
        });

    }

    private void addBookToDatabase() {
        String isbnStr = isbnInput.getText().toString().trim();
        String titleStr = titleInput.getText().toString().trim();
        String priceStr = priceInput.getText().toString().trim();
        String quantityStr = quantityInput.getText().toString().trim();

        // Validate fields
        if (isbnStr.isEmpty() || titleStr.isEmpty() || priceStr.isEmpty() || quantityStr.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isbnStr.length() != 10 || !isbnStr.matches("\\d{10}")){
            Toast.makeText(this, "ISBN is not 10 digits", Toast.LENGTH_SHORT).show();
            return;
        }

        double price;
        int quantity;
        try {
            price = Double.parseDouble(priceStr);
            quantity = Integer.parseInt(quantityStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid price or quantity", Toast.LENGTH_SHORT).show();
            return;
        }

        // Insert into SQLite
        MySQLiteHelper db = new MySQLiteHelper(this);
        boolean success = db.addBook(isbnStr, titleStr, price, quantity);

        if (!success){
            Toast.makeText(this, "Failed to add book", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Book added successfully", Toast.LENGTH_SHORT).show();
            clearFields();
        }
    }
    private void clearFields() {
        isbnInput.setText("");
        titleInput.setText("");
        priceInput.setText("");
        quantityInput.setText("");
    }


}
