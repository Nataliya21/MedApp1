package com.example.medapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.medapp.API.Models.Option;
import com.example.medapp.API.Models.Poll;
import com.example.medapp.API.Models.Question;
import com.example.medapp.API.Models.QuestionAnswer;
import com.example.medapp.API.Models.Section;
import com.example.medapp.API.PollInitializer;

import net.rehacktive.waspdb.WaspDb;
import net.rehacktive.waspdb.WaspFactory;
import net.rehacktive.waspdb.WaspHash;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;


public class BD {

    private static int sectInd = -1 , qstInd = -1;

    public static void WriteToDbPoll(final String baseUrl, final String id, Context context){
        //запись в бд опроса и индеска в БД

            WaspDb Db = WaspFactory.openOrCreateDatabase(context.getFilesDir().getPath(), "MedDB", "pass");
            WaspHash hash = Db.openOrCreateHash("Poll");
            final Poll[] poll = {null};

                Thread tr = new Thread(new Runnable() {
                    @Override
                    public void run() {

                        try
                        {
                            poll[0] = PollInitializer.GetPOll(baseUrl, id);
                        }
                        catch( Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                });
                tr.start();

            try {
                tr.join();

                hash.put("Poll", poll[0]);

                WaspHash hash2 = Db.openOrCreateHash("SectionId");
                hash2.put("SectionId",0);

                WaspHash hash3 = Db.openOrCreateHash("QuestionId");
                hash3.put("QuestionId",0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }



    }

    public  static void Next(TextView question, TextView section, ScrollView scroll, String score, Context context, Button foto, String [] id, String questionId){
        //какой вопрос нужно показывать потом
        WaspDb Db = WaspFactory.openOrCreateDatabase(context.getFilesDir().getPath(), "MedDB", "pass");
        WaspHash hash = Db.openOrCreateHash("Poll");
        Poll data = hash.get("Poll");

        Section [] sct = data.sections;
        Question [] qst = sct[sectInd].questions;

        //переход к fill в зависимости от балла

        WaspHash answer = Db.openOrCreateHash("Answer");
        List<QuestionAnswer> answers = answer.getAllValues();



        //не последний ли это вопрос в секции
        if(qstInd == qst.length-1)
        {
            sectInd++;
            qstInd = 0;
        }
        //проверка не последний ли это вопрос
        if((sectInd==sct.length-1)&&(qstInd == qst.length-1))
        {
            //переход к энд активити
            //передать что-то, чтобы понять, что нужно перейти к енд активити
        }
    }

    public static void Fill(TextView quest, TextView Sect, ScrollView sv, Context context, Button foto){

        WaspDb Db = WaspFactory.openOrCreateDatabase(context.getFilesDir().getPath(), "MedDB", "pass");
        WaspHash hash = Db.openOrCreateHash("Poll");
        Poll poll = hash.get("Poll");

        if((sectInd==-1)||(qstInd==-1)) {
            WaspHash h2 = Db.openOrCreateHash("SectionId");
            sectInd = h2.get("SectionId");

            WaspHash h3 = Db.openOrCreateHash("QuestionId");
            qstInd = h3.get("QuestionId");
        }

        Section [] sct = poll.sections;
        Question [] qst = sct[sectInd].questions;
        Option [] opt = qst[qstInd].options;

        quest.setText(qst[qstInd].text);
        quest.setTag(qst[qstInd].id);
        Sect.setText(sct[sectInd].name);

        if(qst[qstInd].allowAtachments)
        {
            foto.setVisibility(View.VISIBLE);
            LinearLayout ll = new LinearLayout(sv.getContext());
            sv.addView(ll);

            ImageView imageView = new ImageView(ll.getContext());
            imageView.setVisibility(View.VISIBLE);

        }
        else{
            foto.setVisibility(View.INVISIBLE);
        }

        if(qst[qstInd].multipleChoice)
        {
            //создание чексбоксов и лэйаута. Запись вариантов ответа
            LinearLayout ll = new LinearLayout(sv.getContext());
            sv.addView(ll);

            for(int i = 0; i < opt.length; i++)
            {
                CheckBox ch = new CheckBox(ll.getContext());
                ch.setText(opt[i].text);
                ch.setTag(opt[i].id);
                ll.addView(ch);
            }

        }
        else{
            //создание радиогруппы и радио кнопок. Запись вариантов ответа
            RadioGroup rg = new RadioGroup(sv.getContext());
            sv.addView(rg);

            for(int i = 0; i < opt.length; i++)
            {
                RadioButton rb = new RadioButton(rg.getContext());
                rb.setText(opt[i].text);
                rb.setTag(opt[i].id);
                rg.addView(rb);
            }

        }

    }

    public static void AddAnswers(String [] id, String qstId, Context context, String pict)
    {
        WaspDb Db = WaspFactory.openOrCreateDatabase(context.getFilesDir().getPath(), "MedDB", "pass");
        WaspHash hash = Db.openOrCreateHash("Answer");

        QuestionAnswer questionAnswer = new QuestionAnswer();
        questionAnswer.questionId = qstId;
        questionAnswer.selectedOptions = id;
        questionAnswer.attachment = pict;

        hash.put("Answer", questionAnswer);

    }

    public static String ConverBase64(Bitmap bitmap){
        String base64 = "";
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte [] byteArray = stream.toByteArray();
        base64 = Base64.encodeToString(byteArray, Base64.NO_WRAP);

        return base64;
    }


}
