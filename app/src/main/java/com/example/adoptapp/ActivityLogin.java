package com.example.adoptapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class ActivityLogin extends AppCompatActivity {

    Button botonIngresar;
    EditText editTextEmail;
    EditText editTextContrasena;
    private FirebaseAuth mAuth;
    FirebaseUser currentUser;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private static final String TAG = "ActivityLogin";

    String tipoUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        botonIngresar = findViewById(R.id.buttonIngresarLogin);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextContrasena = findViewById(R.id.editTextContrasena);

        //mAuth = FirebaseAuth.getInstance();
        //currentUser = mAuth.getCurrentUser();
        tipoUsuario = "";

        /*if(currentUser!=null) {
            //verificarTipoUsuario(currentUser.getUid()); //entrar de una
            mAuth.signOut();
        }*/

        botonIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if( validarFormulario() ) {

                    String email = editTextEmail.getText().toString();
                    String password = editTextContrasena.getText().toString();
                    //signInUser(email, password);

                    if (editTextEmail.getText().toString().equals("persona@adoptapp.co")) {
                        Intent intent = new Intent(view.getContext(), ActivityInicioPersona.class);
                        startActivity(intent);
                    } else {
                        if (editTextEmail.getText().toString().equals("institucion@adoptapp.co")) {
                            Intent intent = new Intent(view.getContext(), ActivityMenuKeeper.class);
                            startActivity(intent);
                        }else{
                            Toast.makeText(ActivityLogin.this, "Autenticación fallida",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }

            }
        });

    }

    private boolean validarFormulario() {

        boolean valid = true;
        String email = editTextEmail.getText().toString();

        if (TextUtils.isEmpty(email)) {
            editTextEmail.setError("Requerido");
            valid = false;
        } else {
            editTextEmail.setError(null);
        }

        if(!isEmailValid(email)){
            valid = false;
        }

        String password = editTextContrasena.getText().toString();
        if (TextUtils.isEmpty(password)) {
            editTextContrasena.setError("Requerido");
            valid = false;
        } else {
            if(password.length() < 8){
                editTextContrasena.setError("Debe tener mínimo 8 caracteres");
                valid = false;
            }else {
                editTextContrasena.setError(null);
            }
        }
        return valid;
    }

    private boolean isEmailValid(String email) {
        if (!email.contains("@") ||
                !email.contains(".") ||
                email.length() < 5) {
            editTextEmail.setError("Correo electrónico incorrecto");
            return false;
        }
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
                            currentUser = mAuth.getCurrentUser();
                            updateUI();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            currentUser = null;
                            Toast.makeText(ActivityLogin.this, "Autenticación fallida",
                                    Toast.LENGTH_SHORT).show();
                            updateUI();
                        }
                    }
                });

    }

    private void updateUI(){

        if(currentUser != null){
            verificarTipoUsuario(currentUser.getUid());
        }

    }

    private void verificarTipoUsuario(String userId){

        db.collection("usuarios")
                .whereEqualTo("UserId", userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                tipoUsuario = document.getString("Tipo");
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                            lanzarProximaActividad();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    private void lanzarProximaActividad(){
        if (tipoUsuario.equals("Persona")){
            Intent intent = new Intent(ActivityLogin.this, ActivityInicioPersona.class);
            startActivity(intent);
        }else{
            Intent intent = new Intent(ActivityLogin.this, ActivityMenuKeeper.class);
            startActivity(intent);
        }
    }

}
