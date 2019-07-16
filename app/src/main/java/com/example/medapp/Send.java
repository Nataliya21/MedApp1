package com.example.medapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import static com.example.medapp.ActivitiesController.GetUniqNumber;

public class Send extends AppCompatActivity {

    private TextView number;
    private Button share;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);

        number = (TextView) findViewById(R.id.Number);
        share = (Button) findViewById(R.id.Share);

        String uniq = String.valueOf(GetUniqNumber(Send.this));
        Bundle argument = getIntent().getExtras();
        String message = argument.get("message").toString();


        number.setText(message + "\nУникальный номер моего опроса - " + uniq);
//
        final String num = number.getText().toString();

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Share(num);
            }
        });
    }

    private void Share(String number){
        Intent shareIntend = new Intent(Intent.ACTION_SEND);
        shareIntend.setType("text/plain");
        String shareBody = number;
        String shareSub ="";
        shareIntend.putExtra(Intent.EXTRA_SUBJECT, shareSub);
        shareIntend.putExtra(Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(shareIntend, "Поделиться"));
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        BackToMain();
    }

    private void BackToMain(){
        Intent main = new Intent(Send.this, MainActivity.class);
        startActivity(main);
    }
}
