package com.johnbalderson.bakingapp.fragments;


import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.johnbalderson.bakingapp.activity.DetailActivity;
import com.johnbalderson.bakingapp.R;


import java.util.List;


import com.johnbalderson.bakingapp.model.Step;
import com.johnbalderson.bakingapp.model.Recipe;
import com.johnbalderson.bakingapp.ExtrasData;
import com.squareup.picasso.Picasso;

public class PreparationStepFragment extends Fragment {

    private static final String LOG_TAG = PreparationStepFragment.class.getSimpleName();

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    int currentWindow;

    private ListItemClickListener mListener;
    private Recipe selectedRecipe;
    private Integer clickedIndex;
    private PlayerView playerView;
    TextView textView;
    Toolbar toolbar;
    private SimpleExoPlayer player;
    ImageView thumbImage;

    private long playbackPosition = 0;
    private boolean playbackReady = true;
    Uri mediaUri;

    public String saveVideoURL;
    public String saveThumbURL;




    public PreparationStepFragment() {
        // Required empty public constructor
    }



    public interface ListItemClickListener {
        void onListItemClick(List<Step> allSteps, int Index);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PreparationStepFragment.
     */
    @SuppressWarnings("unused")
    public static PreparationStepFragment newInstance(String param1, String param2) {
        PreparationStepFragment fragment = new PreparationStepFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        mListener = (DetailActivity) getActivity();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();

        if (savedInstanceState != null) {
            selectedRecipe = savedInstanceState.getParcelable(ExtrasData.selectedRecipe);
            clickedIndex = savedInstanceState.getInt(ExtrasData.itemClicked);
            playbackPosition = savedInstanceState.getLong(ExtrasData.playerPosition);
            playbackReady = savedInstanceState.getBoolean(ExtrasData.playerWhenReady);
        } else {
            selectedRecipe = getArguments().getParcelable(ExtrasData.selectedRecipe);
            clickedIndex = getArguments().getInt(ExtrasData.itemClicked);
        }

        View view = inflater.inflate(R.layout.prep_step_fragment, container, false);

        textView =  view.findViewById(R.id.recipe_step_detail_text);
        if (textView != null) {
            if (textView.getTag() != null && textView.getTag().equals("landscape")) {
                ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
                View decorView = getActivity().getWindow().getDecorView();
                int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
                decorView.setSystemUiVisibility(uiOptions);
            }
            textView.setText(selectedRecipe.getInstructions().get(clickedIndex).getDescription());
            textView.setVisibility(View.VISIBLE);
        }

        playerView =  view.findViewById(R.id.playerView);
        playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
        thumbImage =  view.findViewById(R.id.step_iv);

        String imageURL = selectedRecipe.getInstructions().get(clickedIndex).getThumbnailURL();
        if (imageURL !=null) {

            Picasso.with(getContext()).
                    load(Uri.parse(imageURL )
                            .buildUpon()
                            .build())
                            .into(thumbImage);
        }

        String videoURL = selectedRecipe.getInstructions().get(clickedIndex).getVideoURL();
        saveVideoURL = videoURL;
        String thumbURL = selectedRecipe.getInstructions().get(clickedIndex).getThumbnailURL();
        saveThumbURL = thumbURL;
        mediaUri = Uri.parse(videoURL);
        testThumbURL();


        if (videoURL!=null && !videoURL.isEmpty()) {
            playerView.setVisibility(View.VISIBLE);
            initializeExoPlayer(mediaUri);
            if (textView.getTag()!=null && textView.getTag().equals("sw600dp")) {
                playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH);

            }
            else if (textView.getTag()!=null && textView.getTag().equals("landscape")
                    && isInLandscapeMode(getContext())){
                textView.setVisibility(View.GONE);
            }
        }
        else {
            // turn off video player and show mixing bowl and instructions
            player=null;
            playerView.setVisibility(View.GONE);
            thumbImage.setImageResource(R.drawable.mixingbowl);
            thumbImage.setVisibility(View.VISIBLE);
        }

