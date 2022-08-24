package com.project.chatstream.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.project.chatstream.R;
import com.project.chatstream.ModelClass.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegistrationActivity extends AppCompatActivity {

    TextView signin,signup_btn;
    EditText reg_name,reg_email,reg_pass,reg_cPass;
    CircleImageView profile_image;
    Uri imageUri;
    String imgURI;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    ProgressDialog pg;

    String email_pattern = "^[_A-Za-z0-9-+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        pg = new ProgressDialog(this);
        pg.setMessage("Please Wait...");
        pg.setCancelable(false);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        profile_image = findViewById(R.id.profile_image);
        signin = findViewById(R.id.signin);
        signup_btn = findViewById(R.id.signup_btn);
        reg_name = findViewById(R.id.reg_name);
        reg_email = findViewById(R.id.reg_email);
        reg_pass = findViewById(R.id.reg_pass);
        reg_cPass = findViewById(R.id.reg_cPass);

        signup_btn.setOnClickListener(v ->
        {
            pg.show();
            String name = reg_name.getText().toString();
            String email = reg_email.getText().toString();
            String pass = reg_pass.getText().toString();
            String cPass = reg_cPass.getText().toString();
            String status = "Hey there! I am using ChatStream";

            if(TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(pass) || TextUtils.isEmpty(cPass))
            {
                pg.dismiss();
                Toast.makeText(RegistrationActivity.this, "Please Enter Valid Data", Toast.LENGTH_SHORT).show();
            }
            else if(!email.matches(email_pattern))
            {
                pg.dismiss();
                reg_email.setError("Please Enter Valid Email");
                Toast.makeText(RegistrationActivity.this, "Please Enter Valid Email", Toast.LENGTH_SHORT).show();
            }
            else if(pass.length()<6)
            {
                pg.dismiss();
                reg_pass.setError("Password must be greater than 6 characters");
                Toast.makeText(RegistrationActivity.this, "Password must be greater than 6 characters", Toast.LENGTH_SHORT).show();
            }
            else if(!pass.equals(cPass))
            {
                pg.dismiss();
                reg_cPass.setError("Password Does Not Match");
                Toast.makeText(RegistrationActivity.this, "Password Does Not Match", Toast.LENGTH_SHORT).show();
            }
            else
            {
                auth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(task ->
                {
                    if(task.isSuccessful())
                    {
                        DatabaseReference reference = database.getReference().child("user").child(Objects.requireNonNull(auth.getUid()));
                        StorageReference storageReference = storage.getReference().child("upload").child(auth.getUid());

                        if(imageUri!=null)
                        {
                            storageReference.putFile(imageUri).addOnCompleteListener(task1 ->
                            {
                                if(task1.isSuccessful())
                                {
                                    storageReference.getDownloadUrl().addOnSuccessListener(uri ->
                                    {
                                        imgURI = uri.toString();
                                        Users users = new Users(auth.getUid(),name,email,imgURI,status);
                                        reference.setValue(users).addOnCompleteListener(task11 ->
                                        {
                                            if(task11.isSuccessful())
                                            {
                                                pg.dismiss();
                                                startActivity(new Intent(RegistrationActivity.this,HomeActivity.class));
                                            }
                                            else
                                            {
                                                pg.dismiss();
                                                Toast.makeText(RegistrationActivity.this, "Error while registering", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    });
                                }
                            });
                        }
                        else
                        {
                            String status1 = "Hey there! I am using ChatStream";
                            imgURI = "https://firebasestorage.googleapis.com/v0/b/sms-chat-ded55.appspot.com/o/profile.png?alt=media&token=56249a00-325b-4a67-84d5-c86e8d976970";
                            Users users = new Users(auth.getUid(),name,email,imgURI, status1);
                            reference.setValue(users).addOnCompleteListener(task12 ->
                            {
                                if(task12.isSuccessful())
                                {
                                    pg.dismiss();
                                    startActivity(new Intent(RegistrationActivity.this,HomeActivity.class));
                                }
                                else
                                {
                                    pg.dismiss();
                                    Toast.makeText(RegistrationActivity.this, "Error while registering", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                    else
                    {
                        pg.dismiss();
                        Toast.makeText(RegistrationActivity.this, "OOPS! Something Went Wrong", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        profile_image.setOnClickListener(v ->
        {
            Intent intent=new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent,"Select Picture"),10);
        });

        signin.setOnClickListener(v -> startActivity(new Intent(RegistrationActivity.this,LoginActivity.class)));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==10)
        {
            if(data!=null)
            {
                imageUri = data.getData();
                profile_image.setImageURI(imageUri);
            }
        }
    }
    public void onBackPressed()
    {
        finishAffinity();
    }
}