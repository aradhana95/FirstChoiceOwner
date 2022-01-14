package com.example.varunkumar.ownerfirstchoice;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.varunkumar.ownerfirstchoice.Common.Common;
import com.example.varunkumar.ownerfirstchoice.Remote.APIService;
import com.example.varunkumar.ownerfirstchoice.Service.MessagesClientFCMServer;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class SendMessage extends AppCompatActivity {
    MaterialEditText edtMessage, edtTitle;
    Button btnSend;
    APIService mService;
    private ArrayList<String> mobileNumbers = new ArrayList<>();
    DatabaseReference users;
    FirebaseDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);
        getSupportActionBar().setTitle("Send Message");
        mService = Common.getFCMClient();
        edtMessage = (MaterialEditText) findViewById(R.id.edtMessage);
        edtTitle = (MaterialEditText) findViewById(R.id.edtTitle);
        btnSend = findViewById(R.id.btnSend);
        FirebaseApp.initializeApp(this);
        db = FirebaseDatabase.getInstance();
        users = db.getReference("User");
//        btnSend.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Notification notification=new Notification(edtTitle.getText().toString(),edtMessage.getText().toString());
//                Sender toTopic=new Sender();
//                toTopic.to=new StringBuilder("/topics/").append(Common.topicName).toString();
//                toTopic.notification=notification;
//                mService.sendNotification(toTopic)
//                        .enqueue(new Callback<MyResponse>() {
//                            @Override
//                            public void onResponse(@NonNull Call<MyResponse> call, @NonNull Response<MyResponse> response) {
//                             if(response.isSuccessful()){
//                                 Toast.makeText(SendMessage.this,"Message sent",Toast.LENGTH_SHORT).show();
//                             }
//                            }
//
//                            @Override
//                            public void onFailure(@NonNull Call<MyResponse> call, Throwable t) {
//                                Toast.makeText(SendMessage.this,""+t.getMessage(),Toast.LENGTH_SHORT).show();
//                            }
//                        });
//            }
//        });
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = edtTitle.getText().toString();
                String msg = edtMessage.getText().toString();
                if ((title.equals("") || TextUtils.isEmpty(title)) || (msg.equals("") || TextUtils.isEmpty(msg)))
                    Toast.makeText(getApplicationContext(), "Fill All Details", Toast.LENGTH_SHORT).show();
                else {
                    MessagesClientFCMServer.main(SendMessage.this, edtTitle.getText().toString(), edtMessage.getText().toString());
                    new SendSms().execute();
                }


//                new SendSms().execute();

                //Create Message
               /* System.out.println("clicked...........");
                Map<String, String> dataSend = new HashMap<>();
                dataSend.put("title", edtTitle.getText().toString());
                dataSend.put("message", edtMessage.getText().toString());
                DataMessage dataMessage = new DataMessage(new StringBuilder("/topics/")
                        .append(Common.topicName).toString(), dataSend);

                System.out.println("clicked..........."+dataMessage);
                mService.sendNotification(dataMessage).enqueue(new Callback<MyResponse>() {
                    @Override
                    public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                        if (response.isSuccessful())
                            Toast.makeText(SendMessage.this, "Message sent!", Toast.LENGTH_SHORT).show();
                        System.out.println("clicked...........success"+response+"......"+response.isSuccessful());
                    }

                    @Override
                    public void onFailure(Call<MyResponse> call, Throwable t) {
                        System.out.println("clicked...........error"+t.getMessage());
                        Toast.makeText(SendMessage.this, "frfrfrf" + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });*/
            }
        });
    }

    public String sendSms() {
        try {
            // Construct data
            String apiKey = "apikey=" + "uRlkT7rVE04-N0fmmUN5Q23xwENdH63iejlr4NO7k0";
            String message = "&message=" + "Message from FOOD CUBO "+edtTitle.getText().toString()+ edtMessage.getText().toString();
            String sender = "&sender=" + "TXTLCL";
            String numberss = "";
            for(int i=0;i<mobileNumbers.size();i++){
                if(i==mobileNumbers.size()-1)
                    numberss = numberss+mobileNumbers.get(i);
                else
                numberss = numberss+mobileNumbers.get(i)+",";
            }
            String numbers = "&numbers=" + numberss;
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

    private class SendSms extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            users.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren())
                    {
                        if(userSnapshot.child("isUser").exists()){
                            mobileNumbers.add(userSnapshot.child("phone").getValue(String.class));
                        }
                    }
                    if(mobileNumbers.size()>0){
                        sendSms();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            return null;
        }
    }

}
