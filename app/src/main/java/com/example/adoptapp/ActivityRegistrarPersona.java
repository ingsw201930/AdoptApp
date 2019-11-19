package com.example.adoptapp;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.location.Address;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;

import com.example.adoptapp.model.Persona;
import com.example.adoptapp.model.ReporteRapido;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.GeoPoint;

public class ActivityRegistrarPersona extends AppCompatActivity {

    private EditText et_nombre;
    private EditText et_apellido;
    private EditText et_email;
    private EditText et_cedula;
    private EditText et_celular;
    private EditText et_contrasena;
    private EditText et_confirmarContrasena;
    private EditText et_documento;
    private EditText et_municipio;
    private EditText et_direccion;
    private RadioButton rb_hombre;
    private RadioButton rb_mujer;
    private ImageButton imageButton_galeria;
    private ImageButton imageButton_camara;
    private ImageView imageView_foto;
    private Button button_registrarse;

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

    private Persona persona;
    private String idUser;

    private GeoPoint ubicacionFundacion;
    private Bitmap selectedImage;
    private Address address;

    private FirebaseFirestoreSettings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_persona);
    }
}
