package com.example.kursproj;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.example.kursproj.DB.DatabaseHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SportActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private EditText etExerciseType, etDuration, etCalories;
    private Button btnSaveActivity;
    private DatabaseHelper dbHelper;
    private ListView listViewActivities;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sport);
        bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setSelectedItemId(R.id.action_item3);
        dbHelper = new DatabaseHelper(this);

        etExerciseType = findViewById(R.id.etExerciseType);
        etDuration = findViewById(R.id.etDuration);
        etCalories = findViewById(R.id.etCalories);
        btnSaveActivity = findViewById(R.id.btnSaveActivity);

        listViewActivities = findViewById(R.id.listViewActivities);
        displayActivitiesForToday();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.action_item1) {
                    startActivity(new Intent(SportActivity.this, MainScreenActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.action_item2) {
                    startActivity(new Intent(SportActivity.this, FoodActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.action_item3) {
                    startActivity(new Intent(SportActivity.this, SportActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.action_item4) {
                    startActivity(new Intent(SportActivity.this, EditActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                }
                return false;
            }
        });
        btnSaveActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String exerciseType = etExerciseType.getText().toString();
                String durationStr = etDuration.getText().toString();
                String caloriesStr = etCalories.getText().toString();

                if (exerciseType.isEmpty() || durationStr.isEmpty() || caloriesStr.isEmpty()) {
                    Toast.makeText(SportActivity.this, "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show();
                    return;
                }

                int duration, calories;

                try {
                    duration = Integer.parseInt(durationStr);
                    calories = Integer.parseInt(caloriesStr);
                } catch (NumberFormatException e) {
                    Toast.makeText(SportActivity.this, "Пожалуйста, введите корректные числовые значения", Toast.LENGTH_SHORT).show();
                    return;
                }

                SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                int userId = sharedPreferences.getInt("user_id", 0);

                long result = dbHelper.insertSport(exerciseType, duration, calories, userId);

                if (result != -1) {
                    Toast.makeText(SportActivity.this, "Спортивная активность успешно сохранена", Toast.LENGTH_SHORT).show();
                    displayActivitiesForToday();
                    etExerciseType.setText("");
                    etDuration.setText("");
                    etCalories.setText("");
                } else {
                    Toast.makeText(SportActivity.this, "Произошла ошибка при сохранении спортивной активности", Toast.LENGTH_SHORT).show();
                }
            }
        });
        listViewActivities.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showDeleteDialog(id); // Показываем диалог удаления при долгом нажатии
                return true;
            }
        });
    }
    private void showDeleteDialog(final long activityId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Удаление активности")
                .setMessage("Вы уверены, что хотите удалить активность?")
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteActivity(activityId);
                    }
                })
                .setNegativeButton("Отмена", null)
                .show();
    }

    private void deleteActivity(long activityId) {
        int result = dbHelper.deleteSport(activityId);

        if (result > 0) {
            Toast.makeText(this, "Активность успешно удалена", Toast.LENGTH_SHORT).show();
            displayActivitiesForToday();
        } else {
            Toast.makeText(this, "Ошибка при удалении активности", Toast.LENGTH_SHORT).show();
        }
    }
    private void displayActivitiesForToday() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String todayDate = sdf.format(new Date());

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("user_id", 0);

        Cursor cursor = dbHelper.getSportsForDate(todayDate, userId);

        String[] fromColumns = {
                "exercise_type",
                "duration",
                "calories"
        };

        int[] toViews = {
                R.id.exerciseTypeValue,
                R.id.durationValue,
                R.id.caloriesValue
        };

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                this,
                R.layout.sport_list_item,
                cursor,
                fromColumns,
                toViews,
                0
        );
        listViewActivities.setAdapter(adapter);
    }
}