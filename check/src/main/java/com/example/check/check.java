package com.example.check;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class check extends AppCompatActivity {

    private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check);
    }

    public void Next(View view) {
        this.view = view;
    }
}
