package com.example.abhatripathi.serverappfoodcubo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.abhatripathi.serverappfoodcubo.Common.Common;
import com.example.abhatripathi.serverappfoodcubo.Remote.APIService;
import com.example.abhatripathi.serverappfoodcubo.Remote.APIService1;
import com.example.abhatripathi.serverappfoodcubo.ViewHolder.OrderViewHolder;
import com.example.abhatripathi.serverappfoodcubo.model.DataMessage;
import com.example.abhatripathi.serverappfoodcubo.model.MyResponse;
import com.example.abhatripathi.serverappfoodcubo.model.Notification;
import com.example.abhatripathi.serverappfoodcubo.model.RequestOld;
import com.example.abhatripathi.serverappfoodcubo.model.Sender;
import com.example.abhatripathi.serverappfoodcubo.model.Token;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.JsonObject;
import com.jaredrummler.materialspinner.MaterialSpinner;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderStatus extends Activity implements View.OnClickListener {
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerAdapter<RequestOld, OrderViewHolder> adapter;
    FirebaseDatabase db;
    DatabaseReference requests;
    LinearLayout shipperl, statusl;

    MaterialSpinner spinner, shipperSpinner;
    APIService mService;
    APIService1 mService1;
    SwipeRefreshLayout swipeRefreshLayout;


    private TextView tvTitle;
    RelativeLayout rl_title;
    ImageView navigationnn;
    private DrawerLayout mDrawerLayout;
    private RelativeLayout drawerll;
    LinearLayout  nav_send_message, nav_allorders, nav_shipper, nav_restaurants;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_admin_list);
            //firebase
        FirebaseApp.initializeApp(OrderStatus.this);
            db = FirebaseDatabase.getInstance();
            requests = db.getReference("Requests");

        //init Service
        mService = Common.getFCMClient();
        mService1 = Common.getFCMClient1();

        tvTitle = findViewById(R.id.tv_title);
        rl_title = findViewById(R.id.rl_title);
        navigationnn = findViewById(R.id.navigationnn);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        drawerll = findViewById(R.id.drawerll);
        nav_shipper = findViewById(R.id.nav_shipper);
        nav_allorders = findViewById(R.id.nav_allorders);
        nav_send_message = findViewById(R.id.nav_send_message);
        nav_restaurants = findViewById(R.id.nav_restaurants);


        navigationnn.setOnClickListener(this);
        nav_send_message.setOnClickListener(this);
        nav_shipper.setOnClickListener(this);
        nav_allorders.setOnClickListener(this);
        nav_restaurants.setOnClickListener(this);
        //init
        recyclerView = findViewById(R.id.recycler_restaurant);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        swipeRefreshLayout = findViewById(R.id.swipe_layout);
        loadOrders();

        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Common.isConnectedToInternet(getBaseContext()))
                    loadOrders();
                else {
                    Toast.makeText(getBaseContext(), "Please check your connection!!!!", Toast.LENGTH_SHORT).show();
                }

            }
        });
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( OrderStatus.this,
                new OnSuccessListener<InstanceIdResult>() {
                    @Override
                    public void onSuccess(InstanceIdResult instanceIdResult) {
                        try{
                        updateToken(instanceIdResult.getToken());
                    }catch(Exception e){
                        Common.reopenApp(OrderStatus.this);
                    }

                    }
                });

    }

    private void updateToken(String token) {
        System.out.println("lol token........"+token);
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference tokens = db.getReference("Tokens");
        Token data = new Token(token, true);
        tokens.child(Common.currentUser.getPhone()).setValue(data);
    }
    private void loadOrders() {
        FirebaseRecyclerOptions<RequestOld> options = new FirebaseRecyclerOptions.Builder<RequestOld>()
                .setQuery(requests, RequestOld.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<RequestOld, OrderViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final OrderViewHolder viewHolder, @SuppressLint("RecyclerView") final int position, @NonNull final RequestOld model) {
                viewHolder.txtOrderId.setText(adapter.getRef(position).getKey());
                viewHolder.txtOrderStatus.setText(Common.convertCodeToStatus(model.getStatus()));
                viewHolder.txtOrderAddress.setText(model.getAddress());
                viewHolder.txtOrderphone.setText(model.getPhone());
                viewHolder.txtOrderDate.setText(Common.getDate(Long.parseLong(adapter.getRef(position).getKey())));
                if(model.getStatus().equals("0")){
                    viewHolder.btnEdit.setText("ASSIGN SHIPPER");
                }else{
                    viewHolder.btnEdit.setText("EDIT");
                }

                //new event button
                viewHolder.btnEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (viewHolder.btnEdit.getText().equals("EDIT"))
                            showUpdateDialog(adapter.getRef(position).getKey(), adapter.getItem(position));
                        else if (viewHolder.btnEdit.getText().equals("ASSIGN SHIPPER"))
                            showAssignShipperDialog(adapter.getRef(position).getKey(), adapter.getItem(position));


                    }
                });
                viewHolder.btnRemove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deleteOrder(adapter.getRef(position).getKey());
                    }
                });
                viewHolder.btnDetails.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent orderDetail = new Intent(OrderStatus.this, OrderDetail.class);
                        Common.currentRequest = model;
                        orderDetail.putExtra("OrderId", adapter.getRef(position).getKey());
                        startActivity(orderDetail);
                    }
                });
                viewHolder.btnDirections.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent trackingOrder = new Intent(OrderStatus.this, TrackingOrder.class);
                        Common.currentRequest = model;
                        startActivity(trackingOrder);
                    }
                });
            }

            @NonNull
            @Override
            public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.order_layout, parent, false);
                return new OrderViewHolder(itemView);
            }


        };
        adapter.startListening();
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
        swipeRefreshLayout.setRefreshing(false);

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void deleteOrder(String key) {
        requests.child(key).removeValue();
        adapter.notifyDataSetChanged();
    }

    private void showUpdateDialog(String key, final RequestOld item) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(OrderStatus.this);
        alertDialog.setTitle("Update Order");
        alertDialog.setMessage("Please Choose Status");
        LayoutInflater inflater = this.getLayoutInflater();
        final View view = inflater.inflate(R.layout.update_order_layout, null);


        shipperl = view.findViewById(R.id.shipperl);
        statusl = view.findViewById(R.id.statusl);
        statusl.setVisibility(View.VISIBLE);
        spinner = (MaterialSpinner) view.findViewById(R.id.statusSpinner);
        spinner.setItems("Placed", "On My Way", "Shipping");

        shipperSpinner = (MaterialSpinner) view.findViewById(R.id.shipperSpinner);
        //load all shipper phone to spinner
        final List<String> shipperList = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference(Common.SHIPPERS_TABLE)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot shipperSnapshot : dataSnapshot.getChildren())
                            shipperList.add(shipperSnapshot.getKey());
                        shipperSpinner.setItems(shipperList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


        alertDialog.setView(view);
        final String localKey = key;
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                item.setStatus(String.valueOf(spinner.getSelectedIndex()));
                if (item.getStatus().equals("0")) {

                    DatabaseReference shipperorders=FirebaseDatabase.getInstance().getReference(Common.ORDER_NEED_SHIP_TABLE);
                    shipperorders.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot shipperSnapshot : dataSnapshot.getChildren()) {
                                if(shipperSnapshot.child(localKey).exists()) {
                                    FirebaseDatabase.getInstance().getReference(Common.ORDER_NEED_SHIP_TABLE)
                                            .child(shipperSnapshot.getKey()).child(localKey).removeValue();
                                }

                            }


                            FirebaseDatabase.getInstance().getReference(Common.ORDER_NEED_SHIP_TABLE)
                                    .child(shipperSpinner.getItems().get(shipperSpinner.getSelectedIndex()).toString())
                                    .child(localKey)
                                    .setValue(item);
                            requests.child(localKey).setValue(item);
                            adapter.notifyDataSetChanged();//add to update item
                            sendOrderStatusToUser(localKey, item);
                            sendOrderShipRequestToShipper(shipperSpinner.getItems().get(shipperSpinner.getSelectedIndex()).toString(),
                                    item,localKey);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                } else {

                    requests.child(localKey).setValue(item);
                    adapter.notifyDataSetChanged();//add to update item
                    sendOrderStatusToUser(localKey, item);

                }
            }
        });
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.show();
    }


    private void showAssignShipperDialog(String key, final RequestOld item) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(OrderStatus.this);
        alertDialog.setTitle("Addign Shipper");
        alertDialog.setMessage("Please Choose Shipper");
        LayoutInflater inflater = this.getLayoutInflater();
        final View view = inflater.inflate(R.layout.update_order_layout, null);


        shipperl = view.findViewById(R.id.shipperl);
        statusl = view.findViewById(R.id.statusl);

        statusl.setVisibility(View.GONE);

        spinner = (MaterialSpinner) view.findViewById(R.id.statusSpinner);
        spinner.setItems("Placed", "On My Way", "Shipping");

        shipperSpinner = (MaterialSpinner) view.findViewById(R.id.shipperSpinner);
        //load all shipper phone to spinner
        final List<String> shipperList = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference(Common.SHIPPERS_TABLE)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot shipperSnapshot : dataSnapshot.getChildren())
                            shipperList.add(shipperSnapshot.getKey());
                        shipperSpinner.setItems(shipperList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


        alertDialog.setView(view);
        final String localKey = key;
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                    sendOrderShipRequestToShipper(shipperSpinner.getItems().get(shipperSpinner.getSelectedIndex()).toString(),
                            item,localKey);


            }
        });
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.show();
    }


    private void sendOrderShipRequestToShipper(final String shipperPhone, final RequestOld item, final String requestKey) {
        DatabaseReference tokens = db.getReference("Tokens");
        tokens.child(shipperPhone)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()) {
                            final Token token = dataSnapshot.getValue(Token.class);
                            final MediaType JSON
                                    = MediaType.parse("application/json; charset=utf-8");
                            new AsyncTask<Void,Void,Void>(){
                                @Override
                                protected Void doInBackground(Void... params) {

                                    try {
                                        sendSms("HELLO SHIPPER You have new order needed to be shipped",dataSnapshot.getKey());
                                        OkHttpClient client = new OkHttpClient();
                                        JSONObject json=new JSONObject();
                                        JSONObject dataJson=new JSONObject();
                                        dataJson.put("body","HELLO SHIPPER");
                                        dataJson.put("title","You have new order needed to be shipped");
                                        json.put("notification",dataJson);
                                        json.put("to",token.getToken());
                                        RequestBody body = RequestBody.create(JSON, json.toString());
                                        okhttp3.Request request = new okhttp3.Request.Builder()
                                                .header("Authorization","key="+Common.LEGACY_SERVER_KEY)
                                                .url("https://fcm.googleapis.com/fcm/send")
                                                .post(body)
                                                .build();
                                        okhttp3.Response response = client.newCall(request).execute();
                                        if (response.code() == 200) {
                                            if (response.isSuccessful()) {
                                                item.setTempShipper(shipperPhone);
                                                requests.child(requestKey).setValue(item);
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(OrderStatus.this, "Order was Updated !", Toast.LENGTH_SHORT).show();

                                                    }
                                                });
                                            } else {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(OrderStatus.this, "Order was Updated but failed to send notification !", Toast.LENGTH_SHORT).show();

                                                    }
                                                });
                                           }
                                        }
                                        String finalResponse = response.body().string();
                                        System.out.println("jiji....." + finalResponse);
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                    return null;
                                }
                            }.execute();

                         /*   Notification notification = new Notification
                                    ("HELLO SHIPPER", "You have new order needed to be shipped");
                            Sender content = new Sender(token.getToken(), notification);
                            mService1.sendNotification(content)
                                    .enqueue(new Callback<MyResponse>() {
                                        @Override
                                        public void onResponse(@NonNull Call<MyResponse> call, @NonNull Response<MyResponse> response) {
                                            System.out.println("response............"+response);
                                            if (response.code() == 200) {
                                                if (response.body().success == 1) {
                                                    Toast.makeText(OrderStatus.this, "Order was Updated !", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(OrderStatus.this, "Order was Updated but failed to send notification !", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<MyResponse> call, Throwable t) {

                                            Log.e("ERROR", t.getMessage());
                                        }
                                    });*/
                           /*  if (dataSnapshot.exists()) {
                            Token token = dataSnapshot.getValue(Token.class);
                            Map<String, String> dataSend = new HashMap<>();
                            dataSend.put("title", "HELLO CLIENT");
                            dataSend.put("message", "You have new order needed to be shipped");
                            assert token != null;
                            DataMessage dataMessage = new DataMessage(token.getToken(), dataSend);
                            mService.sendNotification(dataMessage)
                                    .enqueue(new Callback<MyResponse>() {
                                        @Override
                                        public void onResponse(@NonNull Call<MyResponse> call, @NonNull Response<MyResponse> response) {
                                            if (response.body() != null) {
                                                if (response.body().success == 1) {
                                                    Toast.makeText(OrderStatus.this, "Sent to Shipper!", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(OrderStatus.this, "Failed to send notification !", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<MyResponse> call, Throwable t) {
                                            Log.e("Error", t.getMessage());

                                        }
                                    });
                        }*/
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
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
                                    ("HELLO ", "Your order " + key + " was updated");
                            Sender content = new Sender(token.getToken(), notification);
                            sendSms("HELLO  Your order " + key + " was updated",dataSnapshot.getKey());
                            mService1.sendNotification(content)
                                    .enqueue(new Callback<MyResponse>() {
                                        @Override
                                        public void onResponse(@NonNull Call<MyResponse> call, @NonNull Response<MyResponse> response) {
                                            System.out.println("jiji....." + response);
                                            if (response.code() == 200) {
                                                if (response.body().success == 1) {
                                                    Toast.makeText(OrderStatus.this, "Order was Updated !", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(OrderStatus.this, "Order was Updated but failed to send notification !", Toast.LENGTH_SHORT).show();
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
    public void onClick(View v) {

        if (v == navigationnn)
            mDrawerLayout.openDrawer(drawerll);
        else if (v == nav_restaurants) {
            Intent orders = new Intent(OrderStatus.this, OrderStatus.class);
            startActivity(orders);

        } else if (v == nav_allorders) {

        } else if (v == nav_send_message) {
            Intent message = new Intent(OrderStatus.this, SendMessage.class);
            startActivity(message);
        } else if (v == nav_shipper) {
            Intent shippers = new Intent(OrderStatus.this, ShipperManagement.class);
            startActivity(shippers);
        }
        if (v != navigationnn)
            mDrawerLayout.closeDrawer(drawerll);
    }
}
