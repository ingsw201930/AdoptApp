package com.example.adoptapp.views;

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
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.adoptapp.R;
import com.example.adoptapp.model.Adopcion;
import com.example.adoptapp.model.Solicitud;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;

public class ActivityFormalizarAdopcion extends AppCompatActivity {

    private Solicitud solicitud;

    private ImageButton ib_tomar_foto;
    private ImageButton ib_subir_foto;
    private ImageView imageViewFoto;
    private EditText et_descripcion;
    private Button btn_formalizar;

    private static String[] PERMISSIONS = {
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
    };

    private static final int REQUEST_IMAGE_CAPTURE = 3;
    private static final int IMAGE_PICKER_REQUEST = 2;

    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 0;
    private static final int MY_PERMISSIONS_READ_EXTERNAL_STORAGE = 1;

    private static final String TAG = "Formalizar adopcion";

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;
    private StorageReference storageReference;
    private FirebaseFirestoreSettings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formalizar_adopcion);

        ib_tomar_foto = findViewById(R.id.ib_subir_imagen_adopcion);
        ib_subir_foto = findViewById(R.id.ib_subir_imagen_galeria_adopcion);
        imageViewFoto = findViewById(R.id.iv_entrega);
        et_descripcion = findViewById(R.id.et_descripcion_entrega);
        btn_formalizar = findViewById(R.id.btn_formalizar);

        solicitud = (Solicitud) getIntent().getSerializableExtra("solicitud");

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        settings = new FirebaseFirestoreSettings.Builder()
                .build();
        storageReference = FirebaseStorage.getInstance().getReference();

        ib_tomar_foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //CameraUtils.startDialog(Signup_owner.this);
                if (ContextCompat.checkSelfPermission(v.getContext(), Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_GRANTED) {
                    takePicture();
                }else{
                    requestPermission(ActivityFormalizarAdopcion.this, PERMISSIONS[0], "Acceso " +
                                    "a cámara necesario",
                            MY_PERMISSIONS_REQUEST_CAMERA);
                }
            }
        });

        ib_subir_foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(view.getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {

                    Intent pickImage = new Intent(Intent.ACTION_PICK);
                    pickImage.setType("image/*");
                    startActivityForResult(pickImage, IMAGE_PICKER_REQUEST);

                }else{
                    requestPermission(ActivityFormalizarAdopcion.this, PERMISSIONS[1], "Leer " +
                                    "almacenamiento es necesario",
                            MY_PERMISSIONS_READ_EXTERNAL_STORAGE);
                }
            }
        });

        btn_formalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( revisarFormulario() ){
                    btn_formalizar.setEnabled(false);
                    cambiarFormalizacionSolicitud();
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

    @Override
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
                            imageViewFoto.setImageBitmap(imageBitmap);
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
                            imageViewFoto.setImageBitmap(selectedImage);
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                return;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        switch (requestCode)
        {
            case MY_PERMISSIONS_REQUEST_CAMERA:
            {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takePicture();
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

    private void takePicture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private boolean revisarFormulario() {
        boolean b = true;
        if(et_descripcion.getText().toString().isEmpty()){
            et_descripcion.setError("Campo obligatorio");
            b = false;
        }
        if (imageViewFoto.getDrawable() == null){ //no seleccionó imagen
            b = false;
            Toast toast = Toast.makeText(ActivityFormalizarAdopcion.this, "Se debe seleccionar o " +
                    "tomar una foto", Toast.LENGTH_LONG);
            toast.show();
        }
        if(b){
            return true;
        }else{
            return false;
        }
    }

    private void registrarAdopcion(){
        //buscar un nuevo id en FireStore

        Date dateObj = Calendar.getInstance().getTime();

        Adopcion nuevaAdopcion = new Adopcion();
        nuevaAdopcion.setDescripcion( et_descripcion.getText().toString() );
        nuevaAdopcion.setFecha(dateObj);
        nuevaAdopcion.setIdAnimal(solicitud.getIdAnimal());
        nuevaAdopcion.setIdInstitucion(solicitud.getIdInstitucion());
        nuevaAdopcion.setIdSolicitud(solicitud.getId());
        nuevaAdopcion.setIdPersona(solicitud.getIdPersona());
        nuevaAdopcion.setNombreAnimal(solicitud.getNombreAnimal());
        nuevaAdopcion.setNombreInstitucion(solicitud.getNombreInstitucion());
        nuevaAdopcion.setNombrePersona(solicitud.getNombrePersona());

        DocumentReference referencia = db.collection("adopciones").document();

        nuevaAdopcion.setFotoUrl( "adopciones/"+referencia.getId()+"/photo_1.jpg" );
        nuevaAdopcion.setId( referencia.getId() );

        final String direccionFoto = nuevaAdopcion.getFotoUrl();

        referencia
                .set(nuevaAdopcion)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                        subirFoto(direccionFoto,
                                ((BitmapDrawable)imageViewFoto.getDrawable()).getBitmap());

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                        Toast.makeText(ActivityFormalizarAdopcion.this, "Falló el registro de adopción",
                                Toast.LENGTH_SHORT).show();
                        btn_formalizar.setEnabled(true);
                    }
                });
    }

    private void subirFoto(String ruta, Bitmap photo){
        db.setFirestoreSettings(settings);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = storageReference.child(ruta).putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.i("ERROR", "No se pudo subir la foto");
                Toast.makeText(ActivityFormalizarAdopcion.this, "No se pudo subir la foto",
                        Toast.LENGTH_SHORT).show();
                btn_formalizar.setEnabled(true);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.i(TAG, "Foto subida");
                Toast.makeText(ActivityFormalizarAdopcion.this, "Adopción registrada con éxito",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void cambiarFormalizacionSolicitud(){

        DocumentReference referencia = db.collection("solicitudes").document(solicitud.getId());

        referencia
                .update("formalizada", true)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                        Toast.makeText(ActivityFormalizarAdopcion.this, "Solicitud actualizada " +
                                "con éxito", Toast.LENGTH_SHORT).show();
                        registrarAdopcion();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                        Toast.makeText(ActivityFormalizarAdopcion.this, "Ocurrió un problema" +
                                "intentando rechazar la solicitud. Intentalo de nuevo", Toast.LENGTH_SHORT).show();
                        btn_formalizar.setEnabled(true);
                    }
                });
    }
}
