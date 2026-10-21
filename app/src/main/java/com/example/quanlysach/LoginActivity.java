package com.example.quanlysach;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.text.InputType;

public class LoginActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state); setContentView(R.layout.activity_login); WindowInsetsHelper.apply(this);
        EditText username = findViewById(R.id.username), password = findViewById(R.id.password);
        TextView error = findViewById(R.id.loginError);
        ((Button) findViewById(R.id.togglePassword)).setOnClickListener(v -> {
            int cursor = password.getSelectionStart();
            boolean hidden = (password.getInputType() & InputType.TYPE_TEXT_VARIATION_PASSWORD) == InputType.TYPE_TEXT_VARIATION_PASSWORD;
            password.setInputType(hidden ? InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                    : InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            password.setSelection(Math.max(0, cursor));
            ((Button) v).setText(hidden ? "Ẩn" : "Hiện");
        });
        ((Button) findViewById(R.id.loginButton)).setOnClickListener(v -> {
            String user = username.getText().toString().trim(), pass = password.getText().toString();
            // Báo lỗi ngay tại ô bị thiếu để người dùng biết cần nhập thông tin nào.
            if (user.isEmpty()) { username.setError("Vui lòng nhập tên đăng nhập"); error.setText(""); return; }
            if (pass.trim().isEmpty()) { password.setError("Vui lòng nhập mật khẩu"); error.setText(""); return; }
            username.setError(null); password.setError(null);
            // Tài khoản demo cố định; đăng nhập thành công thì bỏ màn hình này khỏi Back stack.
            if ("admin".equals(user) && "123456".equals(pass)) {
                startActivity(new Intent(this, MainActivity.class)); finish();
            } else error.setText("Sai tên đăng nhập hoặc mật khẩu");
        });
    }
}
