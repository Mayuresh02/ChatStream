package com.project.chatstream.Activity;

import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.project.chatstream.R;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    TextView signup,signin_btn;
    EditText login_email,login_password;
    FirebaseAuth auth;
    ProgressDialog pg;
    String email_pattern = "^[_A-Za-z0-9-+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        pg = new ProgressDialog(this);
        pg.setMessage("Please Wait...");
        pg.setCancelable(false);

        auth = FirebaseAuth.getInstance();

        signin_btn = findViewById(R.id.sign_in_btn);
        login_email = findViewById(R.id.login_email);
        login_password = findViewById(R.id.login_password);
        signup = findViewById(R.id.signup);

        signin_btn.setOnClickListener(v ->
        {
            pg.show();
            String email=login_email.getText().toString();
            String pass = login_password.getText().toString();

            if(TextUtils.isEmpty(email) || TextUtils.isEmpty(pass))
            {
                pg.dismiss();
                Toast.makeText(LoginActivity.this, "Enter Valid Data", Toast.LENGTH_SHORT).show();
            }
            else if(!email.matches(email_pattern))
            {
                pg.dismiss();
                login_email.setError("Invalid Email");
                Toast.makeText(LoginActivity.this,"Enter Valid Email",Toast.LENGTH_SHORT).show();
            }
            else if(pass.length()<6)
            {
                pg.dismiss();
                login_password.setError("Invalid Password");
                Toast.makeText(LoginActivity.this,"Password must be greater than 6 characters",Toast.LENGTH_SHORT).show();
            }
            else
            {
                auth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(task ->
                {
                    if(task.isSuccessful())
                    {
                        pg.dismiss();
                        startActivity(new Intent(LoginActivity.this,HomeActivity.class));
                        finishAffinity();
                    }
                    else
                    {
                        pg.dismiss();
                        Toast.makeText(LoginActivity.this,"Login Failed",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        signup.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this,RegistrationActivity.class)));
    }

    public void onBackPressed()
    {
        finishAffinity();
    }
}