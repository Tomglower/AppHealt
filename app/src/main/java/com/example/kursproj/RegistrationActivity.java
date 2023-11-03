package com.example.kursproj;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kursproj.DB.DatabaseHelper;

public class RegistrationActivity extends AppCompatActivity {
    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button registerButton;
    private Button goLogButton;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        dbHelper = new DatabaseHelper(this);
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        registerButton = findViewById(R.id.registerButton);
        goLogButton = findViewById(R.id.goLog);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                long result = dbHelper.insertUser(username, password);
                if (result != -1) {
                    Toast RegOk = Toast.makeText(getApplicationContext(), "Регистрация прошла успешно!", Toast.LENGTH_SHORT);
                    RegOk.show();
                    startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
                }
            }
        });
        goLogButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
            }

        });
    }
}
