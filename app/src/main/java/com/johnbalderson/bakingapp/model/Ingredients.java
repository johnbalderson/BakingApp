package com.johnbalderson.bakingapp.model;

import android.os.Parcel;
import android.os.Parcelable;


// parcelable for Ingredients

public class Ingredients implements Parcelable{

    private String id;
    private Double quantity;
    private String measure;
    private String ingredient;
    private String recipeId;
    public Ingredients(){}

    // getters and setters
    public Double getQuantity() {
        return quantity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public String getMeasure() {
        return measure;
    }

    public void setMeasure(String measure) {
        this.measure = measure;
    }

    public String getIngredient() {
        return ingredient;
    }

    public String getIngredientString() {
        return ingredient +" ("+quantity+" "+measure+")";
    }

    public void setIngredient(String ingredient) {
        this.ingredient = ingredient;
    }

    public String getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(String recipeId) {
        this.recipeId = recipeId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeDouble(quantity);
        dest.writeString(measure);
        dest.writeString(ingredient);
        dest.writeString(recipeId);
    }

    public Ingredients(Parcel in) {
        id = in.readString();
        quantity = in.readDouble();
        measure = in.readString();
        ingredient = in.readString();
        recipeId = in.readString();
    }

    public static Creator<Ingredients> CREATOR = new Creator<Ingredients>() {

        @Override
        public Ingredients createFromParcel(Parcel source) {
            return new Ingredients(source);
        }

        @Override
        public Ingredients[] newArray(int size) {
            return new Ingredients[size];
        }
    };

}