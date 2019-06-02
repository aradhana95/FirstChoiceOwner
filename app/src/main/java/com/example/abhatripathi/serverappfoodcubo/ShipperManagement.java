package com.example.abhatripathi.serverappfoodcubo;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.abhatripathi.serverappfoodcubo.Common.Common;
import com.example.abhatripathi.serverappfoodcubo.ViewHolder.ShipperViewHolder;
import com.example.abhatripathi.serverappfoodcubo.model.Shipper;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;
import java.util.Map;

public class ShipperManagement extends AppCompatActivity {
    FloatingActionButton fabAdd;
    FirebaseDatabase database;
    DatabaseReference shippers;
    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;
    MaterialEditText edtName,edtPhone,edtPassword;
    FirebaseRecyclerAdapter<Shipper,ShipperViewHolder> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipper_management);
        //init view
        fabAdd=(FloatingActionButton)findViewById(R.id.fab_add);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCreateShipperLayout();
            }
        });

        recyclerView=(RecyclerView)findViewById(R.id.recycler_shipper);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        //firebase
        database=FirebaseDatabase.getInstance();
        shippers=database.getReference(Common.SHIPPERS_TABLE);
        //Load all shippers
        loadAllShippers();

    }

    private void loadAllShippers() {
        FirebaseRecyclerOptions<Shipper> allShipper=new FirebaseRecyclerOptions.Builder<Shipper>()
                .setQuery(shippers,Shipper.class)
                .build();
        adapter=new FirebaseRecyclerAdapter<Shipper, ShipperViewHolder>(allShipper) {
            @Override
            protected void onBindViewHolder(@NonNull ShipperViewHolder holder, @SuppressLint("RecyclerView") final int position, @NonNull final Shipper model) {
              holder.shipper_name.setText(model.getName());
              holder.shipper_phone.setText(model.getPhone());
              holder.btn_edit.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View view) {
                      showEditDialog(adapter.getRef(position).getKey(),model);
                  }
              });
              holder.btn_remove.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View view) {
                      removeShipper(adapter.getRef(position).getKey());
                  }
              });
            }

            @NonNull
            @Override
            public ShipperViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView=LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.shipper_layout,parent,false);
                return new ShipperViewHolder(itemView);
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

    private void showEditDialog(String key,Shipper model) {
        AlertDialog.Builder create_shipper_dialog=new AlertDialog.Builder(ShipperManagement.this);
        create_shipper_dialog.setTitle("Update Shipper");
        LayoutInflater inflater=this.getLayoutInflater();
        View view=inflater.inflate(R.layout.create_shipper_layout,null);
        edtName=view.findViewById(R.id.edtName);
        edtPhone=view.findViewById(R.id.edtPhone);
        edtPassword=view.findViewById(R.id.edtPassword);
        //set data
        edtName.setText(model.getName());
        edtPassword.setText(model.getPassword());
        edtPhone.setText(model.getPhone());



        create_shipper_dialog.setView(view);
        create_shipper_dialog.setIcon(R.drawable.ic_local_shipping_black_24dp);
        create_shipper_dialog.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
               Map<String,Object> update=new HashMap<>();
               update.put("name",edtName.getText().toString());
                update.put("phone",edtPhone.getText().toString());
                update.put("password",edtPassword.getText().toString());

                shippers.child(edtPhone.getText().toString())
                        .updateChildren(update)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(ShipperManagement.this,"Shipper Updated!",Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ShipperManagement.this,e.getMessage(),Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });
        create_shipper_dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        create_shipper_dialog.show();
    }
    private void removeShipper(String key) {
        shippers.child(key)
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ShipperManagement.this,"Removed Successfully!",Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ShipperManagement.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
        adapter.notifyDataSetChanged();
    }

    private void showCreateShipperLayout() {
        AlertDialog.Builder create_shipper_dialog=new AlertDialog.Builder(ShipperManagement.this);
        create_shipper_dialog.setTitle("Create Shipper");
        LayoutInflater inflater=this.getLayoutInflater();
        View view=inflater.inflate(R.layout.create_shipper_layout,null);
        edtName=view.findViewById(R.id.edtName);
        edtPhone=view.findViewById(R.id.edtPhone);
        edtPassword=view.findViewById(R.id.edtPassword);


        create_shipper_dialog.setView(view);
        create_shipper_dialog.setIcon(R.drawable.ic_local_shipping_black_24dp);
        create_shipper_dialog.setPositiveButton("CREATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                Shipper shipper=new Shipper();
                shipper.setName(edtName.getText().toString());
                shipper.setPhone(edtPhone.getText().toString());
                shipper.setPassword(edtPassword.getText().toString());
                shippers.child(edtPhone.getText().toString())
                        .setValue(shipper)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(ShipperManagement.this,"Shipper Created!",Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ShipperManagement.this,e.getMessage(),Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });
        create_shipper_dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                   dialogInterface.dismiss();
            }
        });
        create_shipper_dialog.show();

        }
}
