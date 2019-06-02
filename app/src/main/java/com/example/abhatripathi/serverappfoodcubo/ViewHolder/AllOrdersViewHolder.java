package com.example.abhatripathi.serverappfoodcubo.ViewHolder;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.abhatripathi.serverappfoodcubo.Interface.ItemClickListener;
import com.example.abhatripathi.serverappfoodcubo.R;


public class AllOrdersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView txtRestaurantName;
    public ImageView restaurantImage;
    public FloatingActionButton btnRestaurantRating;

    private ItemClickListener itemClickListener;

    public AllOrdersViewHolder(View itemView){
        super(itemView);

        txtRestaurantName= itemView.findViewById(R.id.restaurant_name);
        restaurantImage= itemView.findViewById(R.id.restaurant_image);
//        btnRestaurantRating = itemView.findViewById(R.id.btn_restaurant_rating);
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
