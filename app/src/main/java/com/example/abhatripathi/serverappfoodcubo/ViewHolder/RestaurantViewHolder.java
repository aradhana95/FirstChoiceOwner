package com.example.abhatripathi.serverappfoodcubo.ViewHolder;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.abhatripathi.serverappfoodcubo.Interface.ItemClickListener;
import com.example.abhatripathi.serverappfoodcubo.R;

public class RestaurantViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView txtRestaurantName;
    public ImageView restaurantImage;

    private ItemClickListener itemClickListener;

    public RestaurantViewHolder(View itemView){
        super(itemView);

        txtRestaurantName= itemView.findViewById(R.id.restaurant_name);
        restaurantImage= itemView.findViewById(R.id.restaurant_image);
        itemView.setOnClickListener(this);

    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view)
    {
        itemClickListener.onClick(view, getAdapterPosition(), false);
    }
}
