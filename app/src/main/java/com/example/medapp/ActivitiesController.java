package com.example.medapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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
import com.example.medapp.API.Models.PollReport;
import com.example.medapp.API.Models.Question;
import com.example.medapp.API.Models.QuestionAnswer;
import com.example.medapp.API.PollInitializer;

import net.rehacktive.waspdb.WaspDb;
import net.rehacktive.waspdb.WaspFactory;
import net.rehacktive.waspdb.WaspHash;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class ActivitiesController {

    // Обязательно вызываем перед тем как начать работать
    public static void Init(String pollId, String baseUrl, Context context){

        WaspDb Db = WaspFactory.openOrCreateDatabase(context.getFilesDir().getPath(), "MedDB", "pass");

        for (String h : Db.getAllHashes()){
            WaspHash hash = Db.openOrCreateHash(h);
            hash.flush();
        }

        //WaspFactory.destroyDatabase(Db);

        WritePollToDb(pollId, baseUrl, context);
        InitializeIndexes(context);
    }

    // Назначаем на кнопку "Следующий вопрос"
    public static void NextQuestion(Context context, String [] options, String atachment){

        Poll poll = GetPoll(context);

        // получаем текущие индексы
        int sectionIndex = GetSectionIndex(context);
        int questionIndex = GetQuestionIndex(context);

        System.out.println("Секция №" + sectionIndex + "\r\nВопрос №" + questionIndex);

        // Добовляем нужный ответ
         AddAnswer(poll.sections[sectionIndex].questions[questionIndex].id, atachment,options, context );

        // Вызываем функцию изменения индексов
        ResolveIndexes(context);

        // Получаем измененные индексы
        sectionIndex = GetSectionIndex(context);
        questionIndex = GetQuestionIndex(context);

        System.out.println("Смена индексов");
        System.out.println("Секция №" + sectionIndex + "\r\nВопрос №" + questionIndex);

        if (NeedToSkipThisQuestion(context)){
            NextQuestion(context, new String[]{}, "");
            return;
        }

        if (sectionIndex == -1 && questionIndex == -1){
            //переходим на финальный экран
            System.out.println(GetPoll(context).toString());
            Intent intent = new Intent(context, End.class);
            context.startActivity(intent);
        } else {
            //переходим дальше
            Intent intent = new Intent(context, var.class);
            context.startActivity(intent);
        }
    }

    public static void FillActivity(TextView quest, TextView Sect, ScrollView sv, Context context, Button foto)
    {
        Poll poll = GetPoll(context);

        int sectionIndex = GetSectionIndex(context);
        int questionIndex = GetQuestionIndex(context);

        if(poll.sections[sectionIndex].questions[questionIndex].allowAtachments)
            FotoButton(foto, poll.sections[sectionIndex].questions[questionIndex].allowAtachments);

        quest.setText(poll.sections[sectionIndex].questions[questionIndex].text);
        Sect.setText(poll.sections[sectionIndex].name);

        if(poll.sections[sectionIndex].questions[questionIndex].multipleChoice)
            FillCheckBoxes(poll.sections[sectionIndex].questions[questionIndex].options,sv);
        else
            FillRadios(poll.sections[sectionIndex].questions[questionIndex].options, sv);
    }

    private static void FillRadios(Option[] options, ScrollView sv){
        RadioGroup rg = new RadioGroup(sv.getContext());
        rg.setId(R.id.radio);
        sv.addView(rg);

        for(int i = 0; i < options.length; i++)
        {
            RadioButton rb = new RadioButton(rg.getContext());
            rb.setText(options[i].text);
            rb.setTag(options[i].id);
            rb.setId(i+1);
            rb.setTextSize(24);
            rg.addView(rb);
        }
    }

    private static void FillCheckBoxes(Option [] option, ScrollView scrollView){
        LinearLayout ll = new LinearLayout(scrollView.getContext());
        ll.setId(R.id.checkbox);
        scrollView.addView(ll);

        for(int i = 0; i < option.length; i++)
        {
            CheckBox ch = new CheckBox(ll.getContext());
            ch.setText(option[i].text);
            ch.setTag(option[i].id);
            ch.setTextSize(24);
            ll.addView(ch);
        }
    }

    private  static  void FotoButton(Button foto, Boolean allowAtachments){
        if(allowAtachments)
        {
            foto.setVisibility(View.VISIBLE);
        }
    }

    private static void WritePollToDb(String pollId, String baseUrl, Context context){

        WaspDb Db = WaspFactory.openOrCreateDatabase(context.getFilesDir().getPath(), "MedDB", "pass");
        WaspHash hash = Db.openOrCreateHash("Poll");

        final Poll[] poll = {new Poll()};

        final String PollId = pollId;
        final String BaseUrl = baseUrl;

        Thread tr = new Thread(new Runnable() {
            @Override
            public void run() {

                try
                {
                    poll[0] = PollInitializer.GetPOll(BaseUrl, PollId);
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


        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void InitializeIndexes(Context context){

        WaspDb Db = WaspFactory.openOrCreateDatabase(context.getFilesDir().getPath(), "MedDB", "pass");

        WaspHash hash2 = Db.openOrCreateHash("SectionIndex");
        hash2.put("SectionIndex", 0);

        WaspHash hash3 = Db.openOrCreateHash("QuestionIndex");
        hash3.put("QuestionIndex", 0);
    }

    private static void ResolveIndexes(Context context){

        int newSectionIndex = -1;
        int newQuestionIndex = -1;

        int curSectionIndex = GetSectionIndex(context);
        int curQuestionIndex = GetQuestionIndex(context);

        Poll poll = GetPoll(context);

        int sectionsLength = poll.sections.length;
        int questionInSectionLength = poll.sections[curSectionIndex].questions.length;

        if (curQuestionIndex == questionInSectionLength - 1){
            newQuestionIndex = 0;
        } else{
            newQuestionIndex = curQuestionIndex + 1;
        }

        if (curQuestionIndex == questionInSectionLength - 1 && curSectionIndex < sectionsLength - 1){
            newSectionIndex = curSectionIndex + 1;
        } else if (curQuestionIndex == questionInSectionLength - 1){
            //это был последний вопрос
            newQuestionIndex = -1;
            newSectionIndex = -1;
        } else {
            newSectionIndex = curSectionIndex;
        }

        WaspDb Db = WaspFactory.openOrCreateDatabase(context.getFilesDir().getPath(), "MedDB", "pass");
        WaspHash hash2 = Db.openOrCreateHash("SectionIndex");
        WaspHash hash3 = Db.openOrCreateHash("QuestionIndex");

        hash3.flush();
        hash2.flush();

        // если небыло еще ответов, то эта функция вызвана в первый раз
        // не надо менять индексы
        if (GetWrittenAnswers(context).isEmpty()){
            hash2.put("SectionIndex", curSectionIndex);
            hash3.put("QuestionIndex", curQuestionIndex);
        } else {
            hash2.put("SectionIndex", newSectionIndex);
            hash3.put("QuestionIndex", newQuestionIndex);
        }

    }

    private static Boolean NeedToSkipThisQuestion(Context context){

        if (GetSectionIndex(context) == -1 || GetQuestionIndex(context) == -1)
            return false;

        if (GetPoll(context).sections.length <= GetSectionIndex(context))
            return false;

        if (GetPoll(context).sections[GetSectionIndex(context)].questions.length <= GetQuestionIndex(context))
            return false;

        Question curQuestion = GetPoll(context).sections[GetSectionIndex(context)].questions[GetQuestionIndex(context)];

        if (!curQuestion.showAfter){
            return false;
        }

        int showAfterScore = curQuestion.afterQuestionScore;
        String showAfterQuestionId = curQuestion.showAfterQuestion;
        String selectedOptionId = "";

        for(QuestionAnswer answer : GetWrittenAnswers(context)){
            if (answer.questionId.compareTo(showAfterQuestionId) == 0){
                // первая из маассива а не весь массив, т.к. нельзя выбрать
                // несколько ответов в вопросе showAfter
                if (answer.selectedOptions.length > 0){
                    selectedOptionId = answer.selectedOptions[0];
                    break;
                }
            }
        }

        int optionScore = -1;

        for (Question question : GetPoll(context).sections[GetSectionIndex(context)].questions){
            if (question.id.compareTo(showAfterQuestionId) == 0){
                for(Option option : question.options){
                    if (option.id.compareTo(selectedOptionId) == 0){
                        optionScore = option.score;
                        break;
                    }
                }
                break;
            }
        }

        return showAfterScore != optionScore;
    }

    private static void AddAnswer(final String questionId, final String attachment, final String [] selectedOptions, final Context context){

        QuestionAnswer answer = new QuestionAnswer();
        answer.questionId = questionId;
        answer.attachment = attachment;
        answer.selectedOptions = selectedOptions;

        WaspDb Db = WaspFactory.openOrCreateDatabase(context.getFilesDir().getPath(), "MedDB", "pass");
        WaspHash hash = Db.openOrCreateHash("Answers");

        hash.put(questionId, answer);
    }

    private static Poll GetPoll(Context context){

        WaspDb Db = WaspFactory.openOrCreateDatabase(context.getFilesDir().getPath(), "MedDB", "pass");
        WaspHash hash = Db.openOrCreateHash("Poll");

        return hash.get("Poll");
    }

    private static int GetQuestionIndex(Context context){
        WaspDb Db = WaspFactory.openOrCreateDatabase(context.getFilesDir().getPath(), "MedDB", "pass");
        WaspHash hash = Db.openOrCreateHash("QuestionIndex");
        return hash.get("QuestionIndex");
    }

    private static int GetSectionIndex(Context context){
        WaspDb Db = WaspFactory.openOrCreateDatabase(context.getFilesDir().getPath(), "MedDB", "pass");
        WaspHash hash = Db.openOrCreateHash("SectionIndex");
        return hash.get("SectionIndex");
    }

    public static ArrayList<QuestionAnswer> GetWrittenAnswers(Context context){

        WaspDb Db = WaspFactory.openOrCreateDatabase(context.getFilesDir().getPath(), "MedDB", "pass");
        WaspHash hash = Db.openOrCreateHash("Answers");

        return new ArrayList<>(hash.<QuestionAnswer>getAllValues());
    }

    public static String ConverBase64(Bitmap bitmap){
        String base64 = "";

        try{
            ByteBuffer buffer =ByteBuffer.allocate(bitmap.getRowBytes()*bitmap.getHeight());
            bitmap.copyPixelsToBuffer(buffer);
            byte[] data = buffer.array();
            return base64 = Base64.encodeToString(data, Base64.DEFAULT);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }

        return null;
    }

    public static String GetPollId(Context context){
        Poll poll = GetPoll(context);
        String pollId = poll.id;
        return pollId;
    }

    public static void WriteReportToDb (final PollReport report, Context context){
        WaspDb Db = WaspFactory.openOrCreateDatabase(context.getFilesDir().getPath(), "MedDB", "pass");
        WaspHash hash = Db.openOrCreateHash("PollReport");

        final PollReport[] result = new PollReport[1];

        Thread tr = new Thread(new Runnable() {
            @Override
            public void run() {

                try
                {
                    result[0] = report;
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

            hash.put("PollReport", result[0]);


        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static int GetUniqNumber(Context context){
        WaspDb Db = WaspFactory.openOrCreateDatabase(context.getFilesDir().getPath(), "MedDB", "pass");
        WaspHash hash = Db.openOrCreateHash("PollReport");

        PollReport result = hash.get("PollReport");
        int number;
        number = result.uniqueNumber;

        return number;
    }
}
