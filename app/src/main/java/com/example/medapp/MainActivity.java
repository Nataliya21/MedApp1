package com.example.medapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.example.medapp.API.Models.PollData;
import com.example.medapp.API.PollInitializer;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ListView list;
    private Button refresh;

    private ArrayAdapter<String> adapter;
    private ArrayList<String> arrayList;
    private ArrayList<PollData> polls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        refresh = findViewById(R.id.Ref);
        list = findViewById(R.id.PollsList);
        arrayList = new ArrayList<String>();

        adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, arrayList);
        refresh.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Refresh();
            }
        });

        list.setAdapter(adapter);
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
                arrayList.add(poll.name);
                adapter.notifyDataSetChanged();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void Start(View view) {
        ///this.view = view;
    }
}
