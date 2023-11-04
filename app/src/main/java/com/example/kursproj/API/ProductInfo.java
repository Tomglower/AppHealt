package com.example.kursproj.API;

public class ProductInfo {
    private double calories;
    private double fatTotal;
    private double protein;
    private double carbohydratesTotal;

    public ProductInfo(double calories, double fatTotal, double protein, double carbohydratesTotal) {
        this.calories = calories;
        this.fatTotal = fatTotal;
        this.protein = protein;
        this.carbohydratesTotal = carbohydratesTotal;
    }

    public double getCalories() {
        return calories;
    }

    public double getFatTotal() {
        return fatTotal;
    }

    public double getProtein() {
        return protein;
    }

    public double getCarbohydratesTotal() {
        return carbohydratesTotal;
    }
}
