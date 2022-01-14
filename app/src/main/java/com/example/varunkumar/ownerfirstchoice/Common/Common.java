package com.example.varunkumar.ownerfirstchoice.Common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.varunkumar.ownerfirstchoice.MainActivity;
import com.example.varunkumar.ownerfirstchoice.Remote.APIService;
import com.example.varunkumar.ownerfirstchoice.Remote.APIService1;
import com.example.varunkumar.ownerfirstchoice.Remote.FCMRetrofitClient;
import com.example.varunkumar.ownerfirstchoice.Remote.IGeoCoordinates;
import com.example.varunkumar.ownerfirstchoice.Remote.RetrofitClient;
import com.example.varunkumar.ownerfirstchoice.model.Request;
import com.example.varunkumar.ownerfirstchoice.model.RequestOld;
import com.example.varunkumar.ownerfirstchoice.model.User;

import java.util.Calendar;
import java.util.Locale;

public class Common {
    public static String restaurantSelected="";
    public static boolean isAdmin=false;
    public static User currentUser;
    public static final String SHIPPERS_TABLE="Shippers";
    public static final String ORDER_NEED_SHIP_TABLE="OrdersNeedShip";
    public static RequestOld currentRequest;
    public static Request currentNewRequest;
    public static final String UPDATE="Update";
    public static final String DELETE="Delete";
    public static String topicName="News";
    public static final int PICK_IMAGE_REQUEST=71;
    private static final String baseUrl = "https://maps.googleapis.com";
    private static final String fcmUrl = "https://fcm.googleapis.com/";
    public static String PHONE_TEXT="userPhone";
    public static final String LEGACY_SERVER_KEY="AAAAcomtHlQ:APA91bHEA5V7XlHkNuMg3CXemd60rlFoNOIKb--7kBsuvZKhlc5pXN9nHgYbNLE7h4bXcn8S4n47J54blm_ySGxqa4toBKPFLnqRykE4Bwi_5fxeo0aUKYUeWL-CFmvcSa_c_ne5kXa9BAygmsBbY9iAUGBSTHopGg";

    public static String convertCodeToStatus(String code){
        if(code.equals("0")){
            return "Placed";
        }
        else if(code.equals("1")){
            return "On My Way";
        }
        else if(code.equals("2")){
            return "Shipping";
        }
        else{
            return "Shipped!";
        }
    }
    public static void reopenApp(Activity context){
        context.finish();
        context.startActivity(new Intent(context,MainActivity.class));
    }

    public static IGeoCoordinates getGeoCodeService(){
        return RetrofitClient.getClient(baseUrl).create(IGeoCoordinates.class);
    }
    public static APIService getFCMClient(){
        return FCMRetrofitClient.getClient(fcmUrl).create(APIService.class);
    }
    public static APIService1 getFCMClient1(){
        return FCMRetrofitClient.getClient(fcmUrl).create(APIService1.class);
    }
    public static Bitmap scaleBitmap(Bitmap bitmap,int newWidth,int newHeight){
        Bitmap scaledBitmap = Bitmap.createBitmap(newWidth,newHeight, Bitmap.Config.ARGB_8888);
        float scaleX = newWidth/(float)bitmap.getWidth();
        float scaleY = newHeight/(float)bitmap.getHeight();
        float pivotX=0,pivotY=0;
        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(scaleX,scaleY,pivotX,pivotY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bitmap,0,0,new Paint(Paint.FILTER_BITMAP_FLAG));
        return scaledBitmap;
    }
    public static String getDate(long time){
        Calendar calendar=  Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        StringBuilder date=new StringBuilder(
                android.text.format.DateFormat.format("dd-MM-yyyy HH:mm",calendar).toString()
        );
        return date.toString();
    }


    public static boolean isConnectedToInternet(Context context){
        ConnectivityManager connectivityManager=
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager !=null){
            NetworkInfo[] info =connectivityManager.getAllNetworkInfo();
            if(info!=null){
                for (int i=0;i<info.length;i++){
                    if(info[i].getState() == NetworkInfo.State.CONNECTED)
                        return true;
                }
            }
        }
        return false;

    }

}
