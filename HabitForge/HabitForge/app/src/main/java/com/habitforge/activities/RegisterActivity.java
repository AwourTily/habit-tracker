package com.habitforge.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.habitforge.R;
import com.habitforge.database.DatabaseHelper;
import com.habitforge.utils.SessionManager;

public class RegisterActivity extends AppCompatActivity {

    private EditText etUsername, etEmail, etPassword, etConfirm;
    private TextInputLayout tilUsername, tilEmail, tilPassword, tilConfirm;
    private Button btnRegister;
    private TextView tvLogin;
    private DatabaseHelper db;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_register);

        db = DatabaseHelper.getInstance(this);
        session = new SessionManager(this);

        etUsername = findViewById(R.id.et_username);
        etEmail    = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etConfirm  = findViewById(R.id.et_confirm);
        tilUsername= findViewById(R.id.til_username);
        tilEmail   = findViewById(R.id.til_email);
        tilPassword= findViewById(R.id.til_password);
        tilConfirm = findViewById(R.id.til_confirm);
        btnRegister= findViewById(R.id.btn_register);
        tvLogin    = findViewById(R.id.tv_login);

        btnRegister.setOnClickListener(v -> attemptRegister());
        tvLogin.setOnClickListener(v -> { finish();
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right); });
    }

    private void attemptRegister() {
        String username = etUsername.getText().toString().trim();
        String email    = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString();
        String confirm  = etConfirm.getText().toString();

        tilUsername.setError(null); tilEmail.setError(null);
        tilPassword.setError(null); tilConfirm.setError(null);

        if (TextUtils.isEmpty(username) || username.length() < 3) {
            tilUsername.setError("Username must be at least 3 characters"); return; }
        if (TextUtils.isEmpty(email) || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Enter a valid email"); return; }
        if (db.isEmailTaken(email)) {
            tilEmail.setError("Email already registered"); return; }
        if (TextUtils.isEmpty(password) || password.length() < 6) {
            tilPassword.setError("Password must be at least 6 characters"); return; }
        if (!password.equals(confirm)) {
            tilConfirm.setError("Passwords do not match"); return; }

        long id = db.registerUser(username, email, password);
        if (id != -1) {
            session.createSession((int)id, username, email);
            Toast.makeText(this, "Welcome to HabitForge, " + username + "! 🌱", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        } else {
            Toast.makeText(this, "Registration failed. Try again.", Toast.LENGTH_SHORT).show();
        }
    }
}
