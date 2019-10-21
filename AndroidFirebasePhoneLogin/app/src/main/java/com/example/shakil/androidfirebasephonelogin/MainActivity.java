package com.example.shakil.androidfirebasephonelogin;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.common.internal.service.Common;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dmax.dialog.SpotsDialog;

public class MainActivity extends AppCompatActivity {

    private static final int APP_REQUEST_CODE = 1000;

    @BindView(R.id.btn_login)
    Button btn_login;

    @OnClick(R.id.btn_login)
    void loginUser(){
        startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder()
                .setAvailableProviders(providers).build(), APP_REQUEST_CODE);
    }

    private List<AuthUI.IdpConfig> providers;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(MainActivity.this);

        init();
    }

    private void init() {
        providers = Arrays.asList(new AuthUI.IdpConfig.PhoneBuilder().build());
        firebaseAuth = FirebaseAuth.getInstance();

        listener = firebaseAuth1 -> {
            FirebaseUser user = firebaseAuth1.getCurrentUser();
            if (user != null) {
                FirebaseInstanceId.getInstance()
                        .getInstanceId()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Log.d("BARBERTOKEN", task.getResult().getToken());

                                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }).addOnFailureListener(e -> {
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                });
            } else {
                loginUser();
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == APP_REQUEST_CODE) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            } else {
                Toast.makeText(this, "Failed to sign in", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (listener != null && firebaseAuth != null){
            firebaseAuth.addAuthStateListener(listener);
        }
    }

    @Override
    protected void onStop() {
        if (listener != null && firebaseAuth != null){
            firebaseAuth.removeAuthStateListener(listener);
        }
        super.onStop();
    }
}