        Button mPrevStep =  view.findViewById(R.id.previous_button);
        Button mNextStep =  view.findViewById(R.id.next_button);
        mPrevStep.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (clickedIndex>0) {
                    if (player !=null){
                        player.stop();
                    }
                    clickedIndex = clickedIndex - 1;
                    mListener.onListItemClick(selectedRecipe.getInstructions(),clickedIndex);
                }
                // show that you have reached the first step after clicking previous
                else {
                    Toast.makeText(getActivity(),getResources().getString(R.string.first_step),
                            Toast.LENGTH_SHORT).show();

                }
            }});

        mNextStep.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                int lastIndex = selectedRecipe.getInstructions().size()-1;
                if (clickedIndex<lastIndex) {
                    if (player !=null){
                        player.stop();
                    }
                    clickedIndex = clickedIndex + 1;
                    mListener.onListItemClick(selectedRecipe.getInstructions(),clickedIndex);
                }
                else {
                    // show that you have reached the last step after clicking next

                    Toast.makeText(getContext(),getResources().getString(R.string.last_step),
                            Toast.LENGTH_SHORT).show();

                }
            }});
        return view;
    }

    public boolean isInLandscapeMode( Context context ) {
        return (context.getResources().
                getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE);
    }

    // initialize ExoPlayer
    private void initializeExoPlayer(Uri mediaUri) {
         if (player == null) {
            // Create an instance of the ExoPlayer.
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            player = ExoPlayerFactory.newSimpleInstance((getActivity()), trackSelector, loadControl);

            playerView.setPlayer(player);
            playerView.setVisibility(View.VISIBLE);

            // Prepare the MediaSource
            String userAgent = Util.getUserAgent(getActivity().
                        getBaseContext(), getString(R.string.app_name));

            MediaSource mediaSource = new ExtractorMediaSource(mediaUri,
                        new DefaultDataSourceFactory(getActivity(), userAgent), new DefaultExtractorsFactory(),
                        null, null);

            if (playbackPosition != C.TIME_UNSET) {
                player.seekTo(playbackPosition);
                }

            player.prepare(mediaSource);
            player.setPlayWhenReady(playbackReady);
             /**
              * currentWindow -    The index of the window.
              playbackPosition - The seek position in the specified window.
              */
             player.seekTo(currentWindow, playbackPosition);
         }
    }



    @Override
    public void onSaveInstanceState(Bundle currentState) {
        super.onSaveInstanceState(currentState);
        currentState.putParcelable(ExtrasData.selectedRecipe,selectedRecipe);
        currentState.putInt(ExtrasData.itemClicked,clickedIndex);

        //save state so that upon rotation, the video doesn't restart
        if(player != null){
            playbackPosition = player.getCurrentPosition();
            playbackReady = player.getPlayWhenReady();
            currentWindow = player.getCurrentWindowIndex();
        }
        currentState.putLong(ExtrasData.playerPosition, playbackPosition);
        currentState.putBoolean(ExtrasData.playerWhenReady, playbackReady);
    }

    public void testThumbURL() {
        if (!saveThumbURL.isEmpty()) {
            playerView.setVisibility(View.INVISIBLE);
            Picasso.with(getActivity()).load(saveThumbURL)
                    .placeholder(R.drawable.mixingbowl)
                    .error(R.drawable.mixingbowl)
                    .into(thumbImage);
            thumbImage.setVisibility(View.INVISIBLE);

        }
    }

    // control operation of ExoPlayer - only available after SDK 23
    @Override
    public void onStart() {
        super.onStart();
        // if (saveVideoURL != null ) {
        if (!saveVideoURL.isEmpty()) {
            if (Util.SDK_INT > 23) {
                initializeExoPlayer(Uri.parse(saveVideoURL));
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
         // if (saveVideoURL != null) {
        if (!saveVideoURL.isEmpty()) {
            if ((Util.SDK_INT <= 23 || player == null)) {
                initializeExoPlayer(Uri.parse(saveVideoURL));
            }
         }
    }
    /**
     * Keep position and state and release the player when the activity is paused.
     */
    @Override
    public void onPause() {
        super.onPause();

        if (player != null) {
            playbackPosition = player.getCurrentPosition();
            playbackReady = player.getPlayWhenReady();

            if (Util.SDK_INT <= 23) {
                releasePlayer();
            }
        }
    }
    /**
     * Release the player when the activity is stopped.
     */
    @Override
    public void onStop() {
        super.onStop();
        if (player != null) {
            if (Util.SDK_INT > 23) {
                releasePlayer();
            }
        }
    }

    /**
     * Release ExoPlayer.
     */
    private void releasePlayer() {
        player.stop();
        player.release();
        player = null;
        }



    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(Uri uri);
    }
}
