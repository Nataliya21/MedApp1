package com.example.medapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public View v;
    Button refresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        refresh = (Button) findViewById(R.id.Ref);
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
                    ArrayList<PollData> polls = PollInitializer.GetPollsData(baseUrl);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }

    public void Start(View view) {
        ///this.view = view;
    }
}
