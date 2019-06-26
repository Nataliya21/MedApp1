package com.example.medapp;

import java.util.ArrayList;

public class MJ {
        public static void MJ(String[] args) throws Exception {

            String baseUrl = "http://localhost:49214";

            ArrayList<PollData> polls = PollInitializer.GetPollsData(baseUrl);
            Poll poll = PollInitializer.GetPOll(baseUrl, polls.get(0).id);

            ArrayList<QuestionAnswer> answers = new ArrayList<>();

            for (Section section : poll.sections){

                for(Question question : section.questions){
                    QuestionAnswer answer = new QuestionAnswer();

                    answer.questionId = question.id;
                    answer.attachment = "base 64 картинки";

                    ArrayList<String> options = new ArrayList<>();
                    options.add(question.options[0].id);

                    answer.selectedOptions = options.toArray(new String[options.size()]);

                    answers.add(answer);
                }
            }

            PollReport report = PollInitializer.SubmitResponse(baseUrl, polls.get(0).id, answers.toArray(new QuestionAnswer[answers.size()]), "m", 1998, 1, 1);

            System.out.println(report.uniqueNumber);
            System.out.println(report.header);
            System.out.println(report.message);
        }

    }
