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

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String SHARED_PREFS_KEY = "savedUser";
    private static final String TAG = "facebookAuth";
    private SharedPreferences savedLogin;

    @BindView(R.id.facebook_login_button)
    Button loginFacebook;
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
    private CallbackManager mCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();
        loginButton.setOnClickListener(this);
        createAccount.setOnClickListener(this);
        loginFacebook.setOnClickListener(this);

        savedLogin = getApplicationContext().getSharedPreferences(SHARED_PREFS_KEY, MODE_PRIVATE);

        mCallbackManager = CallbackManager.Factory.create();

        if (savedLogin.getBoolean("isChecked", false)) {
            loginEmail.setText(savedLogin.getString("email", null));
            loginPassword.setText(savedLogin.getString("password", null));
            rememberMe.setChecked(savedLogin.getBoolean("isChecked", false));
        }

        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
                updateUI();

            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError");
                error.printStackTrace();
            }
        });

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
                    editor.apply();
                } else {
                    editor.putBoolean("isChecked", rememberMe.isChecked());
                    editor.commit();
                }
                userLogin();
                break;

            case R.id.create_account:
                Intent signUpScreen = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(signUpScreen);
                break;

            case R.id.facebook_login_button:
                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("email", "public_profile"));
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Log.d(TAG, "facebook:noUserLogged");
        }
    }

    private void updateUI() {

        Toast.makeText(getApplicationContext(), "Logged in", Toast.LENGTH_SHORT).show();
        Intent homeActivity = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(homeActivity);
        finish();
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
                    Toast.makeText(getApplicationContext(), "Successfully Logged In", Toast.LENGTH_SHORT).show();
                    Intent homeScreen = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(homeScreen);
                    finish();
                    progressBar.setVisibility(View.GONE);
                } else {
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            mAuth.getCurrentUser();
                            updateUI();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
