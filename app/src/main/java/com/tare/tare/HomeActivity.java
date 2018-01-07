package com.tare.tare;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.logout_button)
    Button logout;
    @BindView(R.id.welcome_name)
    TextView welcomeName;
    private FirebaseAuth mAuth;
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        logout.setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.logout_button:
                mAuth.signOut();
                LoginManager.getInstance().logOut();
                setLogout();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
//            setLogout();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setLogout() {
        Toast.makeText(getApplicationContext(), "Currently logged out", Toast.LENGTH_SHORT).show();

        Intent logout = new Intent(HomeActivity.this, LoginActivity.class);
        startActivity(logout);
        logout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();
    }

}
