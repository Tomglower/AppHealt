package com.example.kursproj;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kursproj.DB.DatabaseHelper;
import com.example.kursproj.DB.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class EditActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private EditText editTextUsername;
    private EditText editTextPassword;
    private EditText editTextCalories;
    private Button saveButton;
    private DatabaseHelper dbHelper;
    private int userId;
    private TextView textViewUserId;
    private Button exit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        dbHelper = new DatabaseHelper(this);
        bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        CheckBox showPasswordCheckBox = findViewById(R.id.showPasswordCheckBox);
        editTextCalories = findViewById(R.id.editTextCalories);
        saveButton = findViewById(R.id.saveButton);
        textViewUserId = findViewById(R.id.textViewUserId);
        bottomNavigationView.setSelectedItemId(R.id.action_item4);
        exit = findViewById(R.id.exitButton);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("remember_me", false);
                editor.apply();

                Intent intent = new Intent(EditActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.action_item1) {
                    startActivity(new Intent(EditActivity.this, MainScreenActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.action_item2) {
                    startActivity(new Intent(EditActivity.this, FoodActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.action_item3) {
                    startActivity(new Intent(EditActivity.this, SportActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.action_item4) {
                    startActivity(new Intent(EditActivity.this, EditActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                }
                return false;
            }

        });

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        userId = sharedPreferences.getInt("user_id", 0);
        TextView textViewUserId = findViewById(R.id.textViewUserId);
        textViewUserId.setText("Ваш User_id: " + userId);
        User user = dbHelper.getUserById(userId);
        if (user != null) {
            editTextUsername.setText(user.getUsername());
            editTextPassword.setText(user.getPassword());

            int userCalories = dbHelper.getCaloriesForUser(userId);
            editTextCalories.setText(String.valueOf(userCalories));
        }

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String newUsername = editTextUsername.getText().toString();
                    String newPassword = editTextPassword.getText().toString();
                    int newCalories = Integer.parseInt(editTextCalories.getText().toString());

                    boolean userUpdated = dbHelper.updateUser(userId, newUsername, newPassword);

                    boolean caloriesUpdated = dbHelper.updateCalories(userId, newCalories);

                    if (userUpdated && caloriesUpdated) {
                        Toast updateOk = Toast.makeText(getApplicationContext(), "Обновление прошло успешно!", Toast.LENGTH_SHORT);
                        updateOk.show();

                    } else {
                        Toast updateBad = Toast.makeText(getApplicationContext(), "Ошибка обновления!", Toast.LENGTH_SHORT);
                        updateBad.show();
                    }
                } catch (NumberFormatException e) {
                    Toast errorToast = Toast.makeText(getApplicationContext(), "Ошибка: Введите корректное значение калорий", Toast.LENGTH_SHORT);
                    errorToast.show();
                } catch (Exception e) {
                    Toast errorToast = Toast.makeText(getApplicationContext(), "Произошла ошибка: " + e.getMessage(), Toast.LENGTH_SHORT);
                    errorToast.show();
                }
            }

        });
        showPasswordCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // В зависимости от состояния CheckBox устанавливаем/снимаем трансформацию для поля пароля
            if (isChecked) {
                editTextPassword.setTransformationMethod(null); // Показываем пароль
            } else {
                editTextPassword.setTransformationMethod(new PasswordTransformationMethod()); // Скрываем пароль
            }
        });
    }
}
