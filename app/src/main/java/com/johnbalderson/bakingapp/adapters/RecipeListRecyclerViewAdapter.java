package com.johnbalderson.bakingapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.johnbalderson.bakingapp.activity.DetailActivity;
import com.johnbalderson.bakingapp.ExtrasData;
import com.johnbalderson.bakingapp.model.Recipe;
import com.johnbalderson.bakingapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 *  RecyclerView.Adapter that can display a Recipe and makes a call to the OnClickListener
 *
 */


public class RecipeListRecyclerViewAdapter extends
        RecyclerView.Adapter<RecipeListRecyclerViewAdapter.MyViewHolder> {

    private final String LOG_TAG = RecipeListRecyclerViewAdapter.class.getSimpleName();

    private final List<Recipe> mRecipes;
    public RecipeListRecyclerViewAdapter(List<Recipe> items) {
        mRecipes = items;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recipe_content, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        //viewHolder.onItemClickListener = mListener;
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Recipe recipe = mRecipes.get(position);
        Context context = holder.itemView.getContext();
        holder.titleTextView.setText(recipe.getName());
        holder.servingsTextView.setText(recipe.getServings());
        if (holder.photoImageView!=null && !recipe.getImage().isEmpty()) {

            Picasso.with(context)
                    .load(recipe.getImage())
                    .into(holder.photoImageView);

            }
        holder.recipe = recipe;

    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return mRecipes.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        public View mView;
        @BindView(R.id.servings_tv)
        TextView servingsTextView;
        @BindView(R.id.recipe_cv)
        public CardView cardView;
        @BindView(R.id.recipe_tv)
        public TextView titleTextView;
        @BindView(R.id.recipe_iv)
        public ImageView photoImageView;

        public Recipe recipe;

        public MyViewHolder(View view) {
            super(view);
            mView = view;
            ButterKnife.bind(this, view);

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mView.getContext(), DetailActivity.class);
                    intent.putExtra(ExtrasData.selectedRecipe, recipe);
                    mView.getContext().startActivity(intent);
                }
            });
        }

        @Override
        public void onClick(View v) {
            Toast.makeText(mView.getContext(), (recipe.getName()), Toast.LENGTH_SHORT).show();
        }
    }

}
