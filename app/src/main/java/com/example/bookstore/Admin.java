package com.example.bookstore;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Admin extends AppCompatActivity {

    private Button cancelButton, loginButton;
    private EditText username, password;
    private TextView notAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        cancelButton = findViewById(R.id.cancelButton);
        loginButton = findViewById(R.id.loginButton);
        username = findViewById(R.id.my_edit_text);
        password = findViewById(R.id.my_edit_text2);
        notAdmin = findViewById(R.id.textView6);

        notAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Admin.this, Login.class);
                startActivity(intent);
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username.setText("");
                password.setText("");
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkLogin();
            }
        });
    }

    private void checkLogin() {
        String username_check = username.getText().toString();
        String password_check = password.getText().toString();

        if (username_check.isEmpty() || password_check.isEmpty()){
            showPopup("Please enter username and password");
            return;
        }
        MySQLiteHelper db = new MySQLiteHelper(this);
        if (db.checkAdmin(username_check, password_check)) {
            showPopup("Admin Login Good!");
            Intent intent = new Intent(Admin.this, AddBook.class);
            startActivity(intent);
        }
        else{
            showPopup("Admin Login failed :(");
        }
        db.close();
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
