package com.example.abhatripathi.serverappfoodcubo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.abhatripathi.serverappfoodcubo.Common.Common;
import com.example.abhatripathi.serverappfoodcubo.Remote.APIService;
import com.example.abhatripathi.serverappfoodcubo.Remote.APIService1;
import com.example.abhatripathi.serverappfoodcubo.ViewHolder.OrderViewHolder;
import com.example.abhatripathi.serverappfoodcubo.ViewHolder.RestaurantOrderHolder;
import com.example.abhatripathi.serverappfoodcubo.model.FoodNew;
import com.example.abhatripathi.serverappfoodcubo.model.MyResponse;
import com.example.abhatripathi.serverappfoodcubo.model.Notification;
import com.example.abhatripathi.serverappfoodcubo.model.Order;
import com.example.abhatripathi.serverappfoodcubo.model.Request;
import com.example.abhatripathi.serverappfoodcubo.model.RequestOld;
import com.example.abhatripathi.serverappfoodcubo.model.Sender;
import com.example.abhatripathi.serverappfoodcubo.model.Token;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.jaredrummler.materialspinner.MaterialSpinner;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderStatusRestaurant extends Activity implements View.OnClickListener,ActivityCompat.OnRequestPermissionsResultCallback {
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RestaurantOrdersListAdapter adapter;
    FirebaseDatabase db;
    DatabaseReference requests;
    LinearLayout shipperl, statusl;

    MaterialSpinner spinner, shipperSpinner;
    APIService mService;
    APIService1 mService1;
    SwipeRefreshLayout swipeRefreshLayout;
    ArrayList<RequestOld> restaurantorderslist = new ArrayList<>();


    private TextView tvTitle;
    RelativeLayout rl_title;
    ImageView navigationnn;
    private DrawerLayout mDrawerLayout;
    private RelativeLayout drawerll;
    LinearLayout nav_banners, nav_send_message, nav_allorders, nav_menu;


    private static final int CALL_PHONE_REQUEST_CODE=9999;
    public Dialog mCallDialog;
    public String shipperNumber;
    public TextView tv_phoneno;
    ProgressBar mLoadingProgress;

    @Override
    public void onBackPressed() {
        if(mLoadingProgress.getVisibility()==View.VISIBLE){
            mLoadingProgress.setVisibility(View.GONE);
        }else{
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mLoadingProgress.setVisibility(View.GONE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_restaurant_list);
            //firebase
            db = FirebaseDatabase.getInstance();
            requests = db.getReference("Restaurants").child(Common.restaurantSelected)
            .child("Requests");
        setCallDialog();
        //init Service
        mService = Common.getFCMClient();
        mService1 = Common.getFCMClient1();

        mLoadingProgress = findViewById(R.id.loading_progress);
        tvTitle = findViewById(R.id.tv_title);
        rl_title = findViewById(R.id.rl_title);
        navigationnn = findViewById(R.id.navigationnn);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        drawerll = findViewById(R.id.drawerll);
        nav_banners = findViewById(R.id.nav_banners);
        nav_allorders = findViewById(R.id.nav_allorders);
        nav_send_message = findViewById(R.id.nav_send_message);
        nav_menu = findViewById(R.id.nav_menu);


        navigationnn.setOnClickListener(this);
        nav_banners.setOnClickListener(this);
        nav_send_message.setOnClickListener(this);
        nav_allorders.setOnClickListener(this);
        nav_menu.setOnClickListener(this);
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
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( OrderStatusRestaurant.this,
                new OnSuccessListener<InstanceIdResult>() {
                    @Override
                    public void onSuccess(InstanceIdResult instanceIdResult) {
                        try{
                        updateToken(instanceIdResult.getToken());
                    }catch(Exception e){
                        Common.reopenApp(OrderStatusRestaurant.this);
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

        requests.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                System.out.println("gygygtgt....."+dataSnapshot);

                restaurantorderslist.removeAll(restaurantorderslist);
                restaurantorderslist.clear();

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    RequestOld request = ds.getValue(RequestOld.class);



                    request.setKey(ds.getKey());
                    restaurantorderslist.add(request);
                }

                adapter = new RestaurantOrdersListAdapter(OrderStatusRestaurant.this, restaurantorderslist);
                //set adapter
                recyclerView.setAdapter(adapter);
                swipeRefreshLayout.setRefreshing(false);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        /*
        FirebaseRecyclerOptions<Request> options = new FirebaseRecyclerOptions.Builder<Request>()
                .setQuery(requests, Request.class)
                .build();

        System.out.println("fffffff......"+options.getSnapshots().size()+"...."+Common.restaurantSelected);

        adapter = new FirebaseRecyclerAdapter<Request, RestaurantOrderHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final RestaurantOrderHolder viewHolder, @SuppressLint("RecyclerView") final int position, @NonNull final Request model) {

                viewHolder.txtOrderId.setText(model.getOrderDateTime());
                viewHolder.txtOrderStatus.setText(Common.convertCodeToStatus(model.getStatus()));
                viewHolder.txtOrderAddress.setText(model.getAddress());
                viewHolder.txtOrderphone.setText(model.getPhone());
                viewHolder.txtOrderDate.setText(Common.getDate(Long.parseLong(model.getOrderDateTime())));
                //new event button

                viewHolder.btnDetails.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent orderDetail = new Intent(OrderStatusRestaurant.this, OrderDetail.class);
                        Common.currentNewRequest = model;
                        orderDetail.putExtra("OrderId", model.getOrderDateTime());
                        startActivity(orderDetail);
                    }
                });

                DatabaseReference tokens = db.getReference("Requests");
                tokens.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot requestSnapshot : dataSnapshot.getChildren())
                        {
                            if(requestSnapshot.getKey().equals(adapter.getRef(position).getKey())){
                                if(!requestSnapshot.child("status").getValue(String.class).equals("0")) {
                                    model.setShipperAssigned(true);
                                    model.setShipperNumber(requestSnapshot.child("tempShipper").getValue(String.class));
                                }
                                else {
                                    model.setShipperAssigned(false);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                viewHolder.btn_call_shipper.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        shipperNumber=model.getShipperNumber();
                        mCallDialog.show();
                    }
                });

                if(model.isShipperAssigned()) {
                    viewHolder.shipper_assignment_status.setText("Shipper Assigned");
                    viewHolder.btn_call_shipper.setVisibility(View.VISIBLE);
                }else{
                    viewHolder.shipper_assignment_status.setText("Shipper not assigned yet");
                    viewHolder.btn_call_shipper.setVisibility(View.GONE);
                }
            }

            @NonNull
            @Override
            public RestaurantOrderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.order_layout, parent, false);
                return new RestaurantOrderHolder(itemView);
            }


        };
        adapter.startListening();
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
        swipeRefreshLayout.setRefreshing(false);
*/
    }

    private void setCallDialog(){

        mCallDialog = new Dialog(OrderStatusRestaurant.this);
        mCallDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mCallDialog.setContentView(R.layout.dialog_call_restaurant);
        mCallDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        tv_phoneno = mCallDialog.findViewById(R.id.tv_phoneno);
        TextView cancel = mCallDialog.findViewById(R.id.tv_cancel);
        TextView ok = mCallDialog.findViewById(R.id.tv_call);
        cancel.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                mCallDialog.dismiss();
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {

            @TargetApi(16)
            public void onClick(View view) {
                    if(ActivityCompat.checkSelfPermission(OrderStatusRestaurant.this, Manifest.permission.CALL_PHONE)
                            != PackageManager.PERMISSION_GRANTED
                            ){
                        ActivityCompat.requestPermissions(OrderStatusRestaurant.this,new String[]{
                                Manifest.permission.CALL_PHONE
                        },CALL_PHONE_REQUEST_CODE);

                    }
                    else {
                        mLoadingProgress.setVisibility(View.VISIBLE);
                        Intent intent = new Intent(Intent.ACTION_CALL);
                        intent.setData(Uri.parse("tel:" + shipperNumber));
                        startActivityForResult(intent,0);
                    }
                mCallDialog.dismiss();
            }
        });

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case  CALL_PHONE_REQUEST_CODE:
            {
                if(grantResults.length >0 && grantResults[0] ==PackageManager.PERMISSION_GRANTED){
                    mLoadingProgress.setVisibility(View.VISIBLE);
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + shipperNumber));
                    startActivityForResult(intent,0);
                }
            }
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
    }

    private void deleteOrder(String key) {
        requests.child(key).removeValue();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {

        if (v == navigationnn)
            mDrawerLayout.openDrawer(drawerll);
        else if (v == nav_menu) {
            Intent orders = new Intent(OrderStatusRestaurant.this, Home.class);
            startActivity(orders);

        } else if (v == nav_allorders) {

        } else if (v == nav_banners) {
            Intent orders = new Intent(OrderStatusRestaurant.this, BannerActivity.class);
            startActivity(orders);

        } else if (v == nav_send_message) {
            Intent message = new Intent(OrderStatusRestaurant.this, SendMessage.class);
            startActivity(message);
        }
        if (v != navigationnn)
            mDrawerLayout.closeDrawer(drawerll);
    }
}
