package com.example.adoptapp.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adoptapp.R;
import com.example.adoptapp.adapters.AdapterInstituciones;
import com.example.adoptapp.adapters.RecyclerTouchListener;
import com.example.adoptapp.model.Institucion;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ActivityBuscarInstituciones extends AppCompatActivity {

    FirebaseFirestore db;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;

    RecyclerView recyclerViewInstituciones;
    ProgressBar progressBarCargarLista;
    TextView textViewCargando;
    ConstraintLayout constraintLayoutSuperior;

    private static final String TAG = "Buscar instituciones";
    ArrayList<Institucion> arrayListInstituciones;

    private AdapterInstituciones mAdapter;

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 0;
    private static final int REQUEST_CHECK_SETTINGS = 1;

    private double latitudActual;
    private double longitudActual;

    String[] PERMISSIONS = {
            android.Manifest.permission.ACCESS_FINE_LOCATION
    };

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;

    final int RADIUS_OF_EARTH_KM = 6371; //en km

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar_instituciones);

        recyclerViewInstituciones = findViewById(R.id.rv_lista_instituciones);
        progressBarCargarLista = findViewById(R.id.pb_lista_instituciones);
        textViewCargando = findViewById(R.id.tv_cargando_lista_instituciones);
        constraintLayoutSuperior = findViewById(R.id.cl_superior);

        ConnectivityManager cm = (ConnectivityManager)ActivityBuscarInstituciones.this.getSystemService
                (CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if(!isConnected) {
            progressBarCargarLista.setVisibility(View.GONE);
            textViewCargando.setText(R.string.AvisoNoConexion);
            Toast.makeText(ActivityBuscarInstituciones.this, "No hay conexión a internet",
                    Toast.LENGTH_SHORT).show();
        }

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitNetwork().build();
        StrictMode.setThreadPolicy(policy);

        db = FirebaseFirestore.getInstance();

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationRequest = createLocationRequest();

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Location location = locationResult.getLastLocation();
                Log.i("LOCATION", "Location update in the callback: " + location);
                if (location != null) {
                    latitudActual = location.getLatitude();
                    longitudActual = location.getLongitude();
                    if (arrayListInstituciones.size() == 0) {
                        leerListaInstituciones();
                    }
                    stopLocationUpdates();
                    Log.i(TAG, latitudActual+" "+longitudActual);
                }
            }
        };

        latitudActual = 200; //inicializar con latitud que no existe
        longitudActual = 200; //inicializar con longitud que no existe

        arrayListInstituciones = new ArrayList<>();

        mAdapter = new AdapterInstituciones(arrayListInstituciones);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerViewInstituciones.setLayoutManager(mLayoutManager);
        recyclerViewInstituciones.setItemAnimator(new DefaultItemAnimator());
        //recyclerViewAnimales.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerViewInstituciones.setAdapter(mAdapter);

        recyclerViewInstituciones.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext()
                , recyclerViewInstituciones, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {

                Institucion institucion = arrayListInstituciones.get(position);

                Intent intent = new Intent(view.getContext(), ActivityPerfilInstitucion.class);

                intent.putExtra("Nombre", institucion.getNombre());
                intent.putExtra("Foto_principal", institucion.getImagenPrincipal());

                String datosInstitucion = "Encargado: " + institucion.getEncargado()+"\n"+
                        "Municipio: "+institucion.getMunicipio()+"\n"+
                        "Email: "+institucion.getEmail()+"\n"+
                        "Teléfono: "+institucion.getTelefono()+"\n"+
                        "A "+institucion.getDistancia()+" km de tu ubicación\n"+
                        "Descripción:\n"+institucion.getDescripcion()+"";
                intent.putExtra("Descripcion", datosInstitucion);

                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        if (ContextCompat.checkSelfPermission(ActivityBuscarInstituciones.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            revisarActivacionGPS();
        }else{
            requestPermission(ActivityBuscarInstituciones.this, PERMISSIONS[0], "Acceso " +
                    "a localización necesario para listar animales", MY_PERMISSIONS_REQUEST_LOCATION);
        }
    }

    private void startLocationUpdates() {
        //Verificación de permiso!!
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
        }
    }

    private void stopLocationUpdates(){
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ActivityBuscarInstituciones.this, ActivityMenuAdoptante.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    public double calcularDistancia(double lat1, double long1, double lat2, double long2) {
        double latDistance = Math.toRadians(lat1 - lat2);
        double lngDistance = Math.toRadians(long1 - long2);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double result = RADIUS_OF_EARTH_KM * c;
        return Math.round(result*100.0)/100.0;
    }

    private void cerrarSesion(){
        if (currentUser == null) {
            mAuth.signOut();
        }
        Intent intent = new Intent(ActivityBuscarInstituciones.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        switch (requestCode) {

            case MY_PERMISSIONS_REQUEST_LOCATION:
            {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, continue with task related to permission
                    revisarActivacionGPS();
                }else{
                    Toast.makeText(this,
                            "Sin acceso a localización, permiso denegado!",
                            Toast.LENGTH_LONG).show();
                    constraintLayoutSuperior.setVisibility(View.GONE);
                    textViewCargando.setText("");
                    progressBarCargarLista.setVisibility(View.GONE);
                    //imageButtonFiltrar.setEnabled(false);
                }
            }
            break;

        }

    }

    private void revisarActivacionGPS() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            LocationSettingsRequest.Builder builder = new
                    LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
            SettingsClient client = LocationServices.getSettingsClient(this);
            Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

            task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                @Override
                public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                    //mFusedLocationClient.getLastLocation(); //Todas las condiciones para recibir localizaciones
                    startLocationUpdates();
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
                                resolvable.startResolutionForResult(ActivityBuscarInstituciones.this,
                                        REQUEST_CHECK_SETTINGS);
                            } catch (IntentSender.SendIntentException sendEx) {
                                // Ignore the error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. No way to fix the settings so we won't show the dialog.
                            break;
                    }
                }
            });

        } else {
            Toast.makeText(this,
                    "Sin acceso a localización, permiso denegado!",
                    Toast.LENGTH_LONG).show();
        }
    }

    protected LocationRequest createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000); //tasa de refresco en milisegundos
        mLocationRequest.setFastestInterval(1000); //máxima tasa de refresco
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return mLocationRequest;
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

        if (requestCode == REQUEST_CHECK_SETTINGS) { //se obtuvo acceso a hardware para localización
            if (resultCode == RESULT_OK) {
                //mFusedLocationClient.getLastLocation();
                startLocationUpdates();
            } else {
                Toast.makeText(this,
                        "Sin acceso a localización, hardware deshabilitado!",
                        Toast.LENGTH_LONG).show();
                constraintLayoutSuperior.setVisibility(View.GONE);
                textViewCargando.setText("");
                progressBarCargarLista.setVisibility(View.GONE);
                //imageButtonFiltrar.setEnabled(false);
            }
        }
    }

    public void mostrarListaInstituciones() {
        if (arrayListInstituciones.size() > 0){
            //mAdapter = new AdapterAnimales(arrayAnimales);
            //customAdapter = new CustomAdapter(this, arrayAnimales);
            //listViewAnimales.setAdapter(customAdapter);
            //progressBarCargarLista.setVisibility(View.GONE);
            mAdapter = new AdapterInstituciones(arrayListInstituciones);
            recyclerViewInstituciones.setAdapter(mAdapter);
            //mAdapter.notifyDataSetChanged();
            textViewCargando.setText("");
            constraintLayoutSuperior.setVisibility(View.GONE);
        }else{
            textViewCargando.setText(R.string.resultadosNoEncontrados);
            Toast.makeText(ActivityBuscarInstituciones.this, "La búsqueda no " +
                    "ha encontrado resultados", Toast.LENGTH_SHORT).show();
        }
        progressBarCargarLista.setVisibility(View.GONE);
        //imageButtonFiltrar.setEnabled(true);
    }

    public void leerListaInstituciones() {

        db.collection("instituciones")
                /*.whereEqualTo("Estado", "Espera")*/
                .orderBy("Nombre", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Institucion institucion = new Institucion();
                                institucion.setId(document.getId());
                                institucion.setNombre( document.getString("Nombre") );
                                institucion.setEmail( document.getString("Email") );
                                institucion.setEncargado( document.getString("Encargado") );
                                institucion.setImagenPrincipal( document.getString("ImagenPrincipal") );
                                institucion.setMunicipio( document.getString("Municipio") );
                                institucion.setTelefono( document.getLong("Telefono"));
                                institucion.setDescripcion( document.getString("Descripcion") );
                                GeoPoint ubicacion = document.getGeoPoint("Ubicacion");
                                institucion.setUbicacion( ubicacion );
                                institucion.setDistancia( calcularDistancia(latitudActual, longitudActual,
                                        ubicacion.getLatitude(),ubicacion.getLongitude()) );
                                arrayListInstituciones.add(institucion);
                            }
                            /*arrayListReportes = new ArrayList<>(arrayListAnimales);*/
                            mostrarListaInstituciones();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_sesion, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.MenuCerrarSesion:
                cerrarSesion();
                return true;
            default:
                return true;
        }
    }
}
