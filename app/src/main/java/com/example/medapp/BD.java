package com.example.medapp;

import android.content.Context;

import com.example.medapp.API.Models.PollData;

import net.rehacktive.waspdb.WaspDb;
import net.rehacktive.waspdb.WaspFactory;
import net.rehacktive.waspdb.WaspHash;




public class BD {


    public static void WriteToDb(PollData pollData, Context context){
        //запись в бд опроса и индеска в БД
        WaspDb Db = WaspFactory.openOrCreateDatabase(context.getFilesDir().getPath(), "MedDB", "pass");

        WaspHash hash = Db.openOrCreateHash("PollData");
        hash.put("PollData", pollData);
        //hash.get(pollData.name);
        //System.out.print("qwerty");

    }

    public  static void NextQst(){
        //переход к следующему вопросу
    }

    private static void QstType(){
        //определение типа вопроса
    }

    private static void Fill(){
        //заполнение экрана вопросом
    }

    private static void CreateBD (Context context){

        WaspFactory.openOrCreateDatabase(context.getFilesDir().getPath(), "MedDB", "pass");

    }
}
