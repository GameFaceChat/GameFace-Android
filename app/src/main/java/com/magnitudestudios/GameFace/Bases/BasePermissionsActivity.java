package com.magnitudestudios.GameFace.Bases;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import static com.magnitudestudios.GameFace.Constants.ALL_PERMISSIONS;
import static com.magnitudestudios.GameFace.Constants.PERMISSIONS;

public class BasePermissionsActivity extends AppCompatActivity {

    private static final String TAG = "BasePermissionsActivity";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPermissions(ALL_PERMISSIONS);

    }

    public static boolean hasPermissions(Context context, String[] permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    // Function to check and request permission.
    public void checkPermissions(int requestCode)
    {
        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, requestCode);
        }
    }

    public boolean hasAllPermissionsGranted(@NonNull int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Log.d(TAG, "onRequestPermissionsResult: "+ permissions.toString() + grantResults.toString());

        if (requestCode == ALL_PERMISSIONS && grantResults.length > 0) {
            if (hasAllPermissionsGranted(grantResults)) {
                Toast.makeText(this, "All Permissions Granted", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this, "All Permission Not Granted", Toast.LENGTH_SHORT).show();
            }
        }

    }
}
