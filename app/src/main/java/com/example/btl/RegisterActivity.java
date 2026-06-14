package com.example.btl;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {
    private EditText etRegFullname, etRegUsername, etRegPassword, etRegConfirmPassword;
    private Button btnRegister;
    private TextView tvLogin;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dbHelper = new DatabaseHelper(this);
        etRegFullname = findViewById(R.id.etRegFullname);
        etRegUsername = findViewById(R.id.etRegUsername);
        etRegPassword = findViewById(R.id.etRegPassword);
        etRegConfirmPassword = findViewById(R.id.etRegConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);

        btnRegister.setOnClickListener(v -> {

            String name = etRegFullname.getText().toString().trim();
            String user = etRegUsername.getText().toString().trim();
            String pass = etRegPassword.getText().toString().trim();
            String confirmPass = etRegConfirmPassword.getText().toString().trim();

            if (name.isEmpty() || user.isEmpty() || pass.isEmpty() || confirmPass.isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
            }

            else if (!pass.equals(confirmPass)) {
                Toast.makeText(RegisterActivity.this, "Mật khẩu xác nhận không khớp!", Toast.LENGTH_SHORT).show();
                etRegConfirmPassword.requestFocus(); // Focus lại vào ô xác nhận
            }

            else if (dbHelper.checkUsernameExists(user)) {
                Toast.makeText(RegisterActivity.this, "Tên đăng nhập này đã tồn tại!", Toast.LENGTH_SHORT).show();
                etRegUsername.requestFocus(); // Focus lại vào ô nhập username
            }

            else {
                if (dbHelper.registerUser(user, pass, name)) {
                    Toast.makeText(RegisterActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(RegisterActivity.this, "Đăng ký thất bại do lỗi cơ sở dữ liệu", Toast.LENGTH_SHORT).show();
                }
            }
        });

        tvLogin.setOnClickListener(v -> finish());
    }
}
