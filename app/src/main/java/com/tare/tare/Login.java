package com.tare.tare;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Login extends AppCompatActivity implements View.OnClickListener {
    private static final String SHARED_PREFS_KEY = "savedUser";
    private SharedPreferences savedLogin;
    @BindView(R.id.login_email_entry)
    EditText loginEmail;
    @BindView(R.id.login_password_entry)
    EditText loginPassword;
    @BindView(R.id.login_button)
    Button loginButton;
    @BindView(R.id.create_account)
    Button createAccount;
    @BindView(R.id.remember_me_box)
    CheckBox rememberMe;
    private FirebaseAuth mAuth;
    @BindView(R.id.progress_bar_login)
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();
        loginButton.setOnClickListener(this);
        createAccount.setOnClickListener(this);
        savedLogin = getApplicationContext().getSharedPreferences(SHARED_PREFS_KEY, MODE_PRIVATE);

        if (savedLogin.getBoolean("isChecked", false)) {
            loginEmail.setText(savedLogin.getString("email", null));
            loginPassword.setText(savedLogin.getString("password", null));
            rememberMe.setChecked(savedLogin.getBoolean("isChecked", false));
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_button:
                SharedPreferences.Editor editor = savedLogin.edit();
                if (rememberMe.isChecked()) {
                    editor.putString("email", loginEmail.getText().toString());
                    editor.putString("password", loginPassword.getText().toString());
                    editor.putBoolean("isChecked", rememberMe.isChecked());
                    editor.commit();
                } else {
                    editor.putBoolean("isChecked", rememberMe.isChecked());
                    editor.commit();
                }
                userLogin();
                break;
            case R.id.create_account:
                Intent signUpScreen = new Intent(Login.this, SignUp.class);
                startActivity(signUpScreen);
                break;
        }
    }

    private void userLogin() {
        progressBar.setVisibility(View.VISIBLE);
        String email = loginEmail.getText().toString().trim();
        String password = loginPassword.getText().toString().trim();

        if (email.isEmpty()) {
            loginEmail.requestFocus();
            loginEmail.setError("Required");
            progressBar.setVisibility(View.GONE);
            return;

        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            loginEmail.setError("Please enter a valid email");
            progressBar.setVisibility(View.GONE);
            return;

        } else if (password.isEmpty()) {
            loginPassword.requestFocus();
            loginPassword.setError("Required");
            progressBar.setVisibility(View.GONE);
            return;

        }
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_SHORT).show();
                    Intent homeScreen = new Intent(Login.this, Home.class);
                    startActivity(homeScreen);
//                    finish();
                    progressBar.setVisibility(View.GONE);
                } else {
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });


    }


}
