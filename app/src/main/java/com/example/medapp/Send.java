package com.example.medapp;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class Send extends AppCompatActivity {

    private Button share;

    private String uniqueNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);

        Bundle argument = getIntent().getExtras();

        share = findViewById(R.id.Share);

        ( (TextView)findViewById(R.id.header) ).setText(argument.get("header").toString());
        ( (TextView)findViewById(R.id.message) ).setText(argument.get("message").toString());
        ( (TextView)findViewById(R.id.unique) ).setText(argument.get("unique").toString());

        uniqueNumber = argument.get("unique").toString();

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Share(uniqueNumber);
            }
        });
    }

    private void Share(String uniq){
        Intent shareIntend = new Intent(Intent.ACTION_SEND);
        shareIntend.setType("text/plain");
        String shareBody = "Уникальный номер моего отчета - " + uniq;
        String shareSub ="";
        shareIntend.putExtra(Intent.EXTRA_SUBJECT, shareSub);
        shareIntend.putExtra(Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(shareIntend, "Поделиться"));
    }

    @Override
    public void onBackPressed() {
        triggerRebirth(Send.this);
    }

    public static void triggerRebirth(Context context) {
        PackageManager packageManager = context.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(context.getPackageName());
        ComponentName componentName = intent.getComponent();
        Intent mainIntent = Intent.makeRestartActivityTask(componentName);
        context.startActivity(mainIntent);
        Runtime.getRuntime().exit(0);
    }
}
