package com.example.medapp;

import android.content.Context;

import com.example.medapp.API.Models.Option;
import com.example.medapp.API.Models.Poll;
import com.example.medapp.API.Models.Question;
import com.example.medapp.API.Models.QuestionAnswer;
import com.example.medapp.API.PollInitializer;

import net.rehacktive.waspdb.WaspDb;
import net.rehacktive.waspdb.WaspFactory;
import net.rehacktive.waspdb.WaspHash;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ActivitiesController {

    // Обязательно вызываем перед тем как начать работать
    public static void Init(String pollId, String baseUrl, Context context){

        WritePollToDb(pollId, baseUrl, context);
        InitializeIndexes(context);
    }

    // Назначаем на кнопку "Следующий вопрос"
    public static void NextQuestion(Context context){

        Poll poll = GetPoll(context);

        // получаем текущие индексы
        int sectionIndex = GetSectionIndex(context);
        int questionIndex = GetQuestionIndex(context);

        // Вызываем функцию AddAnnswer с нужными параметрами

        if (NeedToSkipNextQuestion(context)){
            // Добовляем пустой ответ
            AddAnswer(
                poll.sections[sectionIndex].questions[questionIndex].id,
                "",
                new String[]{},
                context
            );
        }
        else {
            // Добовляем нужный ответ
            // AddAnswer(poll.sections[sectionIndex].questions[questionIndex].id, . . . );
        }

        // Вызываем функцию изменения индексов
        ResolveIndexes(context);

        // Получаем измененные индексы
        sectionIndex = GetSectionIndex(context);
        questionIndex = GetQuestionIndex(context);

        if (sectionIndex == -1 && questionIndex == -1){
            //переходим на финальный экран
            return;
        }

        //рендерим вопрос
        if (poll.sections[sectionIndex].questions[questionIndex].multipleChoice){
            FillCheckBoxes();
        }
        else {
            FillRadios();
        }
    }

    private static void FillRadios(){

    }

    private static void FillCheckBoxes(){

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
        } else if (NeedToSkipNextQuestion(context)){
            newQuestionIndex = curQuestionIndex + 2;
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

    private static Boolean NeedToSkipNextQuestion(Context context){

        Question nextQuestion = GetPoll(context).sections[GetSectionIndex(context)].questions[GetQuestionIndex(context) + 1];

        if (!nextQuestion.showAfter){
            return false;
        }

        int showAfterScore = nextQuestion.afterQuestionScore;
        String showAfterQuestionId = nextQuestion.showAfterQuestion;
        String selectedOptionId = "";

        for(QuestionAnswer answer : GetWrittenAnswers(context)){
            if (answer.questionId == showAfterQuestionId){
                //первая из маассива а не весь массив, т.к. нельзя выбрать
                //несколько ответов в вопросе showAfter
                selectedOptionId = answer.selectedOptions[0];
                break;
            }
        }

        int optionScore = -1;

        for (Question question : GetPoll(context).sections[GetSectionIndex(context)].questions){
            if (question.id == showAfterQuestionId){
                for(Option option : question.options){
                    if (option.id == selectedOptionId){
                        optionScore = option.score;
                    }
                }
                break;
            }
        }

        return showAfterScore == optionScore;
    }

    private static void AddAnswer(String questionId, String attachment, String [] selectedOptions, Context context){

        QuestionAnswer answer = new QuestionAnswer();
        answer.questionId = questionId;
        answer.attachment = attachment;
        answer.selectedOptions = selectedOptions;

        WaspDb Db = WaspFactory.openOrCreateDatabase(context.getFilesDir().getPath(), "MedDB", "pass");
        WaspHash hash = Db.openOrCreateHash("Answers");

        hash.put("Answer", answer);

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

    private static ArrayList<QuestionAnswer> GetWrittenAnswers(Context context){

        WaspDb Db = WaspFactory.openOrCreateDatabase(context.getFilesDir().getPath(), "MedDB", "pass");
        WaspHash hash = Db.openOrCreateHash("Answers");

        return new ArrayList<>(hash.<QuestionAnswer>getAllValues());
    }
}
