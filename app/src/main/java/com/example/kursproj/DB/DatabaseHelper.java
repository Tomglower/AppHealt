package com.example.kursproj.DB;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.Arrays;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "users.db";
    private static final String TABLE_NAME = "user_table";
    private static final String COL_1 = "ID";
    private static final String COL_2 = "USERNAME";
    private static final String COL_3 = "PASSWORD";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 16);
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
                    "FOREIGN KEY (user_id) REFERENCES " + TABLE_NAME + "(ID))" // Создание внешнего ключа
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






}
