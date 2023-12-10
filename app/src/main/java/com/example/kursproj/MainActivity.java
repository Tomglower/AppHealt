package com.example.kursproj;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.Toast;

import com.example.kursproj.DB.DatabaseHelper;
import com.example.kursproj.DB.User;
import com.example.kursproj.Notification.NotificationWorker;

public class MainActivity extends AppCompatActivity {
    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private Button RegButton;
    private DatabaseHelper dbHelper;
    public int UserId;
    private CheckBox rememberMeCheckbox;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NotificationWorker.scheduleNotification();

        dbHelper = new DatabaseHelper(this);
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        RegButton = findViewById(R.id.goRegButton);
        rememberMeCheckbox = findViewById(R.id.rememberMeCheckbox);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        boolean rememberMe = sharedPreferences.getBoolean("remember_me", false);
        if (rememberMe) {
            int userId = sharedPreferences.getInt("user_id", -1);
            if (userId != -1) {
                startActivity(new Intent(MainActivity.this, MainScreenActivity.class));
                finish();
            }
        }
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                boolean rememberMe = rememberMeCheckbox.isChecked();
                User user = dbHelper.getUser(username, password);
                if (user != null) {
                    UserId = user.getId();
                    saveUserIdToSharedPreferences(UserId, rememberMe);

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
    private void saveUserIdToSharedPreferences(int userId, boolean rememberMe) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("user_id", userId);
        editor.putBoolean("remember_me", rememberMe);
        editor.apply();
    }





}
