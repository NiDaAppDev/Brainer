package com.nidaappdev.brainer.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.dd.CircularProgressButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.labters.lottiealertdialoglibrary.DialogTypes;
import com.nidaappdev.brainer.App;
import com.nidaappdev.brainer.Lottie.DialogHandler;
import com.nidaappdev.brainer.R;

public class RegisterActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextInputEditText usernameET, passwordET, confirmPasswordET;
    private CircularProgressButton registerBtn;
    private FirebaseAuth authenticator;
    private DialogHandler dialogHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        usernameET = findViewById(R.id.register_user_name_text_input_edit_text);
        passwordET = findViewById(R.id.register_password_text_input_edit_text);
        confirmPasswordET = findViewById(R.id.register_confirm_password_text_input_edit_text);
        registerBtn = findViewById(R.id.register_btn);

        authenticator = FirebaseAuth.getInstance();

        dialogHandler = DialogHandler.getDialogHandler(this);

        initRegisterButton();
    }

    private void initRegisterButton() {
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                String username = usernameET.getText().toString(),
                password = passwordET.getText().toString(),
                confirmPassword = confirmPasswordET.getText().toString();
                if(!confirmPassword.equals(password)) {
                    dialogHandler.showDialog(RegisterActivity.this,
                            RegisterActivity.this,
                            "Passwords Don't Match",
                            "The passwords are different." +
                                    "\nThe one in the \"Confirm Password\" field does not match the one in the \"Password\" field." +
                                    "\nPlease make sure they match, so we can be sure you know your password.",
                            "OK",
                            (Runnable) () -> {

                            },
                            DialogTypes.TYPE_ERROR,
                            "");
                } else if(TextUtils.isEmpty(username) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
                    dialogHandler.showDialog(RegisterActivity.this,
                            RegisterActivity.this,
                            "Missing Fields",
                            "One or more of the fields is empty." +
                                    "\nPlease make sure to fill all of them.",
                            "OK",
                            (Runnable) () -> {

                            },
                            DialogTypes.TYPE_ERROR,
                            "");
                } else {
                    registerBtn.setIndeterminateProgressMode(true);
                    authenticator.createUserWithEmailAndPassword(username, password).addOnCompleteListener(task -> {
                        Handler handler = new Handler();
                        if(task.isSuccessful()) {
                            registerBtn.setProgress(100);
                            Intent i = new Intent(RegisterActivity.this, MainActivity.class);
                            Runnable onFinishLoading = () -> {
                                startActivity(i);
                                finish();
                            };
                            App.loadUserDataFromCloud(registerBtn, onFinishLoading);
                        } else {
                            registerBtn.setProgress(-1);
                            handler.postDelayed(() -> registerBtn.setProgress(0), 2000);
                        }
                    });
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(RegisterActivity.this, SignInActivity.class);
        startActivity(i);
        finish();
    }
}