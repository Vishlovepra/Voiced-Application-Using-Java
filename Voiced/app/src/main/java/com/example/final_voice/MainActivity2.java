package com.example.final_voice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.Manifest;
import android.content.Context;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.Button;

import android.widget.EditText;

import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import java.io.IOException;
import java.io.OutputStreamWriter;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class MainActivity2 extends AppCompatActivity {

    EditText mInputEt;
    Button mSave,recobtn,clear,del;
    String mText;
    private static  String storage="";
    private static final int REQUEST_CODE_SPEECH_INPUT = 1;
    private static final int WRITE_EXTERNAL_STORAGE_CODE=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        mInputEt=findViewById(R.id.inputid);
        mSave = findViewById(R.id.savebtn);
        clear=findViewById(R.id.clr);
        del=findViewById(R.id.del);
        recobtn=findViewById(R.id.Recobtn);//Reco Button
        recobtn.setOnClickListener(this::onClick);

        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mInputEt.setText("");
            }
        });
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FileOutputStream writer = null;
                try {
                    File externalStorageDir = Environment.getExternalStorageDirectory();
                    Intent intent = getIntent();
                    String str2 = intent.getStringExtra("root_name");
                    File myFile = new File(externalStorageDir.getPath() + "/" +"Voiced Files",str2+".txt" );
                    writer = new FileOutputStream(myFile);
                    storage="";
                    Toast.makeText(MainActivity2.this, "Content Of The File Cleared ", Toast.LENGTH_SHORT).show();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                try {
                    writer.write(("").getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        mSave.setOnClickListener(view -> {
            mText = mInputEt.getText().toString().trim();
                //OpenSavedFile();
                if (mText.isEmpty()) {
                    Toast.makeText(MainActivity2.this, "You Have Not Entered Message ,Please Enter", Toast.LENGTH_SHORT).show();
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                            String[] premissons = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                            requestPermissions(premissons, WRITE_EXTERNAL_STORAGE_CODE);

                        } else {
                            saveToTextFile();
                        }
                    } else {
                        saveToTextFile();

                    }
                }

        });
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == WRITE_EXTERNAL_STORAGE_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                saveToTextFile();
            } else {
                Toast.makeText(this, "Storage Permisson Given!", Toast.LENGTH_SHORT).show();
            }
        }
    }



    public void saveToTextFile() {
        File externalStorageDir = Environment.getExternalStorageDirectory();
        Intent intent = getIntent();

        String str2 = intent.getStringExtra("root_name");
        File myFile = new File(externalStorageDir.getPath() + "/" +"Voiced Files",str2+".txt" );
        try {
            FileOutputStream fOut = new FileOutputStream(myFile);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            String res=mInputEt.getText().toString();
            storage+=res;
            myOutWriter.append(storage);
            myOutWriter.close();
            fOut.close();
            Toast.makeText(this, "Saved Contents to File", Toast.LENGTH_SHORT).show();
            Toast.makeText(this, "Saved at"+myFile, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this,  e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> result = data.getStringArrayListExtra(
                        RecognizerIntent.EXTRA_RESULTS);

                mInputEt.setText(
                        storage+" "+ Objects.requireNonNull(result).get(0));
            }
        }
    }
    public void OpenSavedFile(){
        try {
            File externalStorageDir = Environment.getExternalStorageDirectory();
            Intent intent = getIntent();
            String str2 = intent.getStringExtra("root_name");
            File myFile = new File(externalStorageDir.getPath() + "/" + "Voiced Files", str2 + ".txt");
            FileOutputStream i = openFileOutput(str2, Context.MODE_PRIVATE);
            Toast.makeText(this, "Saved"+myFile, Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    private void onClick(View view) {
        Intent intent
                = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text");
        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
        } catch (Exception e) {
            Toast
                    .makeText(MainActivity2.this, " " + e.getMessage(),
                            Toast.LENGTH_SHORT)
                    .show();
        }

    }
}