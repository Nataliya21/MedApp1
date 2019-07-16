package com.example.medapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import static com.example.medapp.ActivitiesController.Init;

public class MainActivity extends AppCompatActivity {

    private Button start;
    private ScrollView sv;
    private RadioGroup rg;
    String baseUrl = "http://andrevvantonovv-001-site1.etempurl.com";

    private ArrayList<PollData> polls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        start = findViewById(R.id.Start);
        sv = findViewById((R.id.sv));
        rg = findViewById(R.id.rg);

        sv.refreshDrawableState();
        rg.refreshDrawableState();
        Refresh();
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
            int i =1;

            for(PollData poll: polls){
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
        int d = -1;
        d = rg.getCheckedRadioButtonId();
        if(d==-1)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Внимание!")
                    .setMessage("Вы не выбрали ни одного теста!")
                    .setCancelable(false).
                    setNegativeButton("Ок",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                    onPause();
                                }
                            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            return;

        }
        RadioButton ch =  findViewById(d);
        id = ch.getTag().toString();

        try {
            Init(id, baseUrl,this);
        } catch (Exception e) {
            e.printStackTrace();
        }



        Intent intent = new Intent(this, var.class);
        startActivity(intent);
    }
 }
