package com.johnbalderson.bakingapp.model;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

// parcelable for Recipe

public class Recipe implements Parcelable{

    private String id;
    private String name;
    private ArrayList<Ingredients> ingredients;
    private ArrayList<Step> instructions;
    private String servings;
    private String image;
    public Recipe(){}

    // getters and setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Ingredients> getIngredients() {
        return ingredients;
    }

    public void setIngredients(ArrayList<Ingredients> ingredients) {
        this.ingredients = ingredients;
    }

    public ArrayList<Step> getInstructions() {
        return instructions;
    }

    public void setInstructions(ArrayList<Step> instruction) {
        this.instructions = instruction;
    }

    public String getServings() {
        return servings;
    }

    public void setServings(String servings) {
        this.servings = servings;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public int describeContents() {
        return 0;
    }


    private Recipe(Parcel in) {
        id = in.readString();
        name = in.readString();
        // ingredients = in.readArrayList(Ingredients.class.getClassLoader());
        ingredients = new ArrayList<>();
        in.readList(ingredients, Ingredients.class.getClassLoader());
        // instructions = in.readArrayList(Step.class.getClassLoader());
        instructions = new ArrayList<>();
        in.readList(instructions, Step.class.getClassLoader());
        servings = in.readString();
        image = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeList(ingredients);
        dest.writeList(instructions);
        dest.writeString(servings);
        dest.writeString(image);
    }

    public static Creator<Recipe> CREATOR = new Creator<Recipe>() {

        @Override
        public Recipe createFromParcel(Parcel source) {
            return new Recipe(source);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };
}
