package com.example.medapp.API;

import com.example.medapp.API.Models.Option;
import com.example.medapp.API.Models.Poll;
import com.example.medapp.API.Models.PollData;
import com.example.medapp.API.Models.PollReport;
import com.example.medapp.API.Models.Question;
import com.example.medapp.API.Models.QuestionAnswer;
import com.example.medapp.API.Models.Section;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class PollInitializer {
    public  static Poll GetPOll(String baseUrl, String pollId) throws Exception{

        Poll result = new Poll();
        HttpClient httpClient = HttpClientBuilder.create().build();

        try {
            HttpPost request = new HttpPost(baseUrl + "/api/Client/GetPoll?id=" + pollId);//задаем URL
            request.addHeader("content-type", "application/json; charset=UTF-8"); //задаем content-type, очень важно!

            HttpResponse response = httpClient.execute(request);

            JSONObject resp = new JSONObject(EntityUtils.toString(response.getEntity(), "UTF-8"));

            switch (response.getStatusLine().getStatusCode()){
                case 200:
                    if (!resp.getBoolean("error")){
                        result = ParsePoll(resp.getJSONObject("poll"));
                    } else {

                        throw new Exception("Ошибка выполнения запроса");
                    }

                    break;

                default:
                    throw new Exception("Ошибка выполнения запроса");
            }

        }
        catch (Exception ex) {
            throw new Exception("Ошибка выполнения запроса");
        }

        return result;
    }
    public  static ArrayList<PollData> GetPollsData(String baseUrl) throws Exception{
        ArrayList<PollData> result = new ArrayList<>();
        HttpClient httpClient = HttpClientBuilder.create().build();

        try {
            HttpPost request = new HttpPost(baseUrl + "/api/Client/GetPolls");//задаем URL
            request.addHeader("content-type", "application/json; charset=UTF-8"); //задаем content-type, очень важно!

            HttpResponse response = httpClient.execute(request);

            JSONObject resp = new JSONObject(EntityUtils.toString(response.getEntity(), "UTF-8"));

            switch (response.getStatusLine().getStatusCode()){
                case 200:
                    if (!resp.getBoolean("error")){
                        JSONArray pollsArr = resp.getJSONArray("pollsArr");

                        for (int i = 0; i < pollsArr.length(); i++){
                            PollData Data = new PollData();
                            JSONObject data = pollsArr.getJSONObject(i);

                            Data.id = data.getString("id");
                            Data.name = data.getString("name");

                            result.add(Data);
                        }
                    } else {

                        throw new Exception("Ошибка выполнения запроса");
                    }

                    break;

                default:
                    throw new Exception("Ошибка выполнения запроса");
            }

        }
        catch (Exception ex) {
            throw ex;
        }

        return result;
    }
    public  static PollReport SubmitResponse(String baseUrl, String pollId, QuestionAnswer[] answers, String gender, int birthYear, int birthMonth, int birhtDay ) throws  Exception{
        PollReport result = new PollReport();

        HttpClient httpClient = HttpClientBuilder.create().build();

        try {
            HttpPost request = new HttpPost(baseUrl + "/api/Client/Response");//задаем URL
            request.addHeader("content-type", "application/json; charset=UTF-8"); //задаем content-type, очень важно!

            JSONObject jsonParams = new JSONObject();//Создаем и заполняем объект параметров
            jsonParams.put("pollId", pollId);
            jsonParams.put("gender", gender);
            jsonParams.put("birthDay", birhtDay);
            jsonParams.put("birthMonth", birthMonth);
            jsonParams.put("birthYear", birthYear);

            JSONArray answersObj = new JSONArray();

            for (QuestionAnswer answer : answers){
                JSONObject answ = new JSONObject();

                answ.put("questionId", answer.questionId);
                answ.put("attachment", answer.attachment);

                JSONArray optionsObj = new JSONArray();

                for (String option : answer.selectedOptions){
                    optionsObj.put(option);
                }

                answ.put("selectedOptions", optionsObj);

                answersObj.put(answ);
            }

            jsonParams.put("answers", answersObj);

            StringEntity params = new StringEntity(jsonParams.toString());
            request.setEntity(params);

            HttpResponse response = httpClient.execute(request);
            JSONObject resp = null;
            resp = new JSONObject(EntityUtils.toString(response.getEntity(), "UTF-8"));


            switch (response.getStatusLine().getStatusCode()){
                case 200:
                    if (!resp.getBoolean("error")){
                        JSONObject data = resp.getJSONObject("resp");
                        result.uniqueNumber = data.getInt("unNumber");
                        result.header = data.getString("head");
                        result.message = data.getString("userMessage");
                    } else {

                        throw new Exception("Ошибка выполнения запроса");
                    }

                    break;

                default:
                    throw new Exception("Ошибка выполнения запроса");
            }

        }
        catch (Exception ex) {
            throw new Exception("Ошибка выполнения запроса");
        }

        return  result;
    }
    private static Poll ParsePoll(JSONObject poll) throws JSONException {
        Poll result = new Poll();

        result.id = poll.getString("id");
        result.name = poll.getString("name");

        JSONArray sections = poll.getJSONArray("sections");
        ArrayList<Section> Sections = new ArrayList<>();

        for(int i = 0; i < sections.length(); i++ ){
            JSONObject section = sections.getJSONObject(i);
            Section Section = new Section();

            Section.id = section.getString("id");
            Section.name = section.getString("name");

            JSONArray questions = section.getJSONArray("questions");
            ArrayList<Question> Questions = new ArrayList<>();

            for (int j = 0; j < questions.length(); j++){
                JSONObject question = questions.getJSONObject(j);
                Question Question = new Question();

                Question.afterQuestionScore = question.getInt("afterQuestionScore");
                Question.allowAtachments = question.getBoolean("allowAtachments");
                Question.id = question.getString("id");
                Question.multipleChoice = question.getBoolean("multipleChoice");
                Question.showAfterQuestion = question.getString("showAfterQuestion");
                Question.showAfter = question.getBoolean("showAfter");
                Question.text = question.getString("text");

                JSONArray options = question.getJSONArray("options");
                ArrayList<Option> Options = new ArrayList<>();

                for (int k = 0; k < options.length(); k++){
                    JSONObject option = options.getJSONObject(k);
                    Option Option = new Option();

                    Option.id = option.getString("id");
                    Option.score = option.getInt("score");
                    Option.text = option.getString("text");

                    Options.add(Option);
                }

                Question.options = Options.toArray(new Option [Options.size()]);
                Questions.add(Question);
            }

            Section.questions = Questions.toArray(new Question [Questions.size()]);
            Sections.add(Section);
        }

        result.sections = Sections.toArray(new Section [Sections.size()]);
        return  result;
    }
}
