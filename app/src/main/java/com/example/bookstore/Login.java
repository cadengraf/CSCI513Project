package com.example.bookstore;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class Login extends AppCompatActivity {

    private Button cancelButton, loginButton, clearSystemButton;
    private TextView textView, textView2;
    private EditText editText, editText2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        cancelButton = findViewById(R.id.cancelButton);
        loginButton = findViewById(R.id.loginButton);
        clearSystemButton = findViewById(R.id.clearSystemButton);
        textView = findViewById(R.id.textView5);
        textView2 = findViewById(R.id.textView6);
        editText = findViewById(R.id.my_edit_text);
        editText2 = findViewById(R.id.my_edit_text2);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, Signup.class);
                startActivity(intent);
            }
        });

        textView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, Admin.class);
                startActivity(intent);
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearFields();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkLogin();
            }
        });

        clearSystemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MySQLiteHelper db = new MySQLiteHelper(Login.this);
                showPopup("Database is being reset right now");
                db.clearSystem();
            }
        });

    }

    private void checkLogin() {
        String username = editText.getText().toString();
        String password = editText2.getText().toString();

        if (username.isEmpty() || password.isEmpty()) {
            showPopup("Please enter username and password");
            return;
        }

        MySQLiteHelper db = new MySQLiteHelper(this);
        int customerID = db.getCustomerID(username, password);

        if (customerID != -1) {  // user exists
            showPopup("Login successful :)");

            Intent intent = new Intent(Login.this, MainActivity.class);
            intent.putExtra("customerID", customerID);
            startActivity(intent);

        } else {
            showPopup("Login failed :(");
        }

        db.close();
    }


    private void clearFields() {
        editText.setText("");
        editText2.setText("");
    }

    private void showPopup(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setCancelable(true)
                .setPositiveButton("OK", (dialog, id) -> dialog.dismiss());
        AlertDialog alert = builder.create();
        alert.show();
    }


}
