package com.example.medapp;

import android.content.Context;
import android.widget.CheckBox;
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

import java.util.ArrayList;


public class BD {

    private static ArrayList<QuestionAnswer> answers = new ArrayList<>();
    private static ArrayList<Question> qst = new ArrayList<>();
    private static int sectInd = -1 , qstInd = -1;

    public static void WriteToDbPoll(String baseUrl, String id, Context context){
        //запись в бд опроса и индеска в БД
        WaspDb Db = WaspFactory.openOrCreateDatabase(context.getFilesDir().getPath(), "MedDB", "pass");

        String psth = context.getFilesDir().getPath();

        WaspHash hash = Db.openOrCreateHash("Poll");
        Poll poll= null;
        try {
            poll = PollInitializer.GetPOll(baseUrl, id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        hash.put("Poll", poll);

        WaspHash hash2 = Db.openOrCreateHash("SectionId");
        hash2.put("SectionId",0);

        WaspHash hash3 = Db.openOrCreateHash("QuestionId");
        hash3.put("QuestionId", 0);

    }

    public  static void Next(){
        //какой вопрос нужно показывать потом
        //проверка не последний ли это вопрос и передать инф в функцию next()
    }

    //что-то не так с заполнением
    public static void Fill(TextView quest, TextView Sect, ScrollView sv, Context context){
        //заполнение экрана вопросом
        WaspDb Db = WaspFactory.openOrCreateDatabase(context.getFilesDir().getPath(), "MedDB", "pass");
        String path = context.getFilesDir().getPath();
        WaspHash hash = Db.openOrCreateHash("Poll");
        Poll poll = hash.get("Poll");

        if((sectInd==-1)||(qstInd==-1)) {
            WaspHash h2 = Db.openOrCreateHash("SectionId");
            sectInd = h2.get("SectionId");

            WaspHash h3 = Db.openOrCreateHash("QuestionId");
            qstInd = h3.get("QuestionId");
        }

       /* Section [] sct = poll.sections;
        Question [] qst = sct[sectInd].questions;
        Option [] opt = qst[qstInd].options;

        quest.setText(qst[qstInd].text);
        Sect.setText(sct[sectInd].name);

        if(qst[qstInd].multipleChoice==true)
        {
            //создание чексбоксов и лэйаута. Запись вариантов ответа
            LinearLayout ll = new LinearLayout(sv.getContext());
            sv.addView(ll);

            for(int i = 0; i < opt.length; i++)
            {
                CheckBox ch = new CheckBox(ll.getContext());
                ch.setText(opt[i].text);
                ch.setTag(opt[i].score);
                ll.addView(ch);
            }
            qstInd++;
        }
        else{
            //создание радиогруппы и радио кнопок. Запись вариантов ответа
            RadioGroup rg = new RadioGroup(sv.getContext());
            sv.addView(rg);

            for(int i = 0; i < opt.length; i++)
            {
                RadioButton rb = new RadioButton(rg.getContext());
                rb.setText(opt[i].text);
                rb.setTag(opt[i].score);
                rg.addView(rb);
            }
            qstInd++;
        }

        //проверка не последний ли это вопрос в секции
        if(qstInd == sct.length)
        {
            sectInd++;
            qstInd = 0;
        }
        */

    }

    private static void CreateBD (Context context){
        WaspFactory.openOrCreateDatabase(context.getFilesDir().getPath(), "MedDB", "pass");
    }

    public static void AddAnswers(Question qst)
    {
        //ПЕРЕДЕЛАТЬ
        QuestionAnswer answer  = new QuestionAnswer();

        answer.questionId = qst.id;
        answer.attachment = "base 64 картинки";

        ArrayList<String> options = new ArrayList<>();
        options.add(qst.options[0].id);

        answer.selectedOptions = options.toArray(new String[options.size()]);

        answers.add(answer);
    }

    public static void WriteAnswers(ArrayList<QuestionAnswer> answrs, Context context)
    {
        WaspDb Db = WaspFactory.openOrCreateDatabase(context.getFilesDir().getPath(), "MedDB", "pass");
        WaspHash hash = Db.openOrCreateHash("QuestionAnswer");
        hash.put("QuestionAnswer", answrs);
    }
}
