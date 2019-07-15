package com.example.medapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import static com.example.medapp.ActivitiesController.ConverBase64;
import static com.example.medapp.ActivitiesController.FillActivity;
import static com.example.medapp.ActivitiesController.NextQuestion;

public class var extends AppCompatActivity {

    private TextView qst;
    private ScrollView sv;
    private TextView sect;
    private Button next;
    private  Button foto;
    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_var);

        qst = (TextView) findViewById(R.id.Qst);
        sect = (TextView) findViewById(R.id.SectId);
        sv = (ScrollView) findViewById(R.id.sv);
        foto = (Button) findViewById(R.id.foto);
        image = (ImageView) findViewById(R.id.Foto);

        foto.setVisibility(View.INVISIBLE);
        image.setVisibility(View.INVISIBLE);

        //показ вопроса
        FillActivity(qst,sect,sv, this,foto, image);

        next = (Button) findViewById(R.id.Next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Next();
            }
        });
        foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Foto();
            }
        });

    }

    private void  Next(){

        //записать отвеы из активити

        ViewGroup viewGroup = (ViewGroup) sv.getChildAt(0);

        String base = "";

        if(image.getVisibility()==View.VISIBLE && image.getDrawable() != null)
        {
            String [] option = new String[1];
            option[0] = "";

            BitmapDrawable drawable = (BitmapDrawable) image.getDrawable();
            Bitmap bmp = drawable.getBitmap();

            base = ConverBase64(bmp);

            NextQuestion(this, option, base);
        }
        if(viewGroup.getId()==R.id.checkbox) {
            //check
            LinearLayout ll = (LinearLayout) viewGroup;
            CheckBox[] mas = new CheckBox[ll.getChildCount()];
            int count = 0;

            for (int i = 0; i < mas.length; i++) {
                mas[i] = (CheckBox) ll.getChildAt(i);
                if (mas[i].isChecked())
                    count++;
            }

            String[] options = new String[count];

            for (int i = 0; i < mas.length; i++) {
                int j = 0;
                if (mas[i].isChecked()) {
                    options[j] = mas[i].getTag().toString();
                    j++;
                }
            }

            NextQuestion(this, options, base);
        }
          else  if(viewGroup.getId()==R.id.radio) {
                //radio
                RadioGroup rg = (RadioGroup) viewGroup;
                String[] option = new String[1];
                int Id = rg.getCheckedRadioButtonId();
                RadioButton rb = findViewById(Id);
                option[0] = rb.getTag().toString();

                NextQuestion(this, option, base);
            }
             else{
                    String [] opt = new String[1];
                    opt[0] = "";
                    NextQuestion(this, opt,base);
        }

        //Intent intent = getIntent();
        //    finish();
        //    startActivity(intent);
    }

    private void Foto(){
        //открыть камеру и передать фото в imageView

    }

}
