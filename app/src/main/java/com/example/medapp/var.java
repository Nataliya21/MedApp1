package com.example.medapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import static com.example.medapp.BD.Fill;

public class var extends AppCompatActivity {

    private TextView qst;
    private ScrollView sv;
    private TextView sect;
    private Button next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_var);

        qst = (TextView) findViewById(R.id.Qst);
        sect = (TextView) findViewById(R.id.SectId);
        sv = (ScrollView) findViewById(R.id.sv);

        //показ вопроса
        Fill(qst, sect, sv, this);

        //обработка выбора


        next = (Button) findViewById(R.id.Next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Next();
            }
        });

    }

    private void  Next(){
        //переход к другому вопросу

    }

}
