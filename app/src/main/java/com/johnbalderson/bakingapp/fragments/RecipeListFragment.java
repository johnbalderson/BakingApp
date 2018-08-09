package com.johnbalderson.bakingapp.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.johnbalderson.bakingapp.adapters.RecipeListRecyclerViewAdapter;
import com.johnbalderson.bakingapp.model.Ingredients;
import com.johnbalderson.bakingapp.model.Step;
import com.johnbalderson.bakingapp.model.Recipe;
import com.johnbalderson.bakingapp.R;
import com.johnbalderson.bakingapp.ExtrasData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class RecipeListFragment extends Fragment {
    private static final String LOG_TAG = RecipeListFragment.class.getSimpleName();

    private static final String ARG_COLUMN_COUNT = "column-count";
    private String recipeURL =
            "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json";

    private int mColumnCount = 1;
    private RecipeListRecyclerViewAdapter adapter;

    private String saveId;
    private String saveStep;
    private int stepNumber;

    // for use in parsing recipeData JSON
    public String id;
    public String name;
    public Recipe recipeData;
    public Integer servings;
    public String image;
    public ArrayList<Ingredients> ingredients;
    public ArrayList<Step> steps;
    public ArrayList<Recipe> mRecipes = new ArrayList<>();


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RecipeListFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static RecipeListFragment newInstance(int columnCount) {
        RecipeListFragment fragment = new RecipeListFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recipe_fragment, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recipes);
        // Set the adapter
        RecyclerView.LayoutManager mLayoutManager = null;
        if (recyclerView !=null) {
            if (view.getTag()!=null && view.getTag().equals("tablet")){
                mLayoutManager = new GridLayoutManager(getContext(),3);
                recyclerView.setLayoutManager(mLayoutManager);
            }
            else {
                mLayoutManager = new LinearLayoutManager(getContext());
                recyclerView.setLayoutManager(mLayoutManager);
            }
            recyclerView.setLayoutManager(mLayoutManager);
            if (savedInstanceState!=null &&
                    savedInstanceState.getParcelableArrayList(ExtrasData.recipeList)!=null) {
                mRecipes = savedInstanceState.getParcelableArrayList(ExtrasData.recipeList);
            } else {
                queryRecipeApi();
            }
            adapter = new RecipeListRecyclerViewAdapter(mRecipes);
            recyclerView.setAdapter(adapter);
        }
        return view;
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "save instance state fragment");
        savedInstanceState.putParcelableArrayList(ExtrasData.recipeList, mRecipes);
        super.onSaveInstanceState(savedInstanceState);
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
        void onListFragmentInteraction(Recipe item);
    }

    /** implement Volley to parse JSON
     * https://developer.android.com/training/volley/request#java
     */
    private void queryRecipeApi() {
        final JsonArrayRequest mJsonObjectRequest = new JsonArrayRequest
                (recipeURL, new Response.Listener<JSONArray>()  {
                    @Override
                    public void onResponse(JSONArray jsonObject) {
                  try {
                      // save recipes
                   if (mRecipes == null) {
                       mRecipes = new ArrayList<>();
                   } else {
                       mRecipes.clear();
                          }
                   // Loop through the array, parse JSON
                   for (int i = 0; i < jsonObject.length(); i++) {

                       JSONObject recipe = jsonObject.getJSONObject(i);
                       // get id
                       id = recipe.getString("id");
                       // get name
                       name = recipe.getString("name");
                       // get number of servings
                       servings = recipe.getInt("servings");
                       // get image
                       image = recipe.getString("image");
                       // store items in array
                       recipeData = new Recipe();
                       recipeData.setId(id);
                       recipeData.setName(name);
                       recipeData.setServings(servings.toString());
                       recipeData.setImage(image);
                       ingredients = new ArrayList<>();
                       steps = new ArrayList<>();

                       // parse ingredients
                        JSONArray ingrArray = recipe.getJSONArray("ingredients");
                          for (int n = 0; n < ingrArray.length(); n++) {
                            JSONObject ingrItem = ingrArray.getJSONObject(n);
                            Ingredients ingr = new Ingredients();
                            ingr.setId(Integer.toString(n));
                            // get ingredient from within ingredients JSON
                            ingr.setIngredient(ingrItem.getString("ingredient"));
                            // get measure from within ingredients JSON
                            ingr.setMeasure(ingrItem.getString("measure"));
                            // get quantity from within ingredients JSON
                            ingr.setQuantity(ingrItem.getDouble("quantity"));
                            // add to ingredients array
                            ingredients.add(ingr); }
                            recipeData.setIngredients(ingredients);

                        // parse steps from JSON array
                        JSONArray stepArray = recipe.getJSONArray("steps");
                          for (int n = 0; n < stepArray.length(); n++) {
                            JSONObject step = stepArray.getJSONObject(n);
                            Step instr = new Step();
                            // number steps on display
                            // save id number to turn it into a step number
                            saveId = step.getString("id");
                            instr.setId(saveId);
                            stepNumber = Integer.valueOf(saveId);
                            // first step (intro) has number 0
                                 if (stepNumber > 0) {
                            // concatenate step number with period
                                 saveStep = saveId + ". ";
                                } else {
                                 saveStep = "";
                                }

                            // build description with step number and short description
                            instr.setShortDescription
                                    (saveStep + step.getString("shortDescription"));
                            // long description
                            instr.setDescription(step.getString("description"));
                            // video URL
                            instr.setVideoURL(step.getString("videoURL"));
                            // thumbnail URL
                            instr.setThumbnailURL(step.getString("thumbnailURL"));
                            // add to steps array
                            steps.add(instr);}
                            recipeData.setInstructions(steps);

                            mRecipes.add(recipeData);
                            }

                            adapter.notifyDataSetChanged();

                        }  // if there's a JSON exception
                        catch (JSONException e) {
                            Log.i(LOG_TAG, e.getMessage());
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Volley error
                        error.printStackTrace();
                        Log.i(LOG_TAG, error.getMessage());
                    }
                });

        // Queue the async request
        Volley.newRequestQueue(getActivity().
                getApplicationContext()).add(mJsonObjectRequest);
    }
}
