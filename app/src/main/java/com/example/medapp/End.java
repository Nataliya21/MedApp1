package com.example.medapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class End extends AppCompatActivity {

    Button share;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end);
        share = (Button) findViewById(R.id.Share);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shareIntend = new Intent(Intent.ACTION_SEND);
                shareIntend.setType("text/plain");
                String shareBody = "Уникальный номер вашего обращения ";
                String shareSub = "000000001";
                shareIntend.putExtra(Intent.EXTRA_SUBJECT, shareSub);
                shareIntend.putExtra(Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(shareIntend, "Поделиться"));
            }
        });
    }
}
