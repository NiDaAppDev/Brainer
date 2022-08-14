package com.nidaappdev.performancemeasurement.activities;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.dd.CircularProgressButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.labters.lottiealertdialoglibrary.DialogTypes;
import com.nidaappdev.performancemeasurement.App;
import com.nidaappdev.performancemeasurement.Lottie.DialogHandler;
import com.nidaappdev.performancemeasurement.R;
import com.nidaappdev.performancemeasurement.publicClassesAndInterfaces.PublicMethods;

import java.util.ArrayList;
import java.util.Random;

public class SignInActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private Toolbar toolbar;
    private TextInputEditText usernameET, passwordET;
    private CircularProgressButton signInBtn;
    private ImageButton googleSignInBtn;
    private TextView registerSuggestionTV;

    public FirebaseAuth authenticator;
    private GoogleApiClient googleApiClient;
    private DialogHandler dialogHandler;

    private static final int RC_GOOGLE_SIGN_IN = 9001;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        usernameET = findViewById(R.id.sign_in_user_name_text_input_edit_text);
        passwordET = findViewById(R.id.sign_in_password_text_input_edit_text);
        signInBtn = findViewById(R.id.sign_in_btn);
        googleSignInBtn = findViewById(R.id.google_sign_in_btn);
        registerSuggestionTV = findViewById(R.id.register_suggestion_TV);

        authenticator = FirebaseAuth.getInstance();

        dialogHandler = DialogHandler.getDialogHandler(this);

        initGoogleSignInVariables();
        initRegisterSuggestionTV();
        initLogInBtn();
        initGoogleSignInBtn();
    }

    private void initRegisterSuggestionTV() {
        registerSuggestionTV.setOnClickListener(view -> {
            Intent i = new Intent(SignInActivity.this, RegisterActivity.class);
            startActivity(i);
            finish();
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initLogInBtn() {
        signInBtn.setOnClickListener(view -> {
            String username = usernameET.getText().toString(),
                    password = passwordET.getText().toString();

            if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                dialogHandler.showDialog(SignInActivity.this,
                        SignInActivity.this,
                        "Missing Fields",
                        "One or more of the fields is empty." +
                                "\nPlease make sure to fill all of them.",
                        "OK",
                        (Runnable) () -> {

                        },
                        DialogTypes.TYPE_ERROR,
                        "");
                return;
            } else {
                signInBtn.setIndeterminateProgressMode(true);
                authenticator.signInWithEmailAndPassword(username, password).addOnCompleteListener(task -> {
                    Handler handler = new Handler();
                    if (!task.isSuccessful()) {
                        App.showSnackBar(signInBtn.getRootView(),
                                getLayoutInflater(),
                                "Failed Signing In",
                                PublicMethods.getValueOrDefault(task.getException().getMessage(),
                                        "Unknown Error Has Occurred"));
                        signInBtn.setProgress(-1);
                        handler.postDelayed(() -> signInBtn.setProgress(0), 2000);
                        return;
                    }
                    Intent i = new Intent(SignInActivity.this, MainActivity.class);
                    Runnable onFinishLoading = () -> {
                        startActivity(i);
                        finish();
                    };
                    App.loadUserDataFromCloud(signInBtn, onFinishLoading);
                });
            }

        });
    }

    private void initGoogleSignInBtn() {
        googleSignInBtn.setOnClickListener(view -> googleSignIn());
    }

    private void initGoogleSignInVariables() {
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();
    }

    private void googleSignIn() {
        signInBtn.setIndeterminateProgressMode(true);
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_GOOGLE_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleGoogleSignInResult(result);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void handleGoogleSignInResult(GoogleSignInResult result) {
        Handler handler = new Handler();
        if (!result.isSuccess()) {
            App.showSnackBar(signInBtn.getRootView(),
                    getLayoutInflater(),
                    "Failed Signing In",
                    result.getStatus().getStatusMessage());
            signInBtn.setProgress(-1);
            handler.postDelayed(() -> signInBtn.setProgress(0), 2000);
            return;
        }
        Intent i = new Intent(SignInActivity.this, MainActivity.class);
        Runnable onFinishLoading = () -> {
            startActivity(i);
            finish();
        };
        App.loadUserDataFromCloud(signInBtn, onFinishLoading);

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private boolean isSignedIn() {
        return authenticator.getCurrentUser() != null || GoogleSignIn.getLastSignedInAccount(this) != null;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (isSignedIn()) {
            Intent i = new Intent(SignInActivity.this, MainActivity.class);
            startActivity(i);
            this.finish();
        }
    }

    private String generatePassword() {
        StringBuilder password = new StringBuilder();
        ArrayList<Character> possibleChars = new ArrayList<>();
        for (char c = 'a'; c <= 'z'; ++c) {
            possibleChars.add(c);
        }
        for (char c = 'A'; c <= 'Z'; ++c) {
            possibleChars.add(c);
        }
        for (int i = 0; i <= 9; i++) {
            possibleChars.add((char) i);
        }
        for (int i = 0; i < 8; i++) {
            password.append(possibleChars.get(new Random().nextInt(possibleChars.size())));
        }
        return password.toString();
    }
}