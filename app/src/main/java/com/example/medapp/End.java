package com.example.medapp;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;


import com.example.medapp.API.Models.PollReport;
import com.example.medapp.API.Models.QuestionAnswer;

import java.util.ArrayList;
import java.util.Calendar;

import static com.example.medapp.API.PollInitializer.SubmitResponse;
import static com.example.medapp.ActivitiesController.GetPollId;
import static com.example.medapp.ActivitiesController.GetWrittenAnswers;
import static com.example.medapp.ActivitiesController.WriteReportToDb;

public class End extends AppCompatActivity {

    private Button doIt;
    private Button doNot;
    private RadioGroup rg;
    private Button dateB;
    private String baseUrl = "http://andrevvantonovv-001-site1.etempurl.com";
    Calendar calendar = Calendar.getInstance();

    int [] dateINT = new int[3];
    int count =0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end);
        doNot = (Button) findViewById(R.id.EndB);
        doIt = (Button) findViewById(R.id.Send);
        rg = (RadioGroup) findViewById(R.id.radioGroup);
        dateB = (Button) findViewById(R.id.date);

        RadioButton m = new RadioButton(rg.getContext());
        m.setText("Мужской");
        m.setTag("m");
        m.setId(R.id.radio);
        rg.addView(m);
        RadioButton f = new RadioButton(rg.getContext());
        f.setText("Женский");
        f.setTag("f");
        f.setId(R.id.checkbox);
        rg.addView(f);

        doNot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Refresh();
            }
        });

        doIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SendToSerever();

            }
        });
        dateB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePicker();

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

    private void Refresh(){

        AlertDialog.Builder builder = new AlertDialog.Builder(End.this);
        builder.setTitle("Внимание!")
                .setMessage("Если вы продолжите, то весь прогресс будет утерян безвозвратно! Хотите продолжить?")
                .setCancelable(false).
                setNegativeButton("Ок",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent refresh = new Intent (End.this, MainActivity.class);
                                startActivity(refresh);
                            }
                        });
        builder.setPositiveButton("Отмена", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                onPause();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();


    }

    private void SendToSerever(){
        //отправка данных на сервер в потоке
        final String pollId = GetPollId(this);
        final ArrayList<QuestionAnswer> answers = GetWrittenAnswers(this);
        final QuestionAnswer[] array = answers.toArray(new QuestionAnswer[answers.size()]);
        String gender = "";

        //обработка выбора радио
        int genderId = -1;
        genderId = rg.getCheckedRadioButtonId();
        if(genderId == -1)
        {
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
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            return;
        }

        RadioButton gen = findViewById(genderId);
        gender = gen.getTag().toString();

        final String finalGender = gender;

        final PollReport[] result = {null};
        final String[] message = {""};

        Thread  thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    result[0] = SubmitResponse(baseUrl, pollId, array, finalGender, dateINT[0],dateINT[1],dateINT[2]);
                    message[0] = result[0].message;
                }
                catch(Exception e)
                {
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
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                    return;
                    //message[0] = e.getMessage();
                }
            }
        });

        thread.start();

        try{
            thread.join();

            WriteReportToDb(result[0],End.this);
            Intent send = new Intent (End.this, Send.class);
            send.putExtra("message", message[0]);
            send.putExtra("header", result[0].header);
            startActivity(send);
        }
        catch(Exception e)
        {
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
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            return;
        }

    }

   private DatePickerDialog DatePicker()
   {
       //final int [] data = new int[3];

       DatePickerDialog date = new DatePickerDialog(End.this, new DatePickerDialog.OnDateSetListener() {
           @Override
           public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
               calendar.set(Calendar.YEAR, year);
               dateINT[0] = year;
               calendar.set(Calendar.MONTH, month);
               dateINT[1] = month + 1 ;
               calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
               dateINT[2] = dayOfMonth;
           }
       }, 1990, 0,1);
       date.show();
       return date;
   }

}
