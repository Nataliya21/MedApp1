package com.example.medapp.API.Models;

public class Question {
    public String id ;

    public String text;
    public Option[] options;

    public Boolean allowAtachments;
    public Boolean showAfter;
    public Boolean multipleChoice;

    public String showAfterQuestion;
    public int afterQuestionScore;
}

