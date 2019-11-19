package com.example.adoptapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adoptapp.model.Institucion;
import com.example.adoptapp.views.ActivityMenuKeeper;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.model.value.GeoPointValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Objects;

public class ActivityRegistrarFundacion extends AppCompatActivity {

    private EditText et_nombre;
    private EditText et_nombreEncargado;
    private EditText et_email;
    private EditText et_contrasena;
    private EditText et_confirmarContrasena;
    private EditText et_telefono;
    private EditText et_municipio;
    private EditText et_descripcion;
    private Button button_registrarse;
    private ImageButton imageButton_galeria;
    private ImageButton imageButton_camara;
    private ImageView imageView_foto;

    //------------------------------------------------------------------

    private StorageReference storageReference;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private FirebaseAuth mAuth;

    //--------------------------PERMISOS------------------------------------------------
    private static final String TAG = "ActivityRegistro";
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 0;
    private static final int MY_PERMISSIONS_READ_EXTERNAL_STORAGE = 1;
    private static final int IMAGE_PICKER_REQUEST = 2;
    private static final int REQUEST_IMAGE_CAPTURE = 3;

    //----------------------LOCATION--------------------------------------

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;

    private static String[] PERMISSIONS = {
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.ACCESS_FINE_LOCATION

    };

    private Institucion fundacion;
    private String idUser;

    private GeoPoint ubicacionFundacion;
    private Bitmap selectedImage;
    private Address address;

