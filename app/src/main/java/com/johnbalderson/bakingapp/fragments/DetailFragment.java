package com.johnbalderson.bakingapp.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;

import com.johnbalderson.bakingapp.activity.DetailActivity;
import com.johnbalderson.bakingapp.adapters.DetailRecyclerViewAdapter;
import com.johnbalderson.bakingapp.model.Ingredients;
import com.johnbalderson.bakingapp.model.Step;
import com.johnbalderson.bakingapp.model.Recipe;
import com.johnbalderson.bakingapp.R;
import com.johnbalderson.bakingapp.ExtrasData;


public class DetailFragment extends Fragment {

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();

    private static final String ARG_COLUMN_COUNT = "column-count";
    public int mColumnCount;
    private DetailRecyclerViewAdapter.ListItemClickListener mListener;
    private Recipe selectedRecipe;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public DetailFragment() {
    }


    @SuppressWarnings("unused")
    public static DetailFragment newInstance(int columnCount) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
        mListener = (DetailActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.detail_fragment, container, false);
        if (savedInstanceState!=null) {
            selectedRecipe = savedInstanceState.getParcelable(ExtrasData.selectedRecipe);
        } else {
            selectedRecipe = getArguments().getParcelable(ExtrasData.selectedRecipe);
        }
        TextView ingredients = view.findViewById(R.id.recipe_ingredients);
        ingredients.setText(getIngredientString(selectedRecipe.getIngredients()));

        RecyclerView recyclerView = view.findViewById(R.id.recipe_detail_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setNestedScrollingEnabled(false);

        DetailRecyclerViewAdapter mRecipeDetailAdapter =
                new DetailRecyclerViewAdapter(selectedRecipe.getInstructions(), mListener);
        recyclerView.setAdapter(mRecipeDetailAdapter);

        // BakingAppWidgetService.startActionUpdateWidget(getContext(), selectedRecipe);

        return view;
    }

    private String getIngredientString(List<Ingredients> ingredients) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i<ingredients.size(); i++) {
            // capitalize first letter of ingredient
            builder.append(ingredients.get(i).getIngredient().substring(0, 1).toUpperCase());
            // lowercase for rest of ingredient
            builder.append(ingredients.get(i).getIngredient().substring(1));
            builder.append(" ");
            // quantity
            builder.append(ingredients.get(i).getQuantity().toString());
            builder.append(" ");
            // measure
            builder.append(ingredients.get(i).getMeasure());
            builder.append("\n");
        }
        return builder.toString();
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelable(ExtrasData.selectedRecipe, selectedRecipe);
        super.onSaveInstanceState(savedInstanceState);
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(Step item);
    }
}
