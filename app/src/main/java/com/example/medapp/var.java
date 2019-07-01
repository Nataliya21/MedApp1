package com.example.medapp;

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

public class var extends AppCompatActivity {

    private TextView qst;
    private ScrollView sv;
    private LinearLayout ll;
    private RadioGroup rg;
    private Button next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_var);
        qst = (TextView) findViewById(R.id.Qst);
        sv = (ScrollView) findViewById(R.id.sv);
        //показ вопроса
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
