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
        try {
            refresh.setOnClickListener(Refresh(this));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private View.OnClickListener Refresh(MainActivity mainActivity) throws Exception {
        String baseUrl = "http://localhost:49214";

        ArrayList<PollData> polls = PollInitializer.GetPollsData(baseUrl);
        Poll poll = PollInitializer.GetPOll(baseUrl, polls.get(0).id);

        ArrayList<QuestionAnswer> answers = new ArrayList<>();

        return null;
    }

    public void Start(View view) {
        ///this.view = view;
    }
}
