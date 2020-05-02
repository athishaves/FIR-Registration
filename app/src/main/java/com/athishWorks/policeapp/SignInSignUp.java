package com.athishWorks.policeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class SignInSignUp extends AppCompatActivity {


    TextView textView;
    TextInputLayout nameEditLayout, gmailEditLayout, passEditLayout, idEditLayout;
    TextInputEditText nameEditText, gmailEditText, passEditText, idEditText;
    Button button;

    String name, gmail, pass, id;

    int goneCount;

    ProgressDialog progressBar;
    FirebaseAuth mAuth;

    private void declarations() {

        goneCount = 0;

        nameEditLayout = findViewById(R.id.usernameInputLayout);
        gmailEditLayout = findViewById(R.id.emailInputLayout);
        passEditLayout = findViewById(R.id.passwordInputLayout);
        idEditLayout = findViewById(R.id.idInputLayout);

        nameEditText = findViewById(R.id.usernameEditText);
        gmailEditText = findViewById(R.id.emailEditText);
        passEditText = findViewById(R.id.passwordEditText);
        idEditText = findViewById(R.id.idEditText);

        textView = findViewById(R.id.textView);
        textView.setText(R.string.click_here_to_login);

        button = findViewById(R.id.button);
        button.setText(R.string.sign_up);

        progressBar = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_sign_up);

        declarations();

    }


    public void text1OnClick(View v) {
        Animation fade_in = AnimationUtils.loadAnimation(SignInSignUp.this, R.anim.fade_in);
        Animation small_fade_in = AnimationUtils.loadAnimation(SignInSignUp.this, R.anim.small_fade_in);
        Animation slide_out = AnimationUtils.loadAnimation(SignInSignUp.this, R.anim.slide_out);
        Animation slide_in = AnimationUtils.loadAnimation(SignInSignUp.this, R.anim.slide_in);

        gmailEditText.setText("");
        nameEditText.setText("");
        passEditText.setText("");
        idEditText.setText("");

        textView.setAnimation(fade_in);

        if (goneCount==0) {
            Log.i("ButtonClick", "GoneCount = " + goneCount);
            slide_out.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    nameEditLayout.setVisibility(View.GONE);
                    idEditLayout.setVisibility(View.GONE);
                }
                @Override
                public void onAnimationEnd(Animation animation) {
                    gmailEditText.requestFocus();
                }
                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            nameEditLayout.setAnimation(slide_out);
            idEditLayout.setAnimation(slide_out);

            button.setText(R.string.sign_in);
            textView.setText(R.string.click_here_to_signin);
            goneCount=1;

        } else if (goneCount==1) {
            Log.i("ButtonClick", "GoneCount = " + goneCount);
            slide_in.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    idEditLayout.setVisibility(View.VISIBLE);
                    nameEditLayout.setVisibility(View.VISIBLE);
                }
                @Override
                public void onAnimationEnd(Animation animation) {
                    nameEditText.requestFocus();
                }
                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            nameEditLayout.setAnimation(slide_in);
            idEditLayout.setAnimation(slide_in);

            button.setText(R.string.sign_up);
            textView.setText(R.string.click_here_to_login);
            goneCount=0;
        }
        button.setAnimation(small_fade_in);
    }

    public void buttonOnClick(View v) {
        progressBar.show();
        progressBar.setCancelable(false);
        progressBar.setMessage("Validating Details...");
        name = nameEditText.getText().toString().trim();
        gmail = gmailEditText.getText().toString().trim();
        pass = passEditText.getText().toString().trim();
        id = idEditText.getText().toString().trim();
        if (goneCount==0) {
            if (validateSignUpItems()) {
                validateItems();
                progressBar.dismiss();
                return;
            }
            if (validateItems()) {
                progressBar.dismiss();
                return;
            }
            progressBar.setMessage("Signing Up...");
            SignUp();
        }
        else {
            if (validateItems()) {
                progressBar.dismiss();
                return;
            }
            progressBar.setMessage("Signing In...");
            SignIn();
        }
    }

    private boolean validateSignUpItems() {
        boolean isWrong = false;
        if (name.isEmpty()) {
            nameEditText.setError("Field cannot be empty");
            isWrong = true;
        }
        if (id.isEmpty()) {
            idEditText.setError("Field cannot be empty");
            isWrong = true;
        }
        return isWrong;
    }

    private boolean validateItems() {
        boolean isWrong = false;
        if (gmail.isEmpty()) {
            gmailEditText.setError("Field cannot be empty");
            isWrong = true;
        }
        if (pass.isEmpty()) {
            passEditText.setError("Field cannot be empty");
            isWrong = true;
        }
        return isWrong;
    }

    private void SignIn() {
        Log.i("TAG", "Signed in");
        mAuth.signInWithEmailAndPassword(gmail, pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.dismiss();
                        if (task.isSuccessful()) {
                            callAToast("User login successful");
                            callAToast("Welcome " + mAuth.getCurrentUser().getDisplayName());
                            startActivity(new Intent(SignInSignUp.this, FirForm.class));
                            finish();
                        } else {
                            callAToast(task.getException().getMessage());
                        }
                    }
                });
    }

    private void SignUp() {

        mAuth.createUserWithEmailAndPassword(gmail, pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.dismiss();
                        if (task.isSuccessful()) {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(name)
                                    .build();
                            user.updateProfile(profile)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            callAToast("User registered successfully");
                                            callAToast("Welcome " + name);
                                            startActivity(new Intent(SignInSignUp.this, FirForm.class));
                                            finish();
                                        }
                                    });

                        } else {
                            callAToast(task.getException().getMessage());
                        }
                    }
                });
    }

    private void callAToast(String a) {
        Toast.makeText(this, a, Toast.LENGTH_SHORT).show();
    }

}
