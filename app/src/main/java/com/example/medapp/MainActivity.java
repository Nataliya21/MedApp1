package com.example.medapp;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;

import com.example.medapp.API.Models.PollData;
import com.example.medapp.API.PollInitializer;

import java.util.ArrayList;

import static com.example.medapp.ActivitiesController.Init;

public class MainActivity extends AppCompatActivity {

    private ProgressBar spinner;
    private Button start;
    private ScrollView sv;
    private RadioGroup rg;
    String baseUrl = "http://pat2doc.ru/";

    private ArrayList<PollData> polls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        start = findViewById(R.id.Start);
        sv = findViewById((R.id.sv));
        rg = findViewById(R.id.rg);
        spinner = findViewById(R.id.progressBar1);

        sv.refreshDrawableState();
        rg.refreshDrawableState();
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new StartPollTask().execute();
            }
        });

        new FetchPollsTask().execute();
    }

    class FetchPollsTask extends AsyncTask<Void, Void, ArrayList<PollData>> {

        @Override
        protected void onPreExecute() {
            spinner.setVisibility(View.VISIBLE);
            start.setVisibility(View.GONE);
            super.onPreExecute();
        }

        @Override
        protected ArrayList<PollData> doInBackground(Void... params) {

            try{
                return PollInitializer.GetPollsData(baseUrl);
            } catch (Exception e){
                e.printStackTrace();
                cancel(false);
                return  null;
            }
        }

        @Override
        protected void onCancelled(){
            super.onCancelled();

            spinner.setVisibility(View.GONE);
            ConnectionErrorDialog();
        }

        @Override
        protected void onPostExecute(ArrayList<PollData> result) {
            super.onPostExecute(result);
            Draw(result);
            spinner.setVisibility(View.GONE);
            start.setVisibility(View.VISIBLE);
        }
    }

    class StartPollTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            spinner.setVisibility(View.VISIBLE);
            sv.setVisibility(View.GONE);
            start.setVisibility(View.GONE);
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try{
                Start();
                return null;
            } catch (Exception e){
                e.printStackTrace();
                this.cancel(false);
                return null;
            }
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }

        protected void onCancelled(){
            super.onCancelled();

            spinner.setVisibility(View.GONE);
            ConnectionErrorDialog();
        }
    }

    private void Draw(ArrayList<PollData> polls){
        int i =1;

        for(PollData poll: polls){
            RadioButton c = new RadioButton(this);
            c.setText(poll.name);
            c.setTag(poll.id);
            c.setId(i);


            c.setTextSize(30);
            c.setPadding(0, 35, 0, 35);

            rg.addView(c);
            i++;
        }
    }

    private void Start() throws Exception{
        String id = "";
        int d = -1;
        d = rg.getCheckedRadioButtonId();

        if(d == -1) {
            TestNotPickedDialog();
            return;
        }

        RadioButton ch =  findViewById(d);
        id = ch.getTag().toString();

        Init(id, baseUrl,this);


        Intent intent = new Intent(this, var.class);
        startActivity(intent);
    }

    private void ConnectionErrorDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Ошибка")
                .setMessage("Не удалось подключиться к серверу\r\nПопробовать снова?")
                .setCancelable(false).
                setNegativeButton("Повторить попытку",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                triggerRebirth(MainActivity.this);
                            }
                        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.DKGRAY);
            }
        });
        alertDialog.show();
    }

    private void TestNotPickedDialog(){
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
        final AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.DKGRAY);
            }
        });
        alertDialog.show();
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
