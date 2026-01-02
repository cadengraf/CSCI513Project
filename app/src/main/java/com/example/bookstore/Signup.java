package com.example.bookstore;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;



public class Signup extends AppCompatActivity {

    private Button cancelButton, signupButton;
    private TextView textView, textView2;
    private EditText editText, editText2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        cancelButton = findViewById(R.id.cancelButton);
        signupButton = findViewById(R.id.signupButton);
        textView = findViewById(R.id.textView5);
        textView2 = findViewById(R.id.textView6);
        editText = findViewById(R.id.my_edit_text);
        editText2 = findViewById(R.id.my_edit_text2);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Signup.this, Login.class);
                startActivity(intent);
            }
        });

        textView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Signup.this, Admin.class);
                startActivity(intent);
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editText.setText("");
                editText2.setText("");
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkSignup();
            }
        });

    }

    private void checkSignup(){
        String username = editText.getText().toString();
        String password = editText2.getText().toString();

        if (username.isEmpty() || password.isEmpty()){
            showPopup("Please enter username and password");
            return;
        }

        MySQLiteHelper db = new MySQLiteHelper(this);
        if (db.checkSignup(username, password)){
            showPopup("Username is already in use try again!");
        }
        else {
            showPopup("Successfull signup! Going to login page");
            Intent intent = new Intent(Signup.this, Login.class);
            startActivity(intent);
        }

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
