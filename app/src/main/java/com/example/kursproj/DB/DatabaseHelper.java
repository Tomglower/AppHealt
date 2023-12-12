package com.example.kursproj.DB;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "users.db";
    private static final String TABLE_NAME = "user_table";
    private static final String COL_1 = "ID";
    private static final String COL_2 = "USERNAME";
    private static final String COL_3 = "PASSWORD";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 17);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL("CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "USERNAME TEXT, PASSWORD TEXT)");
            db.execSQL("CREATE TABLE calories (ID INTEGER PRIMARY KEY AUTOINCREMENT, user_id INTEGER, calories INTEGER, FOREIGN KEY (user_id) REFERENCES user_table(ID))");
            db.execSQL("CREATE TABLE products (" +
                    "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "calories REAL," +
                    "product_name TEXT," +
                    "grams REAL," +
                    "date_added TEXT," +
                    "protein REAL, " +
                    "fat REAL, " +
                    "carbohydrates REAL, " +
                    "user_id INTEGER, " +
                    "FOREIGN KEY (user_id) REFERENCES " + TABLE_NAME + "(ID))");
            db.execSQL("CREATE TABLE sports (" +
                    "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "exercise_type TEXT, " +
                    "duration INTEGER, " +
                    "calories INTEGER, " +
                    "date_added TEXT, " +
                    "user_id INTEGER, " +
                    "FOREIGN KEY (user_id) REFERENCES " + TABLE_NAME + "(ID))"

            );

            Log.e("CreateTable", "Таблицы создались ");
        } catch (Exception e) {
            Log.e("CreateTable", "Ошибка при создании таблиц: " + e.getMessage());
        }
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS calories");
        db.execSQL("DROP TABLE IF EXISTS products");
        onCreate(db);
    }
    public long insertUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, username);
        contentValues.put(COL_3, password);

        // Вставляем пользователя и получаем его ID
        long userId = db.insert(TABLE_NAME, null, contentValues);

        // Создаем запись с calories = 0 для этого пользователя в таблице "calories"
        ContentValues caloriesValues = new ContentValues();
        caloriesValues.put("user_id", userId); // Привязываем к пользователю
        caloriesValues.put("calories", 0); // Устанавливаем начальное значение
        db.insert("calories", null, caloriesValues);

        return userId;
    }


    public User getUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " +
                COL_2 + " = ? AND " + COL_3 + " = ?", new String[]{username, password});

        if (cursor.moveToFirst()) {
            int id = cursor.getInt(0);
            String storedUsername = cursor.getString(1);
            String storedPassword = cursor.getString(2);
            return new User(id, storedUsername, storedPassword);
        } else {
            return null;
        }
    }

    public User getUserById(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COL_1 + " = ?";
        String[] selectionArgs = { String.valueOf(userId) };
        Cursor cursor = db.rawQuery(query, selectionArgs);

        if (cursor.moveToFirst()) {
            int id = cursor.getInt(0);
            String username = cursor.getString(1);
            String password = cursor.getString(2);
            return new User(id, username, password);
        } else {
            return null;
        }
    }
    public boolean updateUser(int userId, String newUsername, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, newUsername);
        contentValues.put(COL_3, newPassword);

        String whereClause = COL_1 + " = ?";
        String[] whereArgs = { String.valueOf(userId) };

        int rowsUpdated = db.update(TABLE_NAME, contentValues, whereClause, whereArgs);

        return rowsUpdated > 0;
    }
    public int getCaloriesForUser(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT calories FROM calories WHERE user_id = ?";
        String[] selectionArgs = { String.valueOf(userId) };
        Cursor cursor = db.rawQuery(query, selectionArgs);

        if (cursor.moveToFirst()) {
            return cursor.getInt(0);
        } else {
            return 0;
        }
    }

    public boolean updateCalories(int userId, int calories) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("calories", calories);

        String whereClause = "user_id = ?";
        String[] whereArgs = { String.valueOf(userId) };
        Log.d("UpdateCalories", "userId: " + userId);
        Log.d("UpdateCalories", "calories: " + calories);
        Log.d("UpdateCalories", "whereClause: " + whereClause);
        Log.d("UpdateCalories", "whereArgs: " + Arrays.toString(whereArgs));
        try {
            int rowsUpdated = db.update("calories", contentValues, whereClause, whereArgs);
            if (rowsUpdated > 0) {
                return true;
            } else {
                Log.e("UpdateCalories", "No rows were updated.");
                return false;
            }
        } catch (Exception e) {
            Log.e("UpdateCalories", "Error updating calories: " + e.getMessage());
            return false;
        }
    }
    public long insertProduct(String productName, double grams, String dateAdded, double protein, double fat, double carbohydrates, int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("product_name", productName);
        contentValues.put("calories", (protein *4) + (fat * 9) + (carbohydrates *4));
        contentValues.put("grams", grams);
        contentValues.put("date_added", dateAdded);
        contentValues.put("protein", protein);
        contentValues.put("fat", fat);
        contentValues.put("carbohydrates", carbohydrates);
        contentValues.put("user_id", userId);

        long productId = db.insert("products", null, contentValues);

        return productId;
    }

    public Cursor getProductsForDate(String date, int userId) {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT ID AS _id, product_name,calories, protein, fat, carbohydrates, grams FROM products WHERE date_added = ? AND user_id = ?";
        String[] selectionArgs = { date, String.valueOf(userId) };

        return db.rawQuery(query, selectionArgs);
    }


    public long insertSport(String exerciseType, int duration, int calories, int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("exercise_type", exerciseType);
        contentValues.put("duration", duration);
        contentValues.put("calories", calories);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String dateAdded = sdf.format(new Date());
        contentValues.put("date_added", dateAdded);

        contentValues.put("user_id", userId);

        return db.insert("sports", null, contentValues);
    }

    public Cursor getSportsForDate(String date, int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT ID AS _id, exercise_type, duration, calories FROM sports WHERE date_added = ? AND user_id = ?";
        String[] selectionArgs = {date, String.valueOf(userId)};
        return db.rawQuery(query, selectionArgs);
    }

    public int getTotalProductCaloriesForDate(String date, int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT SUM(calories) FROM products WHERE date_added = ? AND user_id = ?";
        String[] selectionArgs = {date, String.valueOf(userId)};
        Cursor cursor = db.rawQuery(query, selectionArgs);

        if (cursor.moveToFirst()) {
            return cursor.getInt(0);
        } else {
            return 0;
        }
    }

    public int getTotalSportCaloriesForDate(String date, int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT SUM(calories) FROM sports WHERE date_added = ? AND user_id = ?";
        String[] selectionArgs = {date, String.valueOf(userId)};
        Cursor cursor = db.rawQuery(query, selectionArgs);

        if (cursor.moveToFirst()) {
            return cursor.getInt(0);
        } else {
            return 0;
        }
    }
    public int getTotalProteinForDate(String date, int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT SUM(protein) FROM products WHERE date_added = ? AND user_id = ?";
        String[] selectionArgs = {date, String.valueOf(userId)};
        Cursor cursor = db.rawQuery(query, selectionArgs);

        if (cursor.moveToFirst()) {
            return cursor.getInt(0);
        } else {
            return 0;
        }
    }

    public int getTotalFatForDate(String date, int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT SUM(fat) FROM products WHERE date_added = ? AND user_id = ?";
        String[] selectionArgs = {date, String.valueOf(userId)};
        Cursor cursor = db.rawQuery(query, selectionArgs);

        if (cursor.moveToFirst()) {
            return cursor.getInt(0);
        } else {
            return 0;
        }
    }

    public int getTotalCarbohydratesForDate(String date, int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT SUM(carbohydrates) FROM products WHERE date_added = ? AND user_id = ?";
        String[] selectionArgs = {date, String.valueOf(userId)};
        Cursor cursor = db.rawQuery(query, selectionArgs);

        if (cursor.moveToFirst()) {
            return cursor.getInt(0);
        } else {
            return 0;
        }
    }

    public ArrayList<BarEntry> getSportsBarEntriesForChart(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Создаем временную таблицу с днями недели от 0 до 6 (включительно)
        db.execSQL("CREATE TEMPORARY TABLE IF NOT EXISTS days_of_week (day_of_week INTEGER PRIMARY KEY);");
        db.execSQL("INSERT OR IGNORE INTO days_of_week VALUES (0), (1), (2), (3), (4), (5), (6);");

        // Запрос с использованием LEFT JOIN для включения всех дней недели
        String query = "SELECT days_of_week.day_of_week, COUNT(sports.date_added) " +
                "FROM days_of_week " +
                "LEFT JOIN sports ON days_of_week.day_of_week = strftime('%w', sports.date_added) AND sports.user_id = ? " +
                "GROUP BY days_of_week.day_of_week " +
                "ORDER BY days_of_week.day_of_week;";

        String[] selectionArgs = {String.valueOf(userId)};
        Cursor cursor = db.rawQuery(query, selectionArgs);

        ArrayList<BarEntry> entries = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                int dayOfWeek = cursor.getInt(0);
                int count = cursor.getInt(1);

                entries.add(new BarEntry(dayOfWeek, count));
            } while (cursor.moveToNext());
        }

        return entries;
    }


}
