package com.example.adoptapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ActivityLogin extends AppCompatActivity {

    Button botonIngresar;
    EditText editTextEmail;
    EditText editTextContrasena;
    private FirebaseAuth mAuth;
    FirebaseUser currentUser;

    private static final String TAG = "Login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        botonIngresar = findViewById(R.id.buttonIngresarLogin);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextContrasena = findViewById(R.id.editTextContrasena);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if(currentUser!=null) {
            //entra de una
        }

        botonIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if( validarFormulario() ){
                    /*if(currentUser!=null){
                        Intent intent = new Intent(view.getContext(), ActivityInicioPersona.class);
                        startActivity(intent);
                    } else {

                        String email = editTextEmail.getText().toString();
                        String password = editTextContrasena.getText().toString();
                        signInUser(email, password);
                    }*/

                    if( editTextEmail.getText().toString().equals("persona") ){
                        Intent intent = new Intent(view.getContext(), ActivityInicioPersona.class);
                        startActivity(intent);
                    }
                    if( editTextEmail.getText().toString().equals("institucion") ){
                        Intent intent = new Intent(view.getContext(), ActivityMenuKeeper.class);
                        startActivity(intent);
                    }

                }


            }
        });

    }

    private boolean validarFormulario() {

        boolean valid = true;
        String email = editTextEmail.getText().toString();

        if (TextUtils.isEmpty(email)) {
            editTextEmail.setError("Required");
            valid = false;
        } else {
            editTextEmail.setError(null);
        }

        if(!isEmailValid(email)){
            valid = false;
        }

        String password = editTextContrasena.getText().toString();
        if (TextUtils.isEmpty(password)) {
            editTextContrasena.setError("Required");
            valid = false;
        } else {
            editTextContrasena.setError(null);
        }
        return valid;
    }

    private boolean isEmailValid(String email) {
        if (!email.contains("@") ||
                !email.contains(".") ||
                email.length() < 5)
            return false;
        return true;
    }

    private void signInUser(String email, String password) {

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(ActivityLogin.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });

    }

    private void updateUI(FirebaseUser usuarioActual){

        if(usuarioActual != null){

        }else{
            editTextEmail.setText(R.string.hintEmail);
            editTextContrasena.setText(R.string.hintContrasena);
        }

    }
}
