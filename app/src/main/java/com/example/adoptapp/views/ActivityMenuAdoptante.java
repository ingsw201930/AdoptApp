package com.example.adoptapp.views;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.adoptapp.R;
import com.example.adoptapp.model.Solicitud;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class ActivityMenuAdoptante extends AppCompatActivity {

    private ConstraintLayout constraintLayoutBuscarAnimales;
    private ConstraintLayout constraintLayoutBuscarInstituciones;
    private ConstraintLayout constraintLayoutReporteRapido;
    private ConstraintLayout constraintLayoutVerReportes;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private FirebaseFirestore db;

    private ListenerRegistration listenerLista;

    private String TAG = "Menu adoptante";

    private int numeroNotificacion;
    public static String ID_CANAL = "adoptapp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_adoptante);

        constraintLayoutBuscarAnimales = findViewById(R.id.ConstraintLayoutBuscarAnimales);
        constraintLayoutBuscarInstituciones = findViewById(R.id.ConstraintLayoutBuscarInstituciones);
        constraintLayoutReporteRapido = findViewById(R.id.ConstraintLayoutReporteRapido);
        constraintLayoutVerReportes = findViewById(R.id.ConstraintLayoutVerReportes);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if(currentUser==null){
            finish();
        }

        db = FirebaseFirestore.getInstance();

        constraintLayoutBuscarAnimales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ActivityBuscarAnimales.class);
                startActivity(intent);
            }
        });

        constraintLayoutBuscarInstituciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ActivityBuscarInstituciones.class);
                startActivity(intent);
            }
        });

        constraintLayoutReporteRapido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ActivityReporteRapido.class);
                startActivity(intent);
            }
        });

        constraintLayoutVerReportes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ActivityBuscarReporte.class);
                startActivity(intent);
            }
        });

        createNotificationChannel();
        numeroNotificacion = 1;
        listenerCambiosLista();
    }

    private void createNotificationChannel() {

        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "channel";
            String description = "Notificacion solicitud adopcion";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            //IMPORTANCE_MAX MUESTRA LA NOTIFICACIÓN ANIMADA
            NotificationChannel channel = new NotificationChannel(ID_CANAL, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void crearNotificacionAdopcion(Solicitud solicitud, double lat, double longi){

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, ID_CANAL);
        mBuilder.setSmallIcon(R.drawable.icono_adoptante);
        mBuilder.setContentTitle("Tu solicitud de adopción fue aceptada");
        mBuilder.setContentText(solicitud.getNombreAnimal()+" está esperándote.\n"+
                "Clic aquí para ver a dónde digirte por "+solicitud.getNombreAnimal());
        mBuilder.setPriority(NotificationCompat.PRIORITY_DEFAULT);

        //Acción asociada a la notificación
        Intent intent = new Intent(this, ActivityRecogerAdoptado.class);
        intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP );

        intent.putExtra("latitud", lat);
        intent.putExtra("longitud", longi);
        Bundle bundle = new Bundle();
        bundle.putSerializable("solicitud",solicitud);
        intent.putExtras(bundle);

        // Create the TaskStackBuilder and add the intent, which inflates the back stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(intent);

        // Get the PendingIntent containing the entire back stack
        PendingIntent pendingIntent =
                stackBuilder.getPendingIntent(numeroNotificacion, PendingIntent.FLAG_UPDATE_CURRENT);

        //PendingIntent pendingIntent = PendingIntent.getActivity(this, numeroNotificacion, intent, 0);
        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setAutoCancel(true); //Remueve la notificación cuando se toca*/

        int notificationId = numeroNotificacion;
        numeroNotificacion = numeroNotificacion+1;
        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(this);
        // notificationId es un entero unico definido para cada notificacion que se lanza
        notificationManager.notify(notificationId, mBuilder.build());

    }

    public void listenerCambiosLista(){

        Query query = db.collection("solicitudes")
                .whereEqualTo("idPersona", currentUser.getUid())
                .whereEqualTo("tipo", "Adopción")
                .whereEqualTo("aceptada", true)
                .whereEqualTo("formalizada", false);

        listenerLista = query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "listen:error", e);
                    return;
                }

                Solicitud solicitud;

                for (DocumentChange dc : snapshots.getDocumentChanges()) {
                    switch (dc.getType()) {
                        case ADDED:
                            Log.d(TAG, "New solicitud: " + dc.getDocument().getData());
                            solicitud = dc.getDocument().toObject(Solicitud.class);
                            hacerBusquedaLocalizacion(solicitud);
                            break;
                    }
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

    private void cerrarSesion(){
        if (currentUser != null) {
            mAuth.signOut();
        }
        listenerLista.remove();
        Intent intent = new Intent(ActivityMenuAdoptante.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        listenerLista.remove();
        finish();
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this,
                "Acción no disponible",
                Toast.LENGTH_LONG).show();
    }

    private void hacerBusquedaLocalizacion(final Solicitud solicitud){

        db.collection("animales").document(solicitud.getIdAnimal())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                GeoPoint ubicacion = document.getGeoPoint("Ubicacion");
                                Log.d("TAG", "Se obtuvo documento de "+document.getString("Nombre"));
                                crearNotificacionAdopcion(solicitud, ubicacion.getLatitude(), ubicacion.getLongitude());
                            }
                        }
                    }
                });

    }

}
