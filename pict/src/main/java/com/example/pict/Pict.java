package com.example.pict;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class Pict extends AppCompatActivity {

    private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pict);
    }

    public void Add(View view) {
        this.view = view;
    }

    public void Next(View view) {
        this.view = view;
    }
}
