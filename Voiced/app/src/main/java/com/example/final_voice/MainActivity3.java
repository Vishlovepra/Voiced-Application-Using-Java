package com.example.final_voice;

import static java.lang.Thread.sleep;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class MainActivity3 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        Thread thread=new Thread(){
            public void run(){
                try{
                    sleep(3000);

                }
                catch (Exception e){
                    e.printStackTrace();

                }
                finally {
                    Intent i=new Intent(MainActivity3.this,MainActivity.class);
                    startActivity(i);

                }

            }
        };thread.start();

    }
}