    private FirebaseFirestoreSettings settings;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_fundacion);

         et_nombre = findViewById(R.id.editTextNombre);
         et_nombreEncargado = findViewById(R.id.editTextNombreEncargado);
         et_email = findViewById(R.id.editTextEmail);
         et_contrasena = findViewById(R.id.editTextContrasena);
         et_confirmarContrasena = findViewById(R.id.editTextConfirmarContrasena);
         et_telefono = findViewById(R.id.editTextTelefono);
         et_municipio = findViewById(R.id.editTextCiudad);
         et_descripcion = findViewById(R.id.editTextDescripcion);
         button_registrarse = findViewById(R.id.buttonRegistrarse);
         imageButton_galeria = findViewById(R.id.imageButtonGallery);
         imageButton_camara = findViewById(R.id.imageButtonCamera);
         imageView_foto = findViewById(R.id.imageViewPredefinida);

         settings = new FirebaseFirestoreSettings.Builder().build();
         storageReference = FirebaseStorage.getInstance().getReference();
         mAuth = FirebaseAuth.getInstance();
         ubicacionFundacion = new GeoPoint(0,0);

         fundacion = null;

        imageButton_camara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //CameraUtils.startDialog(Signup_owner.this);
                if (ContextCompat.checkSelfPermission(v.getContext(), Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_GRANTED) {
                    tomarFoto();
                }else{
                    requestPermission(ActivityRegistrarFundacion.this, PERMISSIONS[0], "Acceso " +
                                    "a cámara necesario",
                            MY_PERMISSIONS_REQUEST_CAMERA);
                }
            }
        });

        imageButton_galeria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(view.getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {

                    Intent pickImage = new Intent(Intent.ACTION_PICK);
                    pickImage.setType("image/*");
                    startActivityForResult(pickImage, IMAGE_PICKER_REQUEST);

                }else{
                    requestPermission(ActivityRegistrarFundacion.this, PERMISSIONS[1], "Leer " +
                                    "almacenamiento es necesario",
                            MY_PERMISSIONS_READ_EXTERNAL_STORAGE);
                }
            }
        });

        button_registrarse.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                    fundacion = registrarFundacion();
                }
                if(fundacion != null){
                    registrar();
                }
            }
        });
    }

    private void requestPermission(Activity context, String permiso, String justificacion, int idCode){
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(context, permiso) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            if (ActivityCompat.shouldShowRequestPermissionRationale(context, permiso)) {
                Toast toast = Toast.makeText(context, justificacion, Toast.LENGTH_SHORT);
                toast.show();
            }
            //Request the permission
            ActivityCompat.requestPermissions(context, new String[]{permiso}, idCode);
        }
    }

    private void tomarFoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }


    private Institucion registrarFundacion(){
        boolean retornar = true;

        String nombre = et_nombre.getText().toString();
        String nombreEncargado = et_nombreEncargado.getText().toString();
        String email = et_email.getText().toString();
        String contrasena = et_contrasena.getText().toString();
        String confirmar_contrasena = et_confirmarContrasena.getText().toString();
        String telefono = et_telefono.getText().toString();
        String municipio = et_municipio.getText().toString();
        String descripcion = et_descripcion.getText().toString();

        if(imageView_foto.getDrawable() == null){
            retornar = false;
            Toast toast = Toast.makeText(ActivityRegistrarFundacion.this, "Se debe seleccionar o " +
                    "tomar una foto", Toast.LENGTH_LONG);
            toast.show();
        }

        if(nombre.isEmpty()){
            et_nombre.setError("Campo obligatorio");
        }

        if(nombreEncargado.isEmpty()){
            et_nombreEncargado.setError("Campo obligatorio");
            retornar = false;
        }

        if(email.isEmpty()){
            et_email.setError("Campo obligatorio");
            retornar = false;
        }
        else{
            if(emailValido(email)){
                et_email.setError(null);
            }
        }

        if(contrasena.isEmpty()){
            et_contrasena.setError("Campo obligatorio");
            retornar = false;
        }

        else{
            if(contrasena.length() < 8){
                et_contrasena.setError("Debe tener minimo 6 caracteres");
                retornar = false;
            }
        }

        if(!confirmar_contrasena.equals(contrasena)) {
            et_confirmarContrasena.setError("La contraseña no coincide");
            retornar = false;
        }

        if(telefono.isEmpty()){
            et_telefono.setError("Campo obligatorio");
            retornar = false;
        }

        else{
            if(!isNumeric(telefono)){
                et_telefono.setError("Todos los caracteres deben ser números");
                retornar = false;
            }
        }

        if(!retornar){
            return null;
        }

        return new Institucion(email,nombreEncargado,municipio,nombre,Long.parseLong(telefono),null,descripcion);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case REQUEST_IMAGE_CAPTURE:
                if (resultCode == RESULT_OK) {
                    try {
                        Bundle extras = data.getExtras();
                        Bitmap imageBitmap;
                        if (extras != null) {
                            imageBitmap = (Bitmap) extras.get("data");
                            imageView_foto.setImageBitmap(imageBitmap);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return;

            case IMAGE_PICKER_REQUEST:
                if (resultCode == RESULT_OK) {
                    try {
                        final Uri imageUri = data.getData();
                        final InputStream imageStream;
                        if (imageUri != null) {
                            imageStream = getContentResolver().openInputStream(imageUri);
                            final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                            imageView_foto.setImageBitmap(selectedImage);
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }


        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case MY_PERMISSIONS_REQUEST_CAMERA:
            {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    tomarFoto();
                }
            }
            break;

            case MY_PERMISSIONS_READ_EXTERNAL_STORAGE:
            {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent pickImage = new Intent(Intent.ACTION_PICK);
                    pickImage.setType("image/*");
                    startActivityForResult(pickImage, IMAGE_PICKER_REQUEST);
                }
            }
            break;

        }
    }

    private boolean emailValido(String email){
        if (!email.contains("@") ||
                !email.contains(".") ||
                email.length() < 5) {
            et_email.setError("Correo electrónico incorrecto");
            return false;
        }
        return true;
    }

    public static boolean isNumeric(String strNum) {
        try {
            long d = Long.parseLong(strNum);
        } catch (NumberFormatException | NullPointerException nfe) {
            return false;
        }
        return true;
    }

    private void registrar(){

        mAuth.createUserWithEmailAndPassword(et_email.getText().toString(), et_contrasena.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                            FirebaseUser user = mAuth.getCurrentUser();
                            if(user!=null){ //Update user Info
                                fundacion.setId(user.getUid());
                                UserProfileChangeRequest.Builder upcrb = new UserProfileChangeRequest.Builder();
                                upcrb.setDisplayName(et_nombre.getText().toString());
                                upcrb.setPhotoUri(Uri.parse("clientes/photo_"+user.getUid()+".jpg"));
                                user.updateProfile(upcrb.build());

                                Toast.makeText(ActivityRegistrarFundacion.this, "Usuario creado con éxito",
                                        Toast.LENGTH_SHORT).show();

                                idUser = user.getUid();
                                crearCliente(user);
                            }
                        }
                        if (!task.isSuccessful()) {
                            Toast.makeText(ActivityRegistrarFundacion.this, "Fallo la creación de usuario: "
                                            + Objects.requireNonNull(task.getException()).toString(),
                                    Toast.LENGTH_SHORT).show();
                            Log.e(TAG, Objects.requireNonNull(task.getException().getMessage()));
                            button_registrarse.setEnabled(true);
                        }
                    }
                });

    }

    private void crearCliente(final FirebaseUser user){
        fundacion.setImagenPrincipal("instituciones/"+ user.getUid()+"/logo.jpg" );
        db.collection("instituciones").document(user.getUid())
                .set(fundacion)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                        subirFoto(user.getUid()+"/logo.jpg",
                                ((BitmapDrawable)imageView_foto.getDrawable()).getBitmap());

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                        Toast.makeText(ActivityRegistrarFundacion.this, "Fallo la creación de usuario: ",
                                Toast.LENGTH_SHORT).show();
                        button_registrarse.setEnabled(true);
                    }
                });

    }

    public void subirFoto(String ruta, Bitmap photo){
        db.setFirestoreSettings(settings);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = storageReference.child("instituciones").child(ruta).putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.i("ERROR", "No se pudo subir la foto");
                button_registrarse.setEnabled(true);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.i(TAG, "Foto subida");
                Intent i = new Intent(getApplicationContext() , ActivityMenuKeeper.class);

                i.putExtra("nombre", fundacion.getNombre() );
                i.putExtra("uid", idUser);
                i.putExtra("Latitud", fundacion.getUbicacion().getLatitude() );
                i.putExtra("Longitud",   fundacion.getUbicacion().getLongitude() );
                i.putExtra("PathPhoto",  fundacion.getImagenPrincipal() );

                startActivity(i);
                finish();
            }
        });
    }
}
