package com.example.varunkumar.ownerfirstchoice.Service;

import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.logging.Logger;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.gson.JsonObject;

public class MessagesClientFCMServer {
	
	private static final Logger log = Logger.getLogger(MessagesClientFCMServer.class
			.getName());

	private static String SCOPE = "https://www.googleapis.com/auth/firebase.messaging";
	private static String FCM_DEALS_ENDPOINT = "https://fcm.googleapis.com/v1/projects/fir-foodcubo/messages:send";
	private static Context context1;

	public static void main(String args[]) {
		MessagesClientFCMServer fcmClient = new MessagesClientFCMServer();
		fcmClient.sendNotification(args[0],args[1]);
//		fcmClient.sendData();
	}

	public static void main(Context context,String title, String message) {
		context1=context;
		MessagesClientFCMServer fcmClient = new MessagesClientFCMServer();
		fcmClient.sendNotification(title,message);
//		fcmClient.sendData();
	}

	private void sendNotification(String title, String message){
		new RefreshTokenTask().execute(getFcmMessageJSONNotification(title, message));

//		sendMessageToFcm(getFcmMessageJSONNotification(notificationTitle, notificationBody));
	}

	/*
	private void sendData(){
		sendMessageToFcm(getFcmMessageJSONData());
	}
	private void sendDataNotification(){
		String notificationTitle = "Latest Deals";
		String notificationBody = "View latest deals from top brands.";
		sendMessageToFcm(getFcmMessageJSONDataAndNotification(notificationTitle, notificationBody));
	}*/



	private class RefreshTokenTask extends AsyncTask<String, Void, Void> {
		@Override
		protected Void doInBackground(String... params) {
			try {
				InputStream is =  context1.getAssets().open("firebase-admin-key.json");

				GoogleCredential googleCredential = GoogleCredential
						.fromStream(is)
						.createScoped(Arrays.asList(SCOPE));
				googleCredential.refreshToken();
				String token = googleCredential.getAccessToken();
				System.out.println("juju.........."+token);
				URL url = new URL(FCM_DEALS_ENDPOINT);
				HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();

				httpConn.setRequestProperty("Authorization", "Bearer " + token);
				httpConn.setRequestProperty("Content-Type", "application/json; UTF-8");

				httpConn.setDoOutput(true);
				httpConn.setUseCaches(false);
				httpConn.setRequestMethod("POST");

				DataOutputStream wr = new DataOutputStream(httpConn.getOutputStream());
				wr.writeBytes(params[0]);
				wr.flush();
				wr.close();

				BufferedReader in = new BufferedReader(
						new InputStreamReader(httpConn.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();

				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();

				log.info(response.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	}




	//Using HttpURLConnection it send http post request containing data to FCM server
	private void sendMessageToFcm(String postData) {
		try {

			HttpURLConnection httpConn = getConnection();
			httpConn.setDoOutput(true);
			httpConn.setUseCaches(false);
			httpConn.setRequestMethod("POST");
			
			DataOutputStream wr = new DataOutputStream(httpConn.getOutputStream()); 
			wr.writeBytes(postData);
			wr.flush();
			wr.close();

			BufferedReader in = new BufferedReader(
					new InputStreamReader(httpConn.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			log.info(response.toString());
			System.out.println("done........."+response.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String getAccessToken() throws IOException {
		InputStream is =  context1.getAssets().open("firebase-admin-key.json");

		GoogleCredential googleCredential = GoogleCredential
				.fromStream(is)
				.createScoped(Arrays.asList(SCOPE));
		new RefreshTokenTask1().execute(googleCredential);
		String token = googleCredential.getAccessToken();
		return token;
	}
	private static class RefreshTokenTask1 extends AsyncTask<GoogleCredential, Void, Void> {
		@Override
		protected Void doInBackground(GoogleCredential... params) {
			try {
				params[0].refreshToken();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
	}
	//create HttpURLConnection setting Authorization token
	//and Content-Type header
	private HttpURLConnection getConnection() throws Exception {
		URL url = new URL(FCM_DEALS_ENDPOINT);
		HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

		httpURLConnection.setRequestProperty("Authorization", "Bearer " + getAccessToken());
		httpURLConnection.setRequestProperty("Content-Type", "application/json; UTF-8");
		return httpURLConnection;
	}
/*

	private JsonElement getDealInJsonFormat() {
		
		Deal dealList = prepareLatestDealData();
		Gson gson = new Gson();
		Type type = new TypeToken<Deal>(){}.getType();

		JsonElement jsonElement = gson.toJsonTree(dealList, type);
		return jsonElement;
	}
*/

/*	private String getFcmMessageJSONData() {

		JsonElement dealsJson = getDealInJsonFormat();

		JsonObject jsonObj = new JsonObject();
		jsonObj.addProperty("topic", "deals");
		jsonObj.add("data", dealsJson);

		JsonObject msgObj = new JsonObject();
		msgObj.add("message", jsonObj);

		log.info("json  message "+msgObj.toString());

		return msgObj.toString();
	}
	*/
	private String getFcmMessageJSONNotification(String title, String msg) {
		JsonObject notifiDetails = new JsonObject();
		notifiDetails.addProperty("body", msg);
		notifiDetails.addProperty("title", title);

		JsonObject jsonObj = new JsonObject();
		jsonObj.addProperty("topic", "News");
		jsonObj.add("notification", notifiDetails);

		JsonObject msgObj = new JsonObject();
		msgObj.add("message", jsonObj);

		log.info("json  message "+msgObj.toString());

		return msgObj.toString();
	}
	
	/*private String getFcmMessageJSONDataAndNotification(String title, String msg) {

		JsonElement dealsJson = getDealInJsonFormat();

		JsonObject notifiDetails = new JsonObject();
		notifiDetails.addProperty("body", msg);
		notifiDetails.addProperty("title", title);

		JsonObject jsonObj = new JsonObject();
		jsonObj.addProperty("topic", "deals");
		jsonObj.add("data", dealsJson);
		jsonObj.add("notification", notifiDetails);

		JsonObject msgObj = new JsonObject();
		msgObj.add("message", jsonObj);

		log.info("json  message "+msgObj.toString());

		return msgObj.toString();
	}*/

	/*
	private Deal prepareLatestDealData() {
		List<Deal> dealList = new ArrayList<Deal>();
		Deal deal = new Deal();
		deal.setStoreNAME("Bestbuy");
		deal.setDeal("Get upto 10% off on Laptops");
		deal.setDealDesc("Get upto 10% off on dell, hp and lenovo laptops.");
		deal.setExpiry("20180110");
		deal.setCode("NORDSH");
		dealList.add(deal);

		return deal;
	}*/
	
}