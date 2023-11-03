package com.example.kursproj;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.example.kursproj.DB.DatabaseHelper;
import com.example.kursproj.DB.User;

public class MainActivity extends AppCompatActivity {
    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private Button RegButton;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        RegButton = findViewById(R.id.goRegButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                User user = dbHelper.getUser(username, password);
                if (user != null) {
                    Toast LogOk = Toast.makeText(getApplicationContext(), "Авторизация прошла успешно!", Toast.LENGTH_SHORT);
                    LogOk.show();
                    startActivity(new Intent(MainActivity.this, MainScreenActivity.class));


                } else {
                    Toast LogNoOk = Toast.makeText(getApplicationContext(), "Ошибка авторизации, проверьте логин и пароль", Toast.LENGTH_SHORT);
                    LogNoOk.show();
                }
            }
        });

        RegButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RegistrationActivity.class));
            }

        });

    }
}