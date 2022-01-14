package com.example.varunkumar.ownerfirstchoice.ViewHolder;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.varunkumar.ownerfirstchoice.R;

import info.hoang8f.widget.FButton;

public class RestaurantOrderHolder extends RecyclerView.ViewHolder  {
    public TextView txtOrderId,shipper_assignment_status, txtOrderStatus ,txtOrderDate;
    public FButton btnDetails,btnShipping;
    public FloatingActionButton btn_call_shipper;

    public RestaurantOrderHolder(View itemView){
        super(itemView);
        shipper_assignment_status = itemView.findViewById(R.id.shipper_assignment_status);
        txtOrderId = itemView.findViewById(R.id.order_id);
        txtOrderStatus = itemView.findViewById(R.id.order_status);
        txtOrderDate = itemView.findViewById(R.id.order_date);
        btn_call_shipper = itemView.findViewById(R.id.btn_call_shipper);


        btnDetails=itemView.findViewById(R.id.btnDetails);
        btnShipping=itemView.findViewById(R.id.btnShipping);

    }

}
