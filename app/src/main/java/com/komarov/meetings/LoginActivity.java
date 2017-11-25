package com.komarov.meetings;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.komarov.meetings.model.User;
import com.komarov.meetings.service.NetworkService;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "LoginActivity";

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private EditText mEmailField, mPasswordField, mFullNameField, mPositionField;
    private Button mSignInButton;
    private Button mSignUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        mEmailField = findViewById(R.id.field_email);
        mPasswordField = findViewById(R.id.field_password);
        mFullNameField = findViewById(R.id.field_full_name);
        mFullNameField.setVisibility(View.GONE);
        mPositionField = findViewById(R.id.field_position);
        mPositionField.setVisibility(View.GONE);

        mSignInButton = findViewById(R.id.button_sign_in);
        mSignUpButton = findViewById(R.id.button_sign_up);

        mSignInButton.setOnClickListener(this);
        mSignUpButton.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();

        if (mAuth.getCurrentUser() != null) {
            onSignInSuccess();
        }
    }

    private void signIn() {
        Log.d(TAG, "signIn");
        if (!validateForm(false)) {
            return;
        }

        showProgressDialog();
        String email = mEmailField.getText().toString();
        String password = mPasswordField.getText().toString();

        NetworkService.startActionCheckNetwork(this);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    Log.d(TAG, "signIn:onComplete:" + task.isSuccessful());
                    hideProgressDialog();

                    if (task.isSuccessful()) {
                        onSignInSuccess();
                    } else {
                        Toast.makeText(LoginActivity.this, "Sign In Failed",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void signUp() {
        Log.d(TAG, "signUp");
        if (!validateForm(true)) {
            return;
        }

        showProgressDialog();
        String email = mEmailField.getText().toString();
        String password = mPasswordField.getText().toString();
        String userName = mFullNameField.getText().toString();
        String position = mPositionField.getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    Log.d(TAG, "createUser:onComplete:" + task.isSuccessful());
                    hideProgressDialog();

                    if (task.isSuccessful()) {
                        onSignUpSuccess(task.getResult().getUser(), userName, position);
                    } else {
                        Toast.makeText(LoginActivity.this, "Sign Up Failed",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void onSignInSuccess() {
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }

    private void onSignUpSuccess(FirebaseUser user, String userName, String position) {
        writeNewUser(user.getUid(), userName, user.getEmail(), position);

        onSignInSuccess();
    }


    private boolean validateForm(boolean toSignUp) {
        boolean result = true;
        if (TextUtils.isEmpty(mEmailField.getText().toString())) {
            mEmailField.setError(BaseActivity.REQUIRED_ERROR);
            result = false;
        } else {
            mEmailField.setError(null);
        }

        if (TextUtils.isEmpty(mPasswordField.getText().toString())) {
            mPasswordField.setError(BaseActivity.REQUIRED_ERROR);
            result = false;
        } else {
            mPasswordField.setError(null);
        }

        if (toSignUp) {
            if (TextUtils.isEmpty(mFullNameField.getText().toString())) {
                mFullNameField.setError(BaseActivity.REQUIRED_ERROR);
                result = false;
            } else {
                mFullNameField.setError(null);
            }

            if (TextUtils.isEmpty(mPositionField.getText().toString())) {
                mPositionField.setError(BaseActivity.REQUIRED_ERROR);
                result = false;
            } else {
                mPositionField.setError(null);
            }
        }

        return result;
    }

    private void writeNewUser(String userId, String fullName, String email, String position) {
        User user = new User(fullName, email, position);

        mDatabase.child("users").child(userId).setValue(user);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.button_sign_in) {
            signIn();
        } else if (i == R.id.button_sign_up) {
            signUpClick();
        }
    }

    private void signUpClick() {
        if (mSignInButton.getVisibility() == View.VISIBLE) {
            mSignInButton.setVisibility(View.GONE);
            mPositionField.setVisibility(View.VISIBLE);
            mFullNameField.setVisibility(View.VISIBLE);
        } else {
            signUp();
        }
    }

}
