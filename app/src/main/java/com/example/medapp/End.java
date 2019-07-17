package com.example.medapp;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;


import com.esotericsoftware.kryo.serializers.TaggedFieldSerializer;
import com.example.medapp.API.Models.PollReport;
import com.example.medapp.API.Models.QuestionAnswer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.example.medapp.API.PollInitializer.SubmitResponse;
import static com.example.medapp.ActivitiesController.GetPollId;
import static com.example.medapp.ActivitiesController.GetWrittenAnswers;
import static com.example.medapp.ActivitiesController.WriteReportToDb;

public class End extends AppCompatActivity {

    private Button doIt;
    private Button doNot;
    private RadioGroup rg;
    private Button dateB;
    private TextView dateBirth;
    private String baseUrl = "http://andrevvantonovv-001-site1.etempurl.com";
    Calendar calendar = Calendar.getInstance();
    final int[] countS = {0};
    String birth = "";
    Date db = new Date(1990,0,1);

    int [] dateINT = new int[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end);
        doNot = (Button) findViewById(R.id.EndB);
        doIt = (Button) findViewById(R.id.Send);
        rg = (RadioGroup) findViewById(R.id.radioGroup);
        dateB = (Button) findViewById(R.id.date);
        dateBirth = (TextView) findViewById(R.id.BirthDay);


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

        //dateBirth.setText(birth);

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
        Button btn1 = main.getButton(DialogInterface.BUTTON_NEGATIVE);
        Button btn2 = main.getButton(DialogInterface.BUTTON_POSITIVE);
        btn1.setBackgroundColor(404040);
        btn2.setBackgroundColor(404040);
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
                    final AlertDialog alertDialog = builder.create();
                    alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface dialog) {
                            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.DKGRAY);
                        }
                    });
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

    private void DatePicker()
   {

       final DatePickerDialog date = new DatePickerDialog(End.this, new DatePickerDialog.OnDateSetListener() {
           @Override
           public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
               dateINT[1] = month +1;
               dateINT[2] = year;
               dateINT[0] = dayOfMonth;
               birth = dayOfMonth + "/"+dateINT[1]+"/"+year;
               dateBirth.setText(birth);
           }
       }, 1990, 0,1);
       date.getWindow();

       date.setOnShowListener(new DialogInterface.OnShowListener() {
           @Override
           public void onShow(DialogInterface dialog) {
               date.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.DKGRAY);
               date.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(Color.DKGRAY);
           }
       });
       date.show();

       //return date;
   }

}
