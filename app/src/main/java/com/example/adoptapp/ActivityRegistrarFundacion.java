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
import android.location.Address;
import android.net.Uri;
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

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class ActivityRegistrarFundacion extends AppCompatActivity {

    private EditText et_nombre;
    private EditText et_nombreEncargado;
    private EditText et_email;
    private EditText et_contrasena;
    private EditText et_confirmarContrasena;
    private EditText et_telefono;
    private EditText et_municipio;
    private EditText et_direccion;
    private EditText et_descripcion;
    private Button button_registrarse;
    private ImageButton imageButton_galeria;
    private ImageButton imageButton_camara;
    private ImageView imageView_foto;

    //------------------------------------------------------------------

    private FirebaseStorage storage;
    private StorageReference storageReference;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private FirebaseAuth mAuth;

    //--------------------------PERMISOS------------------------------------------------
    private static final String TAG = "ActivityRegistro";
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 0;
    private static final int MY_PERMISSIONS_READ_EXTERNAL_STORAGE = 1;
    private static final int IMAGE_PICKER_REQUEST = 2;
    private static final int REQUEST_IMAGE_CAPTURE = 3;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 4;
    private static final int REQUEST_CHECK_SETTINGS = 5;
    public final static int ADDRESS_PICKER = 6;

    //----------------------LOCATION--------------------------------------

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;

    private static String[] PERMISSIONS = {
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.ACCESS_FINE_LOCATION

    };

    private Fundacion fundacion;
    private String idUser;

    private GeoPoint ubicacionFundacion;
    private Bitmap selectedImage;
    private Address address;

    private FirebaseFirestoreSettings settings;

    private boolean IngresoActividadMapas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_fundacion);

        IngresoActividadMapas = false;

         et_nombre = findViewById(R.id.editTextNombre);
         et_nombreEncargado = findViewById(R.id.editTextNombreEncargado);
         et_email = findViewById(R.id.editTextEmail);
         et_contrasena = findViewById(R.id.editTextContrasena);
         et_confirmarContrasena = findViewById(R.id.editTextConfirmarContrasena);
         et_telefono = findViewById(R.id.editTextTelefono);
         et_municipio = findViewById(R.id.editTextCiudad);
         et_direccion = findViewById(R.id.editTextDireccion);
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

        et_direccion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(ActivityRegistrarFundacion.this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {

                    Intent i = new Intent(ActivityRegistrarFundacion.this, MapsActivity.class);
                    startActivityForResult(i, Permisos.ADDRESS_PICKER);

                }else{
                    requestPermission(ActivityRegistrarFundacion.this, PERMISSIONS[2], "Acceso a GPS necesario",
                            MY_PERMISSIONS_REQUEST_LOCATION);
                }

            }
        });

        button_registrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fundacion = registrarFundacion();
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

    private Fundacion registrarFundacion(){
        boolean retornar = true;

        String nombre = et_nombre.getText().toString();
        String nombreEncargado = et_nombreEncargado.getText().toString();
        String email = et_email.getText().toString();
        String contrasena = et_contrasena.getText().toString();
        String confirmar_contrasena = et_confirmarContrasena.getText().toString();
        String telefono = et_telefono.getText().toString();
        String municipio = et_municipio.getText().toString();
        String direccion = et_direccion.getText().toString();
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
            if(contrasena.length() < 6){
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

        if(retornar == false){
            return null;
        }

        return new Fundacion(nombre,nombreEncargado,email,contrasena, Integer.parseInt(telefono),null,null,0,0,descripcion);
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
                return;

            case Permisos.ADDRESS_PICKER:
                if (resultCode == RESULT_OK) {

                    IngresoActividadMapas = true;

                    Bundle extras = data.getExtras();
                    address = (Address) extras.get("address");
                    et_direccion.setText(extras.get("direccion").toString());

                    ubicacionFundacion = new GeoPoint(address.getLatitude(), address.getLongitude());

                    Log.i(TAG, String.valueOf(address.getLatitude()));
                    Log.i(TAG, String.valueOf(address.getLongitude()));
                }
                return;
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

            case MY_PERMISSIONS_REQUEST_LOCATION:
            {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, continue with task related to permission
                    //revisarActivacionGPS();
                    Intent i = new Intent(ActivityRegistrarFundacion.this, MapsActivity.class);
                    startActivityForResult(i, Permisos.ADDRESS_PICKER);
                }else{
                    //button_register.setEnabled(false);
                    Toast.makeText(this,
                            "Sin acceso a localización, permiso denegado!",
                            Toast.LENGTH_LONG).show();
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
}
