package com.example.varunkumar.ownerfirstchoice.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.varunkumar.ownerfirstchoice.Interface.ItemClickListener;
import com.example.varunkumar.ownerfirstchoice.R;

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
