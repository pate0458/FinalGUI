package com.prog.gui.guiprogramming.food_nut_db;

import java.io.Serializable;

class FoodModel implements Serializable {

    private String foodId, label, category, categoryLabel, tag;
    private double energy;

    String getFoodId() {
        return foodId;
    }

    void setFoodId(String foodId) {
        this.foodId = foodId;
    }

    String getLabel() {
        return label;
    }

    void setLabel(String label) {
        this.label = label;
    }

    String getCategory() {
        return category;
    }

    void setCategory(String category) {
        this.category = category;
    }

    String getCategoryLabel() {
        return categoryLabel;
    }

    void setCategoryLabel(String categoryLabel) {
        this.categoryLabel = categoryLabel;
    }

    String getTag() {
        return tag;
    }

    void setTag(String tag) {
        this.tag = tag;
    }

    double getEnergy() {
        return energy;
    }

    void setEnergy(double energy) {
        this.energy = energy;
    }
}