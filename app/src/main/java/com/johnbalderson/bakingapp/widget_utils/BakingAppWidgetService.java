package com.johnbalderson.bakingapp.widget_utils;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;


import com.johnbalderson.bakingapp.model.Recipe;


public class BakingAppWidgetService extends IntentService {

    public BakingAppWidgetService(){
        super("BakingAppWidgetService");
    }

    public static final String ACTION_UPDATE_WIDGET = "com.johnbalderson.bakingapp.action.update_update";
    public static final String RECIPE_ITEM_EXTRA_WIDGET = "recipe_item_extra_for_widget";

    //method that triggers the service to perform the update widget action
    public static void startActionUpdateWidget(Context context, Recipe recipeItem){
        Intent intent = new Intent(context, BakingAppWidgetService.class);
        intent.setAction(ACTION_UPDATE_WIDGET);
        intent.putExtra(RECIPE_ITEM_EXTRA_WIDGET, recipeItem);
        context.startService(intent);
        }


    //onHandleIntent extracts action and handles each action seperately
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
    if(intent != null){
        final String action = intent.getAction();
        if(ACTION_UPDATE_WIDGET.equals(action)){
            Recipe receivedRecipeItem = intent.getParcelableExtra(RECIPE_ITEM_EXTRA_WIDGET);
            handleUpdateWidget(receivedRecipeItem);
        }
    }
    }

    public void handleUpdateWidget(Recipe recipeItem){

        AppWidgetManager appWidgetManager  = AppWidgetManager.getInstance(this);
        int [] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,
                BakingAppWidget.class));
        //update all widgets
        BakingAppWidget.updateBakingAppWidgets(this, appWidgetManager, recipeItem, appWidgetIds);
    }
}
