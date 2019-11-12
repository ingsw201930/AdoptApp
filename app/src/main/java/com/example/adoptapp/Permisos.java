package com.example.adoptapp;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class Permisos {
    public final static int MY_PERMISSIONS_REQUEST_READ_STORAGE = 1;
    public final static int MY_PERMISSIONS_REQUEST_CAMERA = 2;
    public final static int MY_PERMISSIONS_REQUEST_LOCATION = 3;
    public final static int REQUEST_CHECK_SETTINGS = 4;
    public final static int REQUEST_IMAGE_CAPTURE = 5;
    public final static int IMAGE_PICKER_REQUEST = 6;
    public final static int RAZAS_PICKER = 7;
    public final static int ADDRESS_PICKER = 8;

    public static void requestPermission(Activity context, String permiso, String justificacion, int idCode) {
        if (ContextCompat.checkSelfPermission(context, permiso) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(context, permiso)) {
                Toast.makeText(context, justificacion, Toast.LENGTH_LONG).show();
            }
            ActivityCompat.requestPermissions(context, new String[]{permiso}, idCode);
        }
    }
}
