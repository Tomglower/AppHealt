package com.example.kursproj;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
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

public class FoodActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private EditText etProductName, etProtein, etFat, etCarbohydrates, etGrams;
    private Button btnSaveProduct;
    private DatabaseHelper dbHelper;
    private ListView listViewProducts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food);

        bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setSelectedItemId(R.id.action_item2);

        dbHelper = new DatabaseHelper(this);

        etProductName = findViewById(R.id.etProductName);
        etProtein = findViewById(R.id.etProtein);
        etFat = findViewById(R.id.etFat);
        etCarbohydrates = findViewById(R.id.etCarbohydrates);
        etGrams = findViewById(R.id.etGrams);
        btnSaveProduct = findViewById(R.id.btnSaveProduct);

        listViewProducts = findViewById(R.id.listViewProducts);
        displayProductsForToday();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.action_item1) {
                    startActivity(new Intent(FoodActivity.this, MainScreenActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.action_item2) {
                    startActivity(new Intent(FoodActivity.this, FoodActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.action_item3) {
                    startActivity(new Intent(FoodActivity.this, SportActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.action_item4) {
                    startActivity(new Intent(FoodActivity.this, EditActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                }
                return false;
            }
        });
        btnSaveProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String productName = etProductName.getText().toString();
                String proteinStr = etProtein.getText().toString();
                String fatStr = etFat.getText().toString();
                String carbohydratesStr = etCarbohydrates.getText().toString();
                String gramsStr = etGrams.getText().toString();

                if (productName.isEmpty() || proteinStr.isEmpty() || fatStr.isEmpty() || carbohydratesStr.isEmpty() || gramsStr.isEmpty()) {
                    Toast.makeText(FoodActivity.this, "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show();
                    return;
                }

                double protein, fat, carbohydrates, grams;

                try {
                    protein = Double.parseDouble(proteinStr);
                    fat = Double.parseDouble(fatStr);
                    carbohydrates = Double.parseDouble(carbohydratesStr);
                    grams = Double.parseDouble(gramsStr);
                } catch (NumberFormatException e) {
                    Toast.makeText(FoodActivity.this, "Пожалуйста, введите корректные числовые значения", Toast.LENGTH_SHORT).show();
                    return;
                }

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                String dateAdded = sdf.format(new Date());

                SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                int userId = sharedPreferences.getInt("user_id", 0);

                long result = dbHelper.insertProduct(productName, grams, dateAdded, protein, fat, carbohydrates, userId);

                if (result != -1) {
                    Toast.makeText(FoodActivity.this, "Продукт успешно сохранен", Toast.LENGTH_SHORT).show();
                    displayProductsForToday();
                    etProductName.setText("");
                    etProtein.setText("");
                    etFat.setText("");
                    etCarbohydrates.setText("");
                    etGrams.setText("");
                } else {
                    Toast.makeText(FoodActivity.this, "Произошла ошибка при сохранении продукта", Toast.LENGTH_SHORT).show();
                }
            }

        });



    }
    private void displayProductsForToday() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String todayDate = sdf.format(new Date());

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("user_id", 0);

        Cursor cursor = dbHelper.getProductsForDate(todayDate, userId);

        String[] fromColumns = {
                "product_name",
                "calories",
                "fat",
                "protein",
                "carbohydrates",
                "grams"

        };

        int[] toViews = {
                R.id.productNameValue,
                R.id.caloriesValue,
                R.id.fatValue,
                R.id.proteinValue,
                R.id.carbohydratesValue,
                R.id.gramsValue

        };

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                this,
                R.layout.product_list_item,
                cursor,
                fromColumns,
                toViews,
                0
        );
        listViewProducts.setAdapter(adapter);
    }
}