package com.example.adoptapp.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.adoptapp.R;
import com.example.adoptapp.model.Evento;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ActivityRegistrarEvento extends AppCompatActivity implements OnMapReadyCallback {

    private static String[] PERMISSIONS = {
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
    };

    private static final String TAG = "Registrar evento";

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private StorageReference storageReference;
    private FirebaseFirestoreSettings settings;

    private static final int REQUEST_IMAGE_CAPTURE = 3;
    private static final int IMAGE_PICKER_REQUEST = 2;

    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 0;
    private static final int MY_PERMISSIONS_READ_EXTERNAL_STORAGE = 1;

    private static final int REQUEST_CHECK_SETTINGS = 5;

    private Geocoder mGeocoder;

    private ImageView imageViewFoto;
    private Button btn_registrarEvento;
    private ImageButton imageButtonTomarFoto;
    private ImageButton imageButtonGaleria;
    private EditText et_titulo;
    private EditText et_fecha;
    private EditText et_direccion;
    private EditText et_descripcion;
    private SupportMapFragment mapFragment;
    private EditText et_hora_inicio;
    private EditText et_hora_fin;

    private Calendar calendar;
    private DatePickerDialog.OnDateSetListener date;
    private TimePickerDialog.OnTimeSetListener horaInicio, horaFin;

    // Limits for the geocoder search (Colombia)
    public static final double lowerLeftLatitude = 1.396967;
    public static final double lowerLeftLongitude = 78.903968;
    public static final double upperRightLatitude = 11.983639;
    public static final double upperRigthLongitude = 71.869905;

    private Address addressResult;
    private GoogleMap mMap;
    private GeoPoint ubicacionEvento;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng latLng = new LatLng(4.657777, -74.093353);

        float zoom = 13; // Un zoom mayor que 13 hace que el emulador falle, pero un valor deseado para
        // callejero es 17 aprox.
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_evento);

        imageViewFoto = findViewById(R.id.iv_evento_registro);
        btn_registrarEvento = findViewById(R.id.btn_registrar_evento);
        imageButtonTomarFoto = findViewById(R.id.ib_tomar_evento);
        imageButtonGaleria = findViewById(R.id.ib_elegir_galeria_evento);
        et_titulo = findViewById(R.id.et_titulo_evento_registro);
        et_fecha = findViewById(R.id.et_fecha_registro_evento);
        et_direccion = findViewById(R.id.et_direccion_evento_registro);
        et_descripcion = findViewById(R.id.et_descripcion_evento_registro);
        et_hora_inicio = findViewById(R.id.et_hora_inicio_registro);
        et_hora_fin = findViewById(R.id.et_hora_fin_registro);
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_evento_registro);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        ubicacionEvento = null;

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        settings = new FirebaseFirestoreSettings.Builder()
                .build();
        storageReference = FirebaseStorage.getInstance().getReference();

        mGeocoder = new Geocoder(getBaseContext());

        calendar = Calendar.getInstance();
        date = new DatePickerDialog.OnDateSetListener(){
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDate();
            }
        };

        horaInicio = new TimePickerDialog.OnTimeSetListener(){
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                String format = "HH:mm";
                SimpleDateFormat sdf = new SimpleDateFormat(format);
                et_hora_inicio.setText(sdf.format(calendar.getTime()));
            }
        };

        horaFin = new TimePickerDialog.OnTimeSetListener(){
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                String format = "HH:mm";
                SimpleDateFormat sdf = new SimpleDateFormat(format);
                et_hora_fin.setText(sdf.format(calendar.getTime()));
            }
        };

        et_fecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(ActivityRegistrarEvento.this, date,
                        calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        et_hora_inicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                new TimePickerDialog(view.getContext(),horaInicio, hour, minute,
                        DateFormat.is24HourFormat(view.getContext())).show();
            }
        });

        et_hora_fin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                new TimePickerDialog(view.getContext(),horaFin, hour, minute,
                        DateFormat.is24HourFormat(view.getContext())).show();
            }
        });

        imageButtonTomarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //CameraUtils.startDialog(Signup_owner.this);
                if (ContextCompat.checkSelfPermission(v.getContext(), Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_GRANTED) {
                    takePicture();
                }else{
                    requestPermission(ActivityRegistrarEvento.this, PERMISSIONS[0], "Acceso " +
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
                    requestPermission(ActivityRegistrarEvento.this, PERMISSIONS[1], "Leer " +
                                    "almacenamiento es necesario",
                            MY_PERMISSIONS_READ_EXTERNAL_STORAGE);
                }
            }
        });

        et_direccion.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE){
                    String addressString = et_direccion.getText().toString();
                    if (!addressString.isEmpty()) {
                        try {
                            List<Address> addresses = mGeocoder.getFromLocationName(addressString, 2, lowerLeftLatitude, lowerLeftLongitude, upperRightLatitude, upperRigthLongitude);
                            if (addresses != null && !addresses.isEmpty()) {
                                addressResult = addresses.get(0);
                                LatLng position = new LatLng(addressResult.getLatitude(), addressResult.getLongitude());
                                if (mMap != null) {
                                    mMap.clear();
                                    Location loc = new Location("");
                                    loc.setLatitude(addressResult.getLatitude());
                                    loc.setLongitude(addressResult.getLongitude());
                                    ubicacionEvento = new GeoPoint( addressResult.getLatitude(), addressResult.getLongitude() );
                                    MarkerOptions myMarkerOptions = new MarkerOptions();
                                    myMarkerOptions.position(position);
                                    myMarkerOptions.title("Dirección encontrada");
                                    myMarkerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                                    mMap.addMarker(myMarkerOptions);
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15));
                                }
                            } else {
                                Toast.makeText(ActivityRegistrarEvento.this, "Dirección no encontrada", Toast.LENGTH_SHORT).show();
                                addressResult = null;
                                ubicacionEvento = null;
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(ActivityRegistrarEvento.this, "La dirección está vacía", Toast.LENGTH_SHORT).show();
                        addressResult = null;
                        ubicacionEvento = null;
                    }
                }
                return false;
            }
        });

        btn_registrarEvento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( revisarFormulario() ){
                    btn_registrarEvento.setEnabled(false);
                    registrarEvento();
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ActivityRegistrarEvento.this, ActivityMenuKeeper.class);
        startActivity(intent);
        finish();
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
        if( et_titulo.getText().toString().isEmpty() ){
            et_titulo.setError("Campo obligatorio");
            b = false;
        }else{
            et_titulo.setError(null);
        }
        if (imageViewFoto.getDrawable() == null){ //no seleccionó imagen
            b = false;
            Toast toast = Toast.makeText(ActivityRegistrarEvento.this, "Se debe seleccionar o " +
                    "tomar una foto", Toast.LENGTH_LONG);
            toast.show();
        }
        if( et_direccion.getText().toString().isEmpty() ){
            et_direccion.setError("Campo obligatorio");
            b = false;
        }else{
            et_direccion.setError(null);
        }
        if( et_fecha.getText().toString().isEmpty() ){
            et_fecha.setError("Campo obligatorio");
            b = false;
        }else{
            if(isDateValid(et_fecha.getText().toString())){
                et_fecha.setError(null);
            }else{
                et_fecha.setError("Fecha/formato incorrecto");
                b = false;
            }
        }
        if( et_descripcion.getText().toString().isEmpty() ){
            et_descripcion.setError("Campo obligatorio");
            b = false;
        }else{
            et_descripcion.setError(null);
        }
        if( et_hora_inicio.getText().toString().isEmpty() ){
            et_hora_inicio.setError("Campo obligatorio");
            b = false;
        }else{
            if(isTimeValid(et_hora_inicio.getText().toString())){
                et_hora_inicio.setError(null);
            }else{
                et_hora_inicio.setError("Hora/formato incorrecto");
                b = false;
            }
        }
        if( et_hora_fin.getText().toString().isEmpty() ){
            et_hora_fin.setError("Campo obligatorio");
            b = false;
        }else{
            if(isTimeValid(et_hora_fin.getText().toString())){
                et_hora_fin.setError(null);
            }else{
                et_hora_fin.setError("Hora/formato incorrecto");
                b = false;
            }
        }
        if( b == true){
            Date timeInicio = null, timeFin = null;
            try {
                timeInicio = new SimpleDateFormat("HH:mm").parse(et_hora_inicio.getText().toString());
                timeFin = new SimpleDateFormat("HH:mm").parse(et_hora_fin.getText().toString());
            } catch (ParseException ex) {
                ex.printStackTrace();
            }
            if(timeInicio != null && timeFin!= null) {
                if (timeInicio.after(timeFin)) {
                    Toast.makeText(ActivityRegistrarEvento.this, "Error: la hora " +
                            "de inicio no puede ser mayor a la hora de finalización", Toast.LENGTH_SHORT).show();
                    b = false;
                }
            }
        }
        if(ubicacionEvento == null){
            Toast.makeText(ActivityRegistrarEvento.this, "No se ha establecido " +
                    "una dirección geográfica", Toast.LENGTH_SHORT).show();
            b = false;
        }
        return b;
    }

    private boolean isDateValid(String fecha){
        Date date = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            date = sdf.parse(fecha);
            if (date != null && !fecha.equals(sdf.format(date))) {
                date = null;
            }
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        if (date == null) {
            return false;
        } else {
            return true;
        }
    }

    private boolean isTimeValid(final String time){

        final String TIME24HOURS_PATTERN =
                "([01]?[0-9]|2[0-3]):[0-5][0-9]";
        Pattern pattern;
        Matcher matcher;
        pattern = Pattern.compile(TIME24HOURS_PATTERN);

        matcher = pattern.matcher(time);
        return matcher.matches();

    }

    private void registrarEvento() {

        Evento evento = new Evento();
        evento.setDescripcion( et_descripcion.getText().toString() );
        evento.setDireccion( et_direccion.getText().toString() );
        evento.setTitulo( et_titulo.getText().toString() );
        evento.setHoraInicio( et_hora_inicio.getText().toString() );
        evento.setHoraFin( et_hora_fin.getText().toString() );

        Date fechaNacimiento = null;
        try {
            fechaNacimiento = new SimpleDateFormat("MM/dd/yyyy").parse( et_fecha.getText().toString() );
        } catch (ParseException e) {
            e.printStackTrace();
        }

        evento.setFecha(fechaNacimiento);
        evento.setIdInstitucion( currentUser.getUid() );
        evento.setNombreInstitucion( currentUser.getDisplayName() );
        evento.setUbicacion( ubicacionEvento );

        DocumentReference referencia = db.collection("eventos").document();

        evento.setId(referencia.getId());
        evento.setFotoUrl( "eventos/"+referencia.getId()+"/photo_1.jpg" );

        final String direccionFoto = evento.getFotoUrl();

        referencia
                .set(evento)
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
                        Toast.makeText(ActivityRegistrarEvento.this, "Falló el registro de adopción",
                                Toast.LENGTH_SHORT).show();
                        btn_registrarEvento.setEnabled(true);
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
                Toast.makeText(ActivityRegistrarEvento.this, "No se pudo subir la foto",
                        Toast.LENGTH_SHORT).show();
                btn_registrarEvento.setEnabled(true);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.i(TAG, "Foto subida");
                Toast.makeText(ActivityRegistrarEvento.this, "Evento registrado con éxito",
                        Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ActivityRegistrarEvento.this, ActivityMenuKeeper.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void updateDate() {
        String format = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        et_fecha.setText(sdf.format(calendar.getTime()));
    }
}
