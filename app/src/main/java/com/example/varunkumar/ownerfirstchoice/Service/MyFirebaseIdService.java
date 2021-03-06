package com.example.varunkumar.ownerfirstchoice.Service;

import com.example.varunkumar.ownerfirstchoice.Common.Common;
import com.example.varunkumar.ownerfirstchoice.model.Token;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyFirebaseIdService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        updateToServer(refreshedToken);
    }

    private void updateToServer(String refreshedToken) {
        if (Common.currentUser != null) {

            FirebaseDatabase db = FirebaseDatabase.getInstance();
            DatabaseReference tokens = db.getReference("Tokens");
            Token data = new Token(refreshedToken, true);//for server its true;
            tokens.child(Common.currentUser.getPhone()).setValue(data);
        }
    }
}
