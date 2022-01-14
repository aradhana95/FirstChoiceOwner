package com.example.varunkumar.ownerfirstchoice;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.varunkumar.ownerfirstchoice.Common.Common;
import com.example.varunkumar.ownerfirstchoice.Interface.ItemClickListener;
import com.example.varunkumar.ownerfirstchoice.ViewHolder.FoodViewHolder;
import com.example.varunkumar.ownerfirstchoice.model.Food;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.UUID;

public class FoodList extends AppCompatActivity {
    CheckBox check_piecetype,check_isveg;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RelativeLayout rootLayout;
    FloatingActionButton fab;
    //firebase
    FirebaseDatabase db;
    DatabaseReference foodList;
    FirebaseStorage storage;
    StorageReference storageReference;
    String categoryId = "";
    FirebaseRecyclerAdapter<Food, FoodViewHolder> adapter;
    //Add new food
    MaterialEditText edtName, edtDescription, edtFullPrice, edtHalfPrice, edtDiscount;
    Button select, upload, btnno, btnyes;
    Food newFood;
    Uri saveUri;

    @Override
    protected void onResume() {
        super.onResume();
        loadListFood(categoryId);
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);
        //firebase
        db = FirebaseDatabase.getInstance();
        foodList = db.getReference("Restaurants").child(Common.restaurantSelected)
                .child("details").child("Food");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        //init
        recyclerView = (RecyclerView) findViewById(R.id.recycler_food);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        rootLayout = (RelativeLayout) findViewById(R.id.rootLayout);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddFoodDialog();
            }
        });
        if (getIntent() != null)
            categoryId = getIntent().getStringExtra("CategoryId");
        if (!categoryId.isEmpty()) {
            loadListFood(categoryId);
        }
    }
    AlertDialog dialog;
    private void showAddFoodDialog() {

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(FoodList.this);
        alertDialog.setTitle("Add new Food");
        alertDialog.setMessage("Please fill full information");
        LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.add_new_food_layout, null);
        edtName = add_menu_layout.findViewById(R.id.edtName);
        edtDescription = add_menu_layout.findViewById(R.id.edtDescription);
        edtFullPrice = add_menu_layout.findViewById(R.id.edtFullPrice);
        edtHalfPrice = add_menu_layout.findViewById(R.id.edtHalfPrice);
        edtDiscount = add_menu_layout.findViewById(R.id.edtDiscount);
        check_piecetype = add_menu_layout.findViewById(R.id.check_piecetype);
        check_isveg = add_menu_layout.findViewById(R.id.check_isveg);

        check_piecetype.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked) {
                    edtHalfPrice.setVisibility(View.VISIBLE);
                } else {
                    edtHalfPrice.setVisibility(View.GONE);
                }
            }
        });

        select = add_menu_layout.findViewById(R.id.btnSelect);
        upload = add_menu_layout.findViewById(R.id.btnUpload);
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();//Let user choose image from gallery and same its uri
            }
        });
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage();
            }
        });
        alertDialog.setView(add_menu_layout);
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);
        btnno = add_menu_layout.findViewById(R.id.btnno);
        btnyes = add_menu_layout.findViewById(R.id.btnyes);
        btnyes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //here create new category
                if (newFood != null) {
                    dialog.dismiss();
                    foodList.push().setValue(newFood);
                    Snackbar.make(rootLayout, "New Food " + newFood.getName() + " is added", Snackbar.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(),"Image Not Uploaded",Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog = alertDialog.create();
        dialog.show();
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), Common.PICK_IMAGE_REQUEST);
    }

    private void uploadImage() {
        if (saveUri != null) {
            final ProgressDialog mDialog = new ProgressDialog(FoodList.this);
            mDialog.setMessage("Uploading...");
            mDialog.show();
            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("images/" + imageName);
            imageFolder.putFile(saveUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            mDialog.dismiss();
                            Toast.makeText(FoodList.this, "Uploaded!!!", Toast.LENGTH_SHORT).show();
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    // set Value for image uploaded and we can get download link
                                    newFood = new Food();
                                    newFood.setName(edtName.getText().toString());
                                    newFood.setDescription(edtDescription.getText().toString());
                                    newFood.setFullprice(edtFullPrice.getText().toString());
                                    if (check_piecetype.isChecked()) {
                                        newFood.setPieceType("true");
                                        newFood.setHalfprice(edtHalfPrice.getText().toString());
                                    } else {
                                        newFood.setPieceType("false");
                                        newFood.setHalfprice("");
                                    }
                                    if (check_isveg.isChecked()) {
                                        newFood.setVegType("true");
                                    } else {
                                        newFood.setVegType("false");
                                    }
                                    newFood.setDiscount(edtDiscount.getText().toString());
                                    newFood.setMenuId(categoryId);
                                    newFood.setImage(uri.toString());

                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {

                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mDialog.dismiss();
                            Toast.makeText(FoodList.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mDialog.setMessage("Uploaded" + progress + "%");
                        }
                    });

        }
    }


    private void loadListFood(String categoryId) {

        Query listFoodByCategoryId = foodList.orderByChild("menuId").equalTo(categoryId);
        FirebaseRecyclerOptions<Food> options = new FirebaseRecyclerOptions.Builder<Food>()
                .setQuery(listFoodByCategoryId, Food.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FoodViewHolder viewHolder, int position, @NonNull Food model) {
                viewHolder.food_name.setText(model.getName());
                if(model.getImage()!=null)
                Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.food_image);
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //code later
                    }
                });
            }

            @NonNull
            @Override
            public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.food_item, parent, false);
                return new FoodViewHolder(itemView);
            }
        };
        adapter.startListening();
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Common.PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            saveUri = data.getData();
            select.setText("Image Selected!!");

        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle().equals(Common.UPDATE)) {
            showUpdateFoodDialog(adapter.getRef(item.getOrder()).getKey(), adapter.getItem(item.getOrder()));
        } else if (item.getTitle().equals(Common.DELETE)) {
            deleteFood(adapter.getRef(item.getOrder()).getKey());
        }
        return super.onContextItemSelected(item);
    }

    private void deleteFood(String key) {
        foodList.child(key).removeValue();
    }

    private void showUpdateFoodDialog(final String key, final Food item) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(FoodList.this);
        alertDialog.setTitle("Edit Food");
        alertDialog.setMessage("Please fill full information");
        LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.add_new_food_layout, null);
        edtName = add_menu_layout.findViewById(R.id.edtName);
        edtDescription = add_menu_layout.findViewById(R.id.edtDescription);
        edtHalfPrice = add_menu_layout.findViewById(R.id.edtHalfPrice);
        edtFullPrice = add_menu_layout.findViewById(R.id.edtFullPrice);
        edtDiscount = add_menu_layout.findViewById(R.id.edtDiscount);
        check_piecetype = add_menu_layout.findViewById(R.id.check_piecetype);
        check_isveg = add_menu_layout.findViewById(R.id.check_isveg);

        check_piecetype.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked) {
                    edtHalfPrice.setVisibility(View.VISIBLE);
                } else {
                    edtHalfPrice.setVisibility(View.GONE);
                }
            }
        });


        //set default value for view
        edtName.setText(item.getName());
        edtDescription.setText(item.getDiscount());
        edtFullPrice.setText(item.getFullprice());

        if (item.getVegType().equals("true")) {
            check_isveg.setChecked(true);
        } else {
            check_isveg.setChecked(false);
        }

        if (item.getPieceType().equals("true")) {
            check_piecetype.setChecked(true);
            edtHalfPrice.setVisibility(View.VISIBLE);
            edtHalfPrice.setText(item.getHalfprice());
        }
        else {
            check_piecetype.setChecked(false);
            edtHalfPrice.setVisibility(View.GONE);
            edtHalfPrice.setText("");
        }
        edtDescription.setText(item.getDescription());

        select = add_menu_layout.findViewById(R.id.btnSelect);
        upload = add_menu_layout.findViewById(R.id.btnUpload);
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();//Let user choose image from gallery and same its uri
            }
        });
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChangeImage(item);
            }
        });
        alertDialog.setView(add_menu_layout);
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        btnno = add_menu_layout.findViewById(R.id.btnno);
        btnyes = add_menu_layout.findViewById(R.id.btnyes);
        btnyes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                //here create new category

                //update information
                item.setName(edtName.getText().toString());
                item.setFullprice(edtFullPrice.getText().toString());
                if (check_piecetype.isChecked()) {
                    item.setPieceType("true");
                    item.setHalfprice(edtHalfPrice.getText().toString());
                } else {
                    item.setPieceType("false");
                    item.setHalfprice("");
                }
                if (check_isveg.isChecked()) {
                    item.setVegType("true");
                } else {
                    item.setVegType("false");
                }
                item.setDiscount(edtDiscount.getText().toString());
                item.setDescription(edtDescription.getText().toString());
                foodList.child(key).setValue(item);
                Snackbar.make(rootLayout, "Food Item " + item.getName() + " is updated", Snackbar.LENGTH_SHORT).show();
            }
        });
        btnno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog = alertDialog.create();
        dialog.show();


    }

    private void ChangeImage(final Food item) {
        if (saveUri != null) {
            final ProgressDialog mDialog = new ProgressDialog(FoodList.this);
            mDialog.setMessage("Uploading...");
            mDialog.show();
            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("images/" + imageName);
            imageFolder.putFile(saveUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            mDialog.dismiss();
                            Toast.makeText(FoodList.this, "Uploaded!!!", Toast.LENGTH_SHORT).show();
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    // set Value for image uploaded and we can get download link
                                    item.setImage(uri.toString());

                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {

                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mDialog.dismiss();
                            Toast.makeText(FoodList.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mDialog.setMessage("Uploaded" + progress + "%");
                        }
                    });

        }
    }
}
