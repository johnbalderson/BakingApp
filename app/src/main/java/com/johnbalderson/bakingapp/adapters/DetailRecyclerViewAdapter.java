package com.johnbalderson.bakingapp.adapters;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.johnbalderson.bakingapp.model.Step;
import com.johnbalderson.bakingapp.R;


import java.util.List;


public class DetailRecyclerViewAdapter extends RecyclerView.Adapter<DetailRecyclerViewAdapter.ViewHolder> {

    private final List<Step> mValues;
    private final ListItemClickListener mListener;
    public View view;

    public DetailRecyclerViewAdapter(List<Step> items, ListItemClickListener listener) {
        mValues = items;
        mListener = listener;
    }

    public interface ListItemClickListener {
        void onListItemClick(List<Step> steps, int clickedItemIndex);
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.detail_list_fragment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.mItem = mValues.get(position);
        holder.stepDescription.setText(mValues.get(position).getShortDescription());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onListItemClick(mValues,position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {
        public final View mView;
        public final TextView stepDescription;
        public CardView cardView;
        Step mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            stepDescription = (TextView) view.findViewById(R.id.stepDescription);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mListener.onListItemClick(mValues,clickedPosition);
        }
    }
}
