package com.akash.mymoney;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.akash.mymoney.Transaction.Transaction;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void  init(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(MainActivity.this, Transaction.class));
                finish();
            }
        }, 1500);
    }

    public void onResume() {
        super.onResume();
        init();
    }

    public void onRestart() {
        super.onRestart();
        init();
    }


}