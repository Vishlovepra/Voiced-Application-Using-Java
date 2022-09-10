package com.example.final_voice;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
        private static final int STORAGE_PERMISSION_CODE = 100;
        private static final String TAG = "PERMISSION_TAG";
        private EditText fileNameEt,open;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fileNameEt = findViewById(R.id.fileNameEt);
        open=findViewById(R.id.opn);
        MaterialButton create = findViewById(R.id.createFolderBtn);
        MaterialButton opn = findViewById(R.id.opnbtn);
        opn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Openfile();

            }
        });

        create.setOnClickListener(view -> {
            if (checkPermission()) {
                Log.d(TAG, "onClick: Permissions already granted...");
                Createfile();

            } else {
                Log.d(TAG, "onClick: Permissions was not granted, request...");
                requestPermission();
            }


        });


    }
    private void Openfile(){
        try{
            Intent intent = new Intent(MainActivity.this, MainActivity4.class);
            int ind=1;
            intent.putExtra("root",ind);
            Toast.makeText(this, "Opening File " , Toast.LENGTH_SHORT).show();
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void Createfile() {
        try {
            String ofile = fileNameEt.getText().toString();
            Intent intent = new Intent(MainActivity.this, MainActivity2.class);
            File root = new File(Environment.getExternalStorageDirectory(), "Voiced Files");
            intent.putExtra("root_name", ofile);
            if (!root.exists()) {
                root.mkdirs();
            }
            File filepath = new File(root, ofile + ".txt");
            FileWriter writer = new FileWriter(filepath);
            writer.flush();
            writer.close();
            Toast.makeText(this, "File Created" + filepath, Toast.LENGTH_SHORT).show();
            startActivity(intent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            //Android is 11(R) or above
            try {
                Log.d(TAG, "requestPermission: try");

                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.fromParts("package", this.getPackageName(), null);
                intent.setData(uri);
                storageActivityResultLauncher.launch(intent);
            } catch (Exception e) {
                Log.e(TAG, "requestPermission: catch", e);
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                storageActivityResultLauncher.launch(intent);
            }
        } else {
            //Android is below 11(R)
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                    STORAGE_PERMISSION_CODE
            );
        }
    }

    private final ActivityResultLauncher<Intent> storageActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Log.d(TAG, "onActivityResult: ");
                //here we will handle the result of our intent
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    //Android is 11(R) or above
                    if (Environment.isExternalStorageManager()) {
                        //Manage External Storage Permission is granted
                        Log.d(TAG, "onActivityResult: Manage External Storage Permission is granted");
                        Createfile();
                    } else {
                        //Manage External Storage Permission is denied
                        Log.d(TAG, "onActivityResult: Manage External Storage Permission is denied");
                        Toast.makeText(MainActivity.this, "Manage External Storage Permission is denied", Toast.LENGTH_SHORT).show();
                    }
                }
                //Android is below 11(R)

            }
    );

    public boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            //Android is 11(R) or above
            return Environment.isExternalStorageManager();
        } else {
            //Android is below 11(R)
            int write = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int read = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

            return write == PackageManager.PERMISSION_GRANTED && read == PackageManager.PERMISSION_GRANTED;
        }
    }

    /*Handle permission request results*/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0) {
                //check each permission if granted or not
                boolean write = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean read = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                if (write && read) {
                    //External Storage permissions granted
                    Log.d(TAG, "onRequestPermissionsResult: External Storage permissions granted");
                  Createfile();
                } else {
                    //External Storage permission denied
                    Log.d(TAG, "onRequestPermissionsResult: External Storage permission denied");
                    Toast.makeText(this, "External Storage permission denied", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}