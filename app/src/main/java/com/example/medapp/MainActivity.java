package com.example.medapp;

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

import com.example.medapp.API.Models.PollData;
import com.example.medapp.API.PollInitializer;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Button refresh;
    private ScrollView sv;
    private RadioGroup rg;

    private ArrayList<PollData> polls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        refresh = findViewById(R.id.Ref);
        sv = findViewById((R.id.sv));
        rg = findViewById(R.id.rg);

        refresh.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Refresh();
            }
        });

    }

    private void Refresh() {

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try  {
                    String baseUrl = "http://andrevvantonovv-001-site1.etempurl.com";
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
                RadioButton c = new RadioButton(this);
                c.setText(poll.name);
                c.setTag(poll.id);
                rg.addView(c);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
 }
