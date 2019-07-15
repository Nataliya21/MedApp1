package com.example.medapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        BackToMain();
    }

    private void BackToMain(){

        AlertDialog.Builder builder = new AlertDialog.Builder(End.this);
        builder.setTitle("Вернуться к начальному экрану???")
                .setMessage("Усли вы продолжите, то весь прогресс будет утерян безвозвратно. Хотите продолжить?");
        builder.setCancelable(false);

        builder.setPositiveButton("Продолжить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent main = new Intent(End.this, MainActivity.class);
                startActivity(main);
                //метод обнуления индексов
            }
        });
        builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                onPause();
            }
        });
        AlertDialog main = builder.create();
        main.show();

    }
}
