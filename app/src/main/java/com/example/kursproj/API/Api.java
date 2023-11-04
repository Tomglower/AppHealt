package com.example.kursproj.API;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
public class Api {
    private static final String NUTRITION_API_URL = "https://api.api-ninjas.com/v1/nutrition?query=";
    private static final String apiToken = "Pl4WRSqHTrMg/XrWetcpkw==8yXTQaPsbxpdM1jU";
    public static ProductInfo getNutritionInfo(String productName) {
        try {
            String apiUrl = NUTRITION_API_URL + productName;

            OkHttpClient client;
            client = new OkHttpClient();
            Request request = new Request.Builder()
                    .addHeader("X-Api-Key", apiToken)
                    .url(apiUrl)
                    .build();

            Response response = client.newCall(request).execute();
            String responseData = response.body().string();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(responseData);

            double calories = root.path("calories").asDouble();
            double fatTotal = root.path("fat_total_g").asDouble();
            double protein = root.path("protein_g").asDouble();
            double carbohydratesTotal = root.path("carbohydrates_total_g").asDouble();

            return new ProductInfo(calories, fatTotal, protein, carbohydratesTotal);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }





}
