package com.example.kursproj;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.kursproj.DB.DatabaseHelper;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainScreenActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private TextView userCaloriesTextView, totalCaloriesTextView;
    private PieChart pieChart;
    private PieChart pieChart2;
    private BarChart sportsBarChart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);



        bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        userCaloriesTextView = findViewById(R.id.user_calories_text_view);
        totalCaloriesTextView = findViewById(R.id.total_calories_text_view);
        pieChart = findViewById(R.id.calories_pie_chart1);
        pieChart2 = findViewById(R.id.calories_pie_chart2);
        displayUserCalories();
        displayTotalCalories();
        setupPieChart();
        displayTotalMacro();
        setupPieChart2();
        sportsBarChart = findViewById(R.id.sports_bar_chart);
        setupSportsBarChart();
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.action_item1) {

                    startActivity(new Intent(MainScreenActivity.this, MainScreenActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.action_item2) {
                    startActivity(new Intent(MainScreenActivity.this, FoodActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.action_item3) {
                    startActivity(new Intent(MainScreenActivity.this, SportActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.action_item4) {
                    startActivity(new Intent(MainScreenActivity.this, EditActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                }
                return false;
            }
        });
    }

    private void displayUserCalories() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        int userId = getUserId();
        int userCalories = dbHelper.getCaloriesForUser(userId);

        userCaloriesTextView.setText("Ваши калории: " + userCalories);
    }

    private void displayTotalCalories() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        int userId = getUserId();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = sdf.format(new Date());

        int totalProductCalories = dbHelper.getTotalProductCaloriesForDate(currentDate, userId);

        int totalSportCalories = dbHelper.getTotalSportCaloriesForDate(currentDate, userId);

        int totalCalories = totalProductCalories - totalSportCalories;

        totalCaloriesTextView.setText("Общие калории: " + totalCalories);
    }

    private int getUserId() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("user_id", 0);
        return userId;
    }

    private void setupPieChart() {
        PieData pieData = generatePieData();

        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);

        pieChart.setData(pieData);

        pieChart.invalidate();
    }

    private PieData generatePieData() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        int userId = getUserId();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = sdf.format(new Date());
        int totalProductCalories = dbHelper.getTotalProductCaloriesForDate(currentDate, userId);
        int totalSportCalories = dbHelper.getTotalSportCaloriesForDate(currentDate, userId);
        int totalCalories = totalProductCalories - totalSportCalories;

        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(totalCalories, "Сумма"));
        entries.add(new PieEntry(dbHelper.getCaloriesForUser(userId), "Норма"));

        PieDataSet dataSet = new PieDataSet(entries, "");

        int[] customColors = {Color.RED, Color.BLUE};
        dataSet.setColors(ColorTemplate.createColors(customColors));
        dataSet.setValueTextSize(12f);

        return new PieData(dataSet);
    }

    private void displayTotalMacro() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        int userId = getUserId();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = sdf.format(new Date());

        int totalProtein = dbHelper.getTotalProteinForDate(currentDate, userId);
        int totalFat = dbHelper.getTotalFatForDate(currentDate, userId);
        int totalCarbohydrates = dbHelper.getTotalCarbohydratesForDate(currentDate, userId);

        TextView totalProteinTextView = findViewById(R.id.total_protein_text_view);
        TextView totalFatTextView = findViewById(R.id.total_fat_text_view);
        TextView totalCarbohydratesTextView = findViewById(R.id.total_carbohydrates_text_view);

        totalProteinTextView.setText("Белки: " + totalProtein + " г");
        totalFatTextView.setText("Жиры: " + totalFat + " г");
        totalCarbohydratesTextView.setText("Углеводы: " + totalCarbohydrates + " г");
    }

    private void setupPieChart2() {
        PieData pieData = displayTotalPFC();

        pieChart2.setUsePercentValues(true);
        pieChart2.getDescription().setEnabled(false);

        pieChart2.setData(pieData);



        pieChart2.invalidate();
    }

    private PieData displayTotalPFC() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        int userId = getUserId();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = sdf.format(new Date());
        int totalProtein = dbHelper.getTotalProteinForDate(currentDate, userId);
        int totalFat = dbHelper.getTotalFatForDate(currentDate, userId);
        int totalCarbohydrates = dbHelper.getTotalCarbohydratesForDate(currentDate, userId);

        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(totalProtein, "Белки"));
        entries.add(new PieEntry(totalFat, "Жиры"));
        entries.add(new PieEntry(totalCarbohydrates, "Углеводы"));

        PieDataSet dataSet = new PieDataSet(entries, "");

        int[] customColors = {Color.rgb(255, 102, 0), Color.rgb(0, 153, 204), Color.rgb(51, 204, 51)};
        dataSet.setColors(ColorTemplate.createColors(customColors));
        dataSet.setValueTextSize(12f);

        return new PieData(dataSet);
    }
    private void setupSportsBarChart() {
        BarData barData = generateSportsBarData();

        sportsBarChart.getDescription().setEnabled(false);
        sportsBarChart.setData(barData);

        sportsBarChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        sportsBarChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(getDaysOfWeek()));
        sportsBarChart.getXAxis().setGranularity(1f);
        sportsBarChart.getAxisLeft().setGranularity(1f);

        sportsBarChart.invalidate();
    }

    private BarData generateSportsBarData() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        int userId = getUserId();

        // Получаем данные о тренировках для графика
        ArrayList<BarEntry> entries = dbHelper.getSportsBarEntriesForChart(userId);

        if (entries.isEmpty()) {
            // Обработка случая, когда нет данных (например, показ сообщения или обработка по вашему усмотрению)
            return null; // или возвращайте пустые данные, если это предпочтительнее
        }

        BarDataSet dataSet = new BarDataSet(entries, "Тренировки");
        dataSet.setColor(Color.BLUE);
        dataSet.setValueTextSize(10f);

        return new BarData(dataSet);
    }

    private String[] getDaysOfWeek() {
        // Возвращает массив дней недели для использования на оси X
        return new String[]{"Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс"};
    }





}
