package com.habitforge.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.habitforge.R;
import com.habitforge.database.DatabaseHelper;
import com.habitforge.models.User;
import com.habitforge.utils.SessionManager;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private TextInputLayout tilEmail, tilPassword;
    private Button btnLogin;
    private TextView tvSignUp, tvForgot, tvAppTagline;
    private DatabaseHelper db;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Full-screen immersive
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_login);

        db = DatabaseHelper.getInstance(this);
        session = new SessionManager(this);

        // Auto-login if session exists
        if (session.isLoggedIn()) {
            goToMain();
            return;
        }

        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        tilEmail = findViewById(R.id.til_email);
        tilPassword = findViewById(R.id.til_password);
        btnLogin = findViewById(R.id.btn_login);
        tvSignUp = findViewById(R.id.tv_signup);
        tvForgot = findViewById(R.id.tv_forgot);
        tvAppTagline = findViewById(R.id.tv_tagline);

        // Entrance animations
        animateEntrance();

        btnLogin.setOnClickListener(v -> attemptLogin());

        tvSignUp.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        });

        tvForgot.setOnClickListener(v ->
                Toast.makeText(this, "Password reset — coming soon!", Toast.LENGTH_SHORT).show());
    }

    private void attemptLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        tilEmail.setError(null);
        tilPassword.setError(null);

        if (TextUtils.isEmpty(email)) {
            tilEmail.setError("Email is required");
            etEmail.requestFocus();
            return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Enter a valid email");
            etEmail.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            tilPassword.setError("Password is required");
            etPassword.requestFocus();
            return;
        }
        if (password.length() < 6) {
            tilPassword.setError("Password must be at least 6 characters");
            etPassword.requestFocus();
            return;
        }

        btnLogin.setEnabled(false);
        btnLogin.setText("Signing in…");

        User user = db.loginUser(email, password);
        if (user != null) {
            session.createSession(user.getId(), user.getUsername(), user.getEmail());
            Toast.makeText(this, "Welcome back, " + user.getUsername() + "! 🌿", Toast.LENGTH_SHORT).show();
            goToMain();
        } else {
            btnLogin.setEnabled(true);
            btnLogin.setText("Sign In");
            tilPassword.setError("Invalid email or password");
        }
    }

    private void goToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void animateEntrance() {
        View logoGroup = findViewById(R.id.logo_group);
        View formCard = findViewById(R.id.form_card);

        // Logo slides down
        TranslateAnimation logoSlide = new TranslateAnimation(0, 0, -80, 0);
        AlphaAnimation logoFade = new AlphaAnimation(0, 1);
        AnimationSet logoAnim = new AnimationSet(true);
        logoAnim.addAnimation(logoSlide);
        logoAnim.addAnimation(logoFade);
        logoAnim.setDuration(700);
        logoAnim.setFillAfter(true);
        logoGroup.startAnimation(logoAnim);

        // Form slides up with delay
        TranslateAnimation formSlide = new TranslateAnimation(0, 0, 100, 0);
        AlphaAnimation formFade = new AlphaAnimation(0, 1);
        AnimationSet formAnim = new AnimationSet(true);
        formAnim.addAnimation(formSlide);
        formAnim.addAnimation(formFade);
        formAnim.setDuration(700);
        formAnim.setStartOffset(300);
        formAnim.setFillAfter(true);
        formCard.startAnimation(formAnim);
    }
}
