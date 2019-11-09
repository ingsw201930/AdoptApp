package com.example.adoptapp.utils;

import android.graphics.BitmapFactory;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FirebaseUtils {

    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
            .build();
    private static StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();

    public static void descargarFotoImageView(String ruta, final ImageView iv)
    {
        db.setFirestoreSettings(settings);
        StorageReference photoRef = mStorageRef.child(ruta);
        final long ONE_MEGABYTE = 1024 * 1024 * 5*5;
        photoRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>()
        {
            @Override
            public void onSuccess(byte[] bytes) {
                iv.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
    }
}
