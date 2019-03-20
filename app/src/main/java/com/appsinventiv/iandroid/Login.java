package com.appsinventiv.iandroid;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {
    private EditText inputEmail, inputPassword;
    private FirebaseAuth auth;
    private Button btnSignup, btnLogin;
    RadioGroup radioGroup;
//    UserModel userModel;

    String chosecolor;
    RadioButton radioButton;
    DatabaseReference mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        inputEmail = (EditText) findViewById(R.id.loginemail);
        inputPassword = (EditText) findViewById(R.id.loginpassword);
        auth = FirebaseAuth.getInstance();
        mDb=FirebaseDatabase.getInstance().getReference();
        //progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnSignup = (Button) findViewById(R.id.registrationbtn);
        btnLogin = (Button) findViewById(R.id.signinbtn);
        radioGroup = (RadioGroup) findViewById(R.id.selectradiogroup);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (inputEmail.getText().length() == 0) {
                    inputEmail.setError("Enter email");
                } else if (inputPassword.getText().length() == 0) {
                    inputPassword.setError("Enter password");
                } else if (chosecolor == null) {
                    Toast.makeText(Login.this, "Choose color", Toast.LENGTH_SHORT).show();
                } else {
                    loginUser();
                }

            }
        });

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, MainActivity.class));
            }

        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {


                radioButton = (RadioButton) findViewById(checkedId);
                int selectedId = radioGroup.indexOfChild(radioButton);
                switch (checkedId) {

                    case R.id.selectGreen:
                        RadioButton r = (RadioButton) radioGroup.getChildAt(selectedId);
                        chosecolor = r.getText().toString();
                        break;


                    case R.id.selectYellow:
                        RadioButton m = (RadioButton) radioGroup.getChildAt(selectedId);
                        chosecolor = m.getText().toString();
                        break;


                    default:
                        Toast.makeText(getApplicationContext(), "Choose Color For security", Toast.LENGTH_SHORT).show();
                        break;

                }
            }
        });

    }

    private void loginUser() {
        auth.signInWithEmailAndPassword(inputEmail.getText().toString(), inputPassword.getText().toString())
                .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        // progressBar.setVisibility(View.GONE);
                        if (!task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "User Not Valid", Toast.LENGTH_SHORT).show();

                        } else {

                            checkUserFromDB(task.getResult().getUser().getUid());
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Login.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void checkUserFromDB(String uid) {
        mDb.child("Users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    UserModel model = dataSnapshot.getValue(UserModel.class);
                    if (model != null) {
                        if (model.getColor().equalsIgnoreCase(chosecolor)) {
                            Toast.makeText(Login.this, "" + model.getName(), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Login.this, DashBoard.class));
                            finish();
                        } else {
                            Toast.makeText(Login.this, "Wrong color", Toast.LENGTH_SHORT).show();
                        }

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
