package com.tare.tare;

import android.content.Intent;
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
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignUp extends AppCompatActivity implements View.OnClickListener {
    @BindView(R.id.create_account_button)
    Button createAccount;
    @BindView(R.id.terms_part_one)
    CheckBox termsAndConditions;
    @BindView(R.id.enter_firstname)
    EditText firstNameEntry;
    @BindView(R.id.enter_lastname)
    EditText lastNameEntry;
    @BindView(R.id.zip_code_entry)
    EditText zipCodeEntry;
    @BindView(R.id.enter_email)
    EditText emailEntry;
    @BindView(R.id.password_entry)
    EditText passwordEntry;
    @BindView(R.id.confirm_password_entry)
    EditText confirmPasswordEntry;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    private final String TAG = "userAuthentication";
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();
        createAccount.setOnClickListener(this);
        if (termsAndConditions.isChecked()) {
            createAccount.setEnabled(false);
        } else {
            createAccount.setEnabled(true);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.create_account_button:
                registerUser();
                break;
        }
    }

    private void registerUser() {
        String email = emailEntry.getText().toString().trim();
        String password = passwordEntry.getText().toString().trim();
        String firstName = firstNameEntry.getText().toString().trim();
        String lastName = lastNameEntry.getText().toString().trim();
        String zipCode = zipCodeEntry.getText().toString().trim();
        String confirmPassword = confirmPasswordEntry.getText().toString().trim();

        if (firstName.isEmpty()) {
            firstNameEntry.requestFocus();
            firstNameEntry.setError("Required");
            return;
        } else if (lastName.isEmpty()) {
            lastNameEntry.requestFocus();
            lastNameEntry.setError("Required");
            return;

        } else if (zipCode.isEmpty()) {
            zipCodeEntry.requestFocus();
            zipCodeEntry.setError("Required");
            return;

        } else if (zipCode.length() < 5 || zipCode.length() > 5) {
            zipCodeEntry.requestFocus();
            zipCodeEntry.setError("Please enter a valid Zip Code");
            return;

        } else if (email.isEmpty()) {
            emailEntry.requestFocus();
            emailEntry.setError("Required");
            return;

        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEntry.setError("Please enter a valid email");
            return;

        } else if (password.isEmpty()) {
            passwordEntry.requestFocus();
            passwordEntry.setError("Required");
            return;

        } else if (password.length() < 6) {
            passwordEntry.requestFocus();
            passwordEntry.setError("Password must be a minimum of 6 characters");
            return;

        } else if (confirmPassword.isEmpty()) {
            confirmPasswordEntry.requestFocus();
            confirmPasswordEntry.setError("Required");
            return;

        } else if (!confirmPassword.contentEquals(password)) {
            confirmPasswordEntry.requestFocus();
            confirmPasswordEntry.setError("Password Doesn't Match");
            return;

        } else {

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Authentication Successful.", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "createUserWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        Intent homeScreen = new Intent(SignUp.this, Home.class);
                        startActivity(homeScreen);
//                    updateUI(user);
                    } else {
                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                            Toast.makeText(getApplicationContext(), "Email is already registered", Toast.LENGTH_SHORT).show();
                        }
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(getApplicationContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
//                    updateUI(null);
                    }
                }
            });
        }
        progressBar.setVisibility(View.VISIBLE);

    }

    private void updateUI(FirebaseUser user) {

    }
}
