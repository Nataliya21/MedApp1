package com.example.medapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;

import com.example.medapp.API.Models.Poll;
import com.example.medapp.API.Models.PollData;
import com.example.medapp.API.PollInitializer;

import java.util.ArrayList;

import static com.example.medapp.BD.WriteToDbPoll;

public class MainActivity extends AppCompatActivity {

    private Button refresh;
    private Button start;
    private ScrollView sv;
    private RadioGroup rg;
    String baseUrl = "http://andrevvantonovv-001-site1.etempurl.com";

    private ArrayList<PollData> polls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        refresh = findViewById(R.id.Ref);
        start = findViewById(R.id.Start);
        sv = findViewById((R.id.sv));
        rg = findViewById(R.id.rg);

        sv.refreshDrawableState();
        rg.refreshDrawableState();
        refresh.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Refresh();
            }
        });
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Start();
            }
        });
    }

    private void Refresh() {

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try  {

                    polls = PollInitializer.GetPollsData(baseUrl);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();

        try{
            thread.join();

            for(PollData poll: polls){
                int i =1;
                RadioButton c = new RadioButton(this);
                c.setText(poll.name);
                c.setTag(poll.id);
                c.setId(i);
                rg.addView(c);
                i++;
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void Start(){
        //передать в бд вопросы и состояния вопросов
        String id = "";
        int d = rg.getCheckedRadioButtonId();
        RadioButton ch =  findViewById(d);
        id = ch.getTag().toString();

        try {
            WriteToDbPoll(baseUrl, id, this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //узнать тип вопроса


        Intent intent = new Intent(this, var.class);
        startActivity(intent);

    }
 }
