package com.example.adoptapp.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Geocoder;
import android.location.Location;
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
import com.example.adoptapp.model.Institucion;
import com.example.adoptapp.model.ReporteRapido;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ActivityReporteRapido extends AppCompatActivity implements OnMapReadyCallback {

    private ImageView imageViewFoto;
    private EditText editTextReporte;
    private Button buttonReportar;
    private ImageButton ib_upload_photo;
    private ImageButton imageButtonGaleria;
    private SupportMapFragment mapFragment;

    private static String[] PERMISSIONS = {
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.ACCESS_FINE_LOCATION
    };

    private static final String TAG = "ActivityReporteRapido";

    private FirebaseStorage storage;
    private StorageReference storageReference;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private static final int REQUEST_IMAGE_CAPTURE = 3;
    private static final int IMAGE_PICKER_REQUEST = 2;

    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 0;
    private static final int MY_PERMISSIONS_READ_EXTERNAL_STORAGE = 1;

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 4;
    private static final int REQUEST_CHECK_SETTINGS = 5;

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;

    private GeoPoint ubicacionCliente;

    private FirebaseFirestoreSettings settings;

    private LatLng latLng1;

    private Geocoder mGeocoder;

    private ReporteRapido reporte;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng latLng = new LatLng(4.657777, -74.093353);

        float zoom = 13; // Un zoom mayor que 13 hace que el emulador falle, pero un valor deseado para
        // callejero es 17 aprox.
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reporte_rapido);

        imageViewFoto = findViewById(R.id.iv_reporte);
        editTextReporte = findViewById(R.id.et_descripcion_reporte);
        buttonReportar = findViewById(R.id.btn_hacer_reporte);
        ib_upload_photo = findViewById(R.id.ib_upload_photo);
        imageButtonGaleria = findViewById(R.id.ibGaleria);
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        mGeocoder = new Geocoder( getBaseContext());

        settings = new FirebaseFirestoreSettings.Builder()
                .build();
        storageReference = FirebaseStorage.getInstance().getReference();

        ubicacionCliente = new GeoPoint(0,0);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationRequest = createLocationRequest();

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                final Location location = locationResult.getLastLocation();
                Log.i("LOCATION", "Location update in the callback: " + location);
                if (location != null) {
                    ubicacionCliente = new GeoPoint(location.getLatitude(), location.getLongitude());
                    stopLocationUpdates();

                    mapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap googleMap) {

                            latLng1 = new LatLng(location.getLatitude(), location.getLongitude());
                            float zoom = 13;

                            Marker inicio = googleMap.addMarker(new MarkerOptions()
                                    .position(latLng1)
                                    .title("Tu ubicación")
                                    .icon(BitmapDescriptorFactory
                                            .defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

                            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                            googleMap.getUiSettings().setCompassEnabled(true);
                            googleMap.getUiSettings().setZoomGesturesEnabled(true);
                            googleMap.getUiSettings().setZoomControlsEnabled(true);

                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng1, zoom));

                            stopLocationUpdates();
                        }
                    });

                }
            }
        };

        ib_upload_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //CameraUtils.startDialog(Signup_owner.this);
                if (ContextCompat.checkSelfPermission(v.getContext(), Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_GRANTED) {
                    takePicture();
                }else{
                    requestPermission(ActivityReporteRapido.this, PERMISSIONS[0], "Acceso " +
                                    "a cámara necesario",
                            MY_PERMISSIONS_REQUEST_CAMERA);
                }
            }
        });

        imageButtonGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(view.getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {

                    Intent pickImage = new Intent(Intent.ACTION_PICK);
                    pickImage.setType("image/*");
                    startActivityForResult(pickImage, IMAGE_PICKER_REQUEST);

                }else{
                    requestPermission(ActivityReporteRapido.this, PERMISSIONS[1], "Leer " +
                                    "almacenamiento es necesario",
                            MY_PERMISSIONS_READ_EXTERNAL_STORAGE);
                }
            }
        });

        buttonReportar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reporte = obtenerReporte();
                if (reporte != null && ubicacionCliente.getLatitude()!=0 && ubicacionCliente.getLongitude() != 0) {
                    reporte.setUbicacion(ubicacionCliente);
                    buttonReportar.setEnabled(false);
                    registrarReporte();
                }else{
                    if( ubicacionCliente.getLatitude()==0 && ubicacionCliente.getLongitude() == 0 ){
                        Toast toast = Toast.makeText(ActivityReporteRapido.this, "No se ha encontrado " +
                                "la ubicación geográfica", Toast.LENGTH_LONG);
                        toast.show();
                    }
                }
            }
        });

        if (ContextCompat.checkSelfPermission(ActivityReporteRapido.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            revisarActivacionGPS();
        }else{
            requestPermission(ActivityReporteRapido.this, PERMISSIONS[2], "Acceso a GPS necesario",
                    MY_PERMISSIONS_REQUEST_LOCATION);
        }

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

            case REQUEST_CHECK_SETTINGS:
                if (resultCode == RESULT_OK) {
                    startLocationUpdates(); //Se encendió la localización!!!
                } else {
                    //button_register.setEnabled(false);
                    bloquearElementosActividad();
                    Toast.makeText(this,
                            "Sin acceso a localización, hardware deshabilitado!",
                            Toast.LENGTH_LONG).show();
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

            case MY_PERMISSIONS_REQUEST_LOCATION:
            {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, continue with task related to permission
                    revisarActivacionGPS();
                }else{
                    //button_register.setEnabled(false);
                    bloquearElementosActividad();
                    Toast.makeText(this,
                            "Sin acceso a localización, permiso denegado!",
                            Toast.LENGTH_LONG).show();
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

    protected LocationRequest createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000); //tasa de refresco en milisegundos
        mLocationRequest.setFastestInterval(1000); //máxima tasa de refresco
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return mLocationRequest;
    }

    private void stopLocationUpdates(){
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    private void startLocationUpdates() {
        //Verificación de permiso!!
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
        }
    }

    private void revisarActivacionGPS(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            LocationSettingsRequest.Builder builder = new
                    LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
            SettingsClient client = LocationServices.getSettingsClient(this);
            Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

            task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                @Override
                public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                    startLocationUpdates(); //Todas las condiciones para recibir localizaciones
                }
            });

            task.addOnFailureListener(this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    int statusCode = ((ApiException) e).getStatusCode();
                    switch (statusCode) {
                        case CommonStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied, but this can be fixed by showing the user a dialog.
                            try {// Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                ResolvableApiException resolvable = (ResolvableApiException) e;
                                resolvable.startResolutionForResult(ActivityReporteRapido.this,
                                        REQUEST_CHECK_SETTINGS);
                            } catch (IntentSender.SendIntentException sendEx) {
                                // Ignore the error.
                            } break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. No way to fix the settings so we won't show the dialog.
                            break;
                    }
                }
            });

        }else{
            Toast.makeText(this,
                    "Sin acceso a localización, permiso denegado!",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    private void bloquearElementosActividad(){
        editTextReporte.setEnabled(false);
        buttonReportar.setEnabled(false);
        ib_upload_photo .setEnabled(false);
        imageButtonGaleria.setEnabled(false);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ActivityReporteRapido.this, ActivityMenuAdoptante.class);
        startActivity(intent);
        finish();
    }

    private ReporteRapido obtenerReporte() {

        boolean completo = true;
        String descripcion=editTextReporte.getText().toString();
        Date fecha=null;
        String foto=null;

        if (imageViewFoto.getDrawable() == null){ //no seleccionó imagen
            completo = false;
            Toast toast = Toast.makeText(ActivityReporteRapido.this, "Se debe seleccionar o " +
                    "tomar una foto", Toast.LENGTH_LONG);
            toast.show();
        }

        if(descripcion.isEmpty()){
            editTextReporte.setError(getString(R.string.campo_obligatorio));
            completo = false;
        }

        if(!completo) {
            return null;
        }else{
            Date dateObj = Calendar.getInstance().getTime();
            return new ReporteRapido(descripcion, foto, dateObj, currentUser.getUid(),
                    currentUser.getDisplayName());
        }

    }

    private void registrarReporte(){

        //buscar un nuevo id en FireStore
        DocumentReference referencia = db.collection("reportes").document();

        reporte.setDireccionFoto( "reportes/"+referencia.getId()+"/photo_1.jpg" );
        referencia
                .set(reporte)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                        subirFoto(reporte.getDireccionFoto(),
                                ((BitmapDrawable)imageViewFoto.getDrawable()).getBitmap());

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                        Toast.makeText(ActivityReporteRapido.this, "Falló el registro de reporte",
                                Toast.LENGTH_SHORT).show();
                        buttonReportar.setEnabled(true);
                    }
                });
    }

    public void subirFoto(String ruta, Bitmap photo){
        db.setFirestoreSettings(settings);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = storageReference.child(ruta).putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.i("ERROR", "No se pudo subir la foto");
                Toast.makeText(ActivityReporteRapido.this, "No se pudo subir la foto",
                        Toast.LENGTH_SHORT).show();
                buttonReportar.setEnabled(true);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.i(TAG, "Foto subida");
                Toast.makeText(ActivityReporteRapido.this, "Solicitud registrado con éxito",
                        Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ActivityReporteRapido.this, ActivityMenuAdoptante.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
