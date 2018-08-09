package com.johnbalderson.bakingapp.widget_utils;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import com.johnbalderson.bakingapp.ExtrasData;
import com.johnbalderson.bakingapp.activity.MainActivity;
import com.johnbalderson.bakingapp.model.Ingredients;
import com.johnbalderson.bakingapp.model.Recipe;
import com.johnbalderson.bakingapp.R;

import java.util.List;

/**
 * Implementation of App Widget functionality.
 */
public class BakingAppWidget extends AppWidgetProvider {

    private static Recipe selectedRecipe = null;
    private static List<Ingredients> mIngredients;


    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, Recipe recipeItem) {

        selectedRecipe = recipeItem;

        CharSequence widgetTitle;
        CharSequence widgetText;

        if (selectedRecipe == null){
            widgetTitle = context.getString(R.string.widget_no_recipe);
            widgetText = context.getString(R.string.widget_select_recipe);
        } else
            {

            //get ingredients in string format
            mIngredients = selectedRecipe.getIngredients();

            StringBuilder builder = new StringBuilder();

            for (int i = 0; i < mIngredients.size(); i++){

                Ingredients ingredient = mIngredients.get(i);
                // capitalize first letter of ingredient
                builder.append(ingredient.getIngredient().substring(0, 1).toUpperCase());
                // lowercase for rest of ingredient
                builder.append(ingredient.getIngredient().substring(1));
                String ingrName = ingredient.getIngredient();
                Log.i("ingr", ingrName);
                builder.append(" ");
                // quantity
                builder.append(ingredient.getQuantity().toString());
                builder.append(" ");
                // measure
                builder.append(ingredient.getMeasure());
                builder.append("\n");
               /* Ingredients passedIngredient = mIngredients.get(i);
                String ingredientDetail = String.valueOf(passedIngredient.getQuantity()) + " " +
                        passedIngredient.getMeasure() + " " + passedIngredient.getIngredient() + "\n";
                builder.append(ingredientDetail);*/
            }


            widgetTitle = selectedRecipe.getName();
            widgetText = builder.toString();
        }

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.baking_app_widget);
        views.setTextViewText(R.id.appwidget_title, widgetTitle);
        views.setTextViewText(R.id.appwidget_text, widgetText);

        //set Pending intent to open MainActivity upon clicking
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.appwidget_title, pendingIntent);
        views.setOnClickPendingIntent(R.id.appwidget_text, pendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }



    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        BakingAppWidgetService.startActionUpdateWidget(context, selectedRecipe);
    }

    public static void updateBakingAppWidgets(Context context, AppWidgetManager appWidgetManager,
                                              Recipe recipeItem, int[] appWidgetIds) {
        // This scrolls through all instances of a widget, and updates them
        //method called upon creation and upon timed-update as defined in the xml file
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, recipeItem);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        selectedRecipe = intent.getParcelableExtra(ExtrasData.RECIPE_TO_WIDGET);
        BakingAppWidgetService.startActionUpdateWidget(context, selectedRecipe);
    }

}

