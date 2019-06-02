package com.example.abhatripathi.serverappfoodcubo;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Toast;

import com.example.abhatripathi.serverappfoodcubo.Common.Common;
import com.example.abhatripathi.serverappfoodcubo.Remote.APIService1;
import com.example.abhatripathi.serverappfoodcubo.ViewHolder.RestaurantOrderHolder;
import com.example.abhatripathi.serverappfoodcubo.model.MyResponse;
import com.example.abhatripathi.serverappfoodcubo.model.Notification;
import com.example.abhatripathi.serverappfoodcubo.model.Request;
import com.example.abhatripathi.serverappfoodcubo.model.RequestOld;
import com.example.abhatripathi.serverappfoodcubo.model.Sender;
import com.example.abhatripathi.serverappfoodcubo.model.Token;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RestaurantOrdersListAdapter extends RecyclerView.Adapter<RestaurantOrderHolder>  {

    private final OrderStatusRestaurant mContext;
    private final ArrayList<RequestOld> restaurantlist;
    FirebaseDatabase db;
    DatabaseReference requests;
    APIService1 mService1;

    public RestaurantOrdersListAdapter(OrderStatusRestaurant context, ArrayList<RequestOld> restaurantlist){
        this.mContext= context;
        this.restaurantlist=restaurantlist;

        db = FirebaseDatabase.getInstance();
        requests = db.getReference("Requests");
        mService1 = Common.getFCMClient1();

    }

    @Override
    public RestaurantOrderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.restaurant_order_layout,parent,false);
        return new RestaurantOrderHolder(itemView);
    }

    public String sendSms(String msg,String phonenumber) {
        try {
            // Construct data
            String apiKey = "apikey=" + "uRlkT7rVE04-N0fmmUN5Q23xwENdH63iejlr4NO7k0";
            String message = "&message=" + "Message from FOOD CUBO "+msg;
            String sender = "&sender=" + "TXTLCL";
            String numbers = "&numbers=" + phonenumber;
            // Send data
            HttpURLConnection conn = (HttpURLConnection) new URL("https://api.textlocal.in/send/?").openConnection();
            String data = apiKey + numbers + message + sender;
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Length", Integer.toString(data.length()));
            conn.getOutputStream().write(data.getBytes("UTF-8"));
            final BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            final StringBuffer stringBuffer = new StringBuffer();
            String line;
            while ((line = rd.readLine()) != null) {
                System.out.println("FOOD CUBO " + line);
                stringBuffer.append(line);
            }
            rd.close();

            return stringBuffer.toString();
        } catch (Exception e) {
            System.out.println("Error SMS " + e);
            return "Error " + e;
        }
    }


    private void sendOrderStatusToUser(final String key, final RequestOld item) {
        DatabaseReference tokens = db.getReference("Tokens");
        tokens.child(item.getPhone())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Token token = dataSnapshot.getValue(Token.class);

                            Notification notification = new Notification
                                    ("HELLO ", "Shipper recieved your order " + key + " from restaurant");
                            Sender content = new Sender(token.getToken(), notification);
                            sendSms("HELLO  Shipper recieved your order " + key + " from restaurant",dataSnapshot.getKey());
                            mService1.sendNotification(content)
                                    .enqueue(new Callback<MyResponse>() {
                                        @Override
                                        public void onResponse(@NonNull Call<MyResponse> call, @NonNull Response<MyResponse> response) {
                                            System.out.println("jiji....." + response);
                                            if (response.code() == 200) {
                                                if (response.body().success == 1) {
                                                    Toast.makeText(mContext, "Order was Updated !", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(mContext, "Order was Updated but failed to send notification !", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<MyResponse> call, Throwable t) {

                                            Log.e("ERROR", t.getMessage());
                                        }
                                    });

                            /*Map<String, String> dataSend = new HashMap<>();
                            dataSend.put("title", "HELLO CLIENT");
                            dataSend.put("message", "Your order " + key + " was updated");
                            assert token != null;
                            DataMessage dataMessage = new DataMessage(token.getToken(), dataSend);
                            mService.sendNotification(dataMessage)
                                    .enqueue(new Callback<MyResponse>() {
                                        @Override
                                        public void onResponse(@NonNull Call<MyResponse> call, @NonNull Response<MyResponse> response) {
                                            if (response.body() != null) {
                                                if (response.body().success == 1) {
                                                    Toast.makeText(OrderStatus.this, "Order was Updated !", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(OrderStatus.this, "Order was Updated but failed to send notification !", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<MyResponse> call, Throwable t) {
                                            Log.e("Error", t.getMessage());

                                        }
                                    });*/
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public void onBindViewHolder(@NonNull final RestaurantOrderHolder viewHolder, final int position) {
        final RequestOld model = restaurantlist.get(viewHolder.getAdapterPosition());
        viewHolder.txtOrderId.setText(model.getKey());
        viewHolder.txtOrderStatus.setText(Common.convertCodeToStatus(model.getStatus()));
        if(model.getKey()!=null)
        viewHolder.txtOrderDate.setText(Common.getDate(Long.parseLong(model.getKey())));
        //new event button
        if(model.getStatus().equals("1")){
            viewHolder.btnShipping.setVisibility(View.VISIBLE);
        }
        else
            viewHolder.btnShipping.setVisibility(View.GONE);
        viewHolder.btnShipping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                model.setStatus("2");
                requests.child(model.getKey()).setValue(model);
                sendOrderStatusToUser(model.getKey(),model);
            }
        });
        viewHolder.btnDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent orderDetail = new Intent(mContext, OrderDetail.class);
                Common.currentRequest = model;
                orderDetail.putExtra("OrderId", model.getKey());
                mContext.startActivity(orderDetail);
            }
        });

/*
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Requests");
        tokens.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot requestSnapshot : dataSnapshot.getChildren())
                {
                    System.out.println("koko................"+requestSnapshot);
                    if(requestSnapshot.getKey().equals(model.getKey())){
                        if(!requestSnapshot.child("status").getValue(String.class).equals("0")) {
                            model.setShipperAssigned(true);
                            model.setShipperNumber(requestSnapshot.child("tempShipper").getValue(String.class));
                            viewHolder.shipper_assignment_status.setText("Shipper Assigned");
                            viewHolder.btn_call_shipper.setVisibility(View.VISIBLE);
                        }
                        else {
                            model.setShipperAssigned(false);
                            viewHolder.shipper_assignment_status.setText("Shipper not assigned yet");
                            viewHolder.btn_call_shipper.setVisibility(View.GONE);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/

        viewHolder.btn_call_shipper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mContext.shipperNumber=model.getTempShipper();
                mContext.tv_phoneno.setText(mContext.shipperNumber);
                mContext.mCallDialog.show();
            }
        });

        if(!model.getStatus().equals("0")) {
            viewHolder.shipper_assignment_status.setText("Shipper Assigned");
            viewHolder.btn_call_shipper.setVisibility(View.VISIBLE);
        }else{
            viewHolder.shipper_assignment_status.setText("Shipper not assigned yet");
            viewHolder.btn_call_shipper.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return restaurantlist.size();
    }


}
