package com.example.adoptapp.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.adoptapp.R;
import com.example.adoptapp.utils.FirebaseUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.GeoPoint;

public class ActivityDetalleReporte extends AppCompatActivity implements OnMapReadyCallback {

    private TextView textViewNombre;
    private TextView textViewDescripcion;
    private ImageView imageViewFotoPrincipal;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private Geocoder mGeocoder;
    private Long latitud, longitud;
    private LatLng latLng1, latLng2;

    private SupportMapFragment mapFragment;

    private GeoPoint ubicacionCliente;

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 4;
    private static final int REQUEST_CHECK_SETTINGS = 5;

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_reporte);

        textViewNombre = findViewById(R.id.tv_perfil_reporte);
        textViewDescripcion = findViewById(R.id.tv_detalles_reporte);
        imageViewFotoPrincipal = findViewById(R.id.iv_detalle_reporte);
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        mGeocoder = new Geocoder( getBaseContext());

        ubicacionCliente = new GeoPoint(0,0);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationRequest = createLocationRequest();

        Intent intent = getIntent();
        String nombre = intent.getStringExtra("Nombre");
        final String fotoPrincipal = intent.getStringExtra("Foto_principal");
        String descripcion = intent.getStringExtra("Descripcion");
        Double latitud = intent.getDoubleExtra("Latitud",0.0);
        Double longitud = intent.getDoubleExtra("Longitud",0.0);
        latLng2 = new LatLng(latitud, longitud);

        textViewNombre.setText(nombre);
        textViewDescripcion.setText(descripcion);

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

                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng1, zoom));

                            Marker ubicacionReporte = googleMap.addMarker(new MarkerOptions()
                                    .position(latLng2)
                                    .title("Ubicación del reporte")
                                    .icon(BitmapDescriptorFactory
                                            .defaultMarker(BitmapDescriptorFactory.HUE_RED)));

                            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                            googleMap.getUiSettings().setCompassEnabled(true);
                            googleMap.getUiSettings().setZoomGesturesEnabled(true);
                            googleMap.getUiSettings().setZoomControlsEnabled(true);

                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng2, zoom));

                            stopLocationUpdates();
                        }
                    });
                }
            }
        };



        new Thread(new Runnable() {
            public void run() {
                // a potentially time consuming task
                if (fotoPrincipal != null && !fotoPrincipal.equals("")) {
                    //descargar la imagen
                    FirebaseUtils.descargarFotoImageView(fotoPrincipal, imageViewFotoPrincipal);
                }
            }
        }).start();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng latLng = new LatLng(4.657777, -74.093353);

        float zoom = 13; // Un zoom mayor que 13 hace que el emulador falle, pero un valor deseado para
        // callejero es 17 aprox.
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
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
}
