package com.example.medapp;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;


import com.example.medapp.API.Models.PollReport;
import com.example.medapp.API.Models.QuestionAnswer;

import java.util.ArrayList;

import static com.example.medapp.API.PollInitializer.SubmitResponse;
import static com.example.medapp.ActivitiesController.GetPollId;
import static com.example.medapp.ActivitiesController.GetWrittenAnswers;

public class End extends AppCompatActivity {

    private Button doIt;
    private Button doNot;
    private RadioGroup rg;
    private Button dateB;
    private TextView dateBirth;
    private ProgressBar spinner;

    private String baseUrl = "http://andrevvantonovv-001-site1.etempurl.com";

    int BirthDay = 1;
    int BirthMonth = 1;
    int BirthYear = 1990;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end);
        doNot = findViewById(R.id.EndB);
        doIt = findViewById(R.id.Send);
        rg = findViewById(R.id.radioGroup);
        dateB = findViewById(R.id.date);
        dateBirth = findViewById(R.id.BirthDay);
        spinner = findViewById(R.id.progressBar1);

        spinner.setVisibility(View.GONE);

        RadioButton m = new RadioButton(rg.getContext());
        m.setText("Мужской");
        m.setTag("m");
        m.setId(R.id.radio);
        m.setTextSize(24);
        rg.addView(m);
        RadioButton f = new RadioButton(rg.getContext());
        f.setText("Женский");
        f.setTag("f");
        f.setId(R.id.checkbox);
        f.setTextSize(24);
        rg.addView(f);

        doNot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BackToMain();
            }
        });
        doIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SubmitResponseTask().execute();
            }
        });
        dateB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePicker();
            }
        });

    }

    class SubmitResponseTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            spinner.setVisibility(View.VISIBLE);

            doNot.setVisibility(View.GONE);
            doIt.setVisibility(View.GONE);
            rg.setVisibility(View.GONE);
            dateB.setVisibility(View.GONE);
            dateBirth.setVisibility(View.GONE);
            doNot.setVisibility(View.GONE);

            findViewById(R.id.head).setVisibility(View.GONE);
            findViewById(R.id.gen).setVisibility(View.GONE);
            findViewById(R.id.msg).setVisibility(View.GONE);

            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            SendToServer();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }

    private void SendToServer(){

        int genderId = -1;
        genderId = rg.getCheckedRadioButtonId();

        if(genderId == -1)
        {
            GenderAlert();
            return;
        }

        ArrayList<QuestionAnswer> answers = GetWrittenAnswers(this);

        PollReport report;

        try {
            report = SubmitResponse(
                baseUrl,
                GetPollId(this),
                answers.toArray(new QuestionAnswer[answers.size()]),
                findViewById(genderId).getTag().toString(),
                BirthYear, BirthMonth, BirthDay
            );

            Intent send = new Intent (End.this, Send.class);

            send.putExtra("message", report.message);
            send.putExtra("header", report.header);
            send.putExtra("unique", report.uniqueNumber);

            startActivity(send);

        } catch(Exception e) {
            e.printStackTrace();
            AlertDialog.Builder builder = new AlertDialog.Builder(End.this);
            builder.setTitle("Ошибка!")
                    .setMessage("Не удалось установить соединение с сервером!")
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
            return;
        }
    }

    private void DatePicker() {

       final DatePickerDialog date = new DatePickerDialog(End.this, new DatePickerDialog.OnDateSetListener() {
           @Override
           public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
               BirthMonth = month + 1;
               BirthYear = year;
               BirthDay = dayOfMonth;

               dateBirth.setText(dayOfMonth + "/" + BirthMonth + "/" + year);
           }
       }, BirthYear, BirthMonth - 1, BirthDay);
       date.getWindow();

       date.setOnShowListener(new DialogInterface.OnShowListener() {
           @Override
           public void onShow(DialogInterface dialog) {
               date.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.DKGRAY);
               date.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(Color.DKGRAY);
           }
       });
       date.show();
   }

    @Override
    public void onBackPressed() {
        BackToMain();
    }

    private void GenderAlert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(End.this);
        builder.setTitle("Внимание!")
                .setMessage("Вы не выбрали пол!")
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

    private void BackToMain(){

        AlertDialog.Builder builder = new AlertDialog.Builder(End.this);
        builder.setTitle("Вернуться к начальному экрану?")
                .setMessage("Если вы продолжите, то весь прогресс будет утерян безвозвратно. Хотите продолжить?");
        builder.setCancelable(false);

        builder.setPositiveButton("Продолжить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                triggerRebirth(End.this);
            }
        });
        builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();
                onPause();
            }
        });
        final AlertDialog main = builder.create();
        main.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                main.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.DKGRAY);
                main.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.DKGRAY);
            }
        });
        main.show();

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
