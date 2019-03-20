package com.appsinventiv.iandroid;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private EditText email, password;
    private Button signin, btnSignUp;
    private FirebaseAuth auth;
    private DatabaseReference mDatabase;
    String userid, usergraphic;
    EditText name, lastname, phoneno;
//    private UserModel userModel;

    // RadioGroup radioGroup;


    String color;
    int clickcount = 0;

    RadioGroup options;
    RadioButton selected;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

//        userModel = new UserModel();

        signin = (Button) findViewById(R.id.forlogin);
        btnSignUp = (Button) findViewById(R.id.registration);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        name = (EditText) findViewById(R.id.name);
        lastname = (EditText) findViewById(R.id.lastname);
        phoneno = (EditText) findViewById(R.id.phoneno);
        options = (RadioGroup) findViewById(R.id.radiogroup);


        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (name.getText().length() == 0) {
                    name.setError("Enter name");
                } else if (lastname.getText().length() == 0) {
                    lastname.setError("Enter last name");
                } else if (phoneno.getText().length() == 0) {
                    phoneno.setError("Enter number");
                } else if (email.getText().length() == 0) {
                    email.setError("Enter email");
                } else if (password.getText().length() == 0) {
                    password.setError("Enter password");
                } else if (color == null) {
                    Toast.makeText(MainActivity.this, "Please choose color", Toast.LENGTH_SHORT).show();
                } else {
                    signUp();
                }
            }
        });


        options.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                selected = findViewById(checkedId);
                int idx = options.indexOfChild(selected);
                switch (checkedId) {

                    case R.id.Green:
                        RadioButton r = (RadioButton) options.getChildAt(idx);
                        color = r.getText().toString();

                        break;
                    case R.id.Yellow:

                        RadioButton m = (RadioButton) options.getChildAt(idx);
                        color = m.getText().toString();
                        break;
                    default:
                        Toast.makeText(getApplicationContext(), "Choose Color For security", Toast.LENGTH_SHORT).show();
                        break;
                }

            }
        });

    }

    private void signUp() {
//        mDatabase.child("Users")

        auth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {


                        //progressBar.setVisibility(View.GONE);
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Authentication failed." + task.getException(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // userId=FirebaseAuth.getInstance().getCurrentUser().getUid();
                            //   startActivity( new Intent( MainActivity.this, Signin.class ) );
                            sendDataToDB(task.getResult().getUser().getUid());

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendDataToDB(String uid) {
        mDatabase.child("Users").child(uid).setValue(new UserModel(
                uid,
                name.getText().toString(),
                lastname.getText().toString(),
                phoneno.getText().toString(),
                email.getText().toString(),
                password.getText().toString(),
                color

        )).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                startActivity(new Intent(MainActivity.this, Login.class));
                finish();
            }
        });
    }
}
