package com.johnbalderson.bakingapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.List;

import com.johnbalderson.bakingapp.model.Step;
import com.johnbalderson.bakingapp.model.Recipe;
import com.johnbalderson.bakingapp.fragments.DetailFragment;
import com.johnbalderson.bakingapp.adapters.DetailRecyclerViewAdapter;
import com.johnbalderson.bakingapp.fragments.PreparationStepFragment;
import com.johnbalderson.bakingapp.R;
import com.johnbalderson.bakingapp.ExtrasData;
import com.johnbalderson.bakingapp.widget_utils.BakingAppWidget;

import butterknife.BindView;

public class DetailActivity extends AppCompatActivity implements DetailRecyclerViewAdapter.ListItemClickListener,
        PreparationStepFragment.ListItemClickListener {

    private Fragment mContent;
    private Fragment stepFragment;

    private Recipe selectedRecipe;

    private final static String LOG_TAG = DetailActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
        setToolbarListener(toolbar);

        if (savedInstanceState == null) {
            selectedRecipe = getIntent().getExtras().getParcelable(ExtrasData.selectedRecipe);
            getSupportActionBar().setTitle(selectedRecipe.getName());
            if((savedInstanceState==null)) {
                mContent = new DetailFragment();
                mContent.setArguments(getIntent().getExtras());
                getSupportFragmentManager().beginTransaction().replace(R.id.detail_content, mContent)
                        .commit();
            }
        }  else {
            selectedRecipe = savedInstanceState.getParcelable(getResources().getString(R.string.recipe));
            mContent = getSupportFragmentManager().getFragment(savedInstanceState, getResources().getString(R.string.stepFragment));
            stepFragment = getSupportFragmentManager().getFragment(savedInstanceState, getResources().getString(R.string.stepDetailFragment));
            int containerId = savedInstanceState.getInt(getResources().getString(R.string.containerId));
            
            if (mContent!=null && (stepFragment==null || findViewById(R.id.detail_coordinator_layout)!=null)) {
                
                getSupportFragmentManager().beginTransaction().replace(R.id.detail_content, mContent).commit();
            } else if (mContent==null && findViewById(R.id.detail_coordinator_layout)!=null) {
                mContent = new DetailFragment();
                mContent.setArguments(getIntent().getExtras());
                getSupportFragmentManager().beginTransaction().replace(R.id.detail_content, mContent)
                        .commit();

            }
            if (stepFragment!=null && containerId>0) {
                
                getSupportFragmentManager().beginTransaction().replace(containerId, stepFragment).commit();
            }
        }
    }

    private void setToolbarListener(Toolbar toolbar) {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                if (findViewById(R.id.prep_step_layout) != null) {
                    
                    Intent intent = new Intent(v.getContext(), DetailActivity.class);
                    intent.putExtra(ExtrasData.selectedRecipe, selectedRecipe);
                    v.getContext().startActivity(intent);
                } else {
                    
                    Intent intent = new Intent(v.getContext(), MainActivity.class);
                    v.getContext().startActivity(intent);
                }
            }
        });

    }

    @Override
    public void onListItemClick(List<Step> steps, int clickedItemIndex) {
        stepFragment = new PreparationStepFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ExtrasData.selectedRecipe, selectedRecipe);
        bundle.putInt(ExtrasData.itemClicked, clickedItemIndex);
        stepFragment.setArguments(bundle);
        if (findViewById(R.id.detail_coordinator_layout)!=null && findViewById(R.id.detail_coordinator_layout).getTag()!=null && findViewById(R.id.detail_coordinator_layout).getTag().equals("tablet")) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_content_steps, stepFragment).commit();

        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.detail_content, stepFragment).commit();
            mContent =null;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        
        if (mContent!=null) {
            getSupportFragmentManager().putFragment(savedInstanceState, getResources().getString(R.string.stepFragment), mContent);
        }
        if(stepFragment!=null) {
            getSupportFragmentManager().putFragment(savedInstanceState, getResources().getString(R.string.stepDetailFragment), stepFragment);
            ViewGroup vg = ((ViewGroup)stepFragment.getView().getParent());
            if (vg!=null) {
                savedInstanceState.putInt(getResources().getString(R.string.containerId), vg.getId());
            }
        }
        savedInstanceState.putParcelable(getResources().getString(R.string.recipe), selectedRecipe);
        super.onSaveInstanceState(savedInstanceState);
    }


}
