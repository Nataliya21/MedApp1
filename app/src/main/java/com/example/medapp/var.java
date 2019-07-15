package com.example.medapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
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

import java.util.concurrent.TimeoutException;

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
    private static final int CAMERA_REQUEST = 0;

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
            final CheckBox[] mas = new CheckBox[ll.getChildCount()];
            final int[] count = {0};

            for (int i = 0; i < mas.length; i++) {
                mas[i] = (CheckBox) ll.getChildAt(i);
                if (mas[i].isChecked())
                    count[0]++;
            }

            if(count[0] ==0)
            {

                AlertDialog.Builder builder = new AlertDialog.Builder(var.this);
                builder.setTitle("Внимание!")
                        .setMessage("Вы не выбрали ни одного варианта ответа!")
                        .setCancelable(false).
                        setNegativeButton("Ок",
                                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            count[0] = 1;
                            mas[0].setChecked(true);
                    }
                });
                builder.setPositiveButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                            onPause();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }
            String[] options = new String[count[0]];

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
            final int[] Id = {-1};
                RadioGroup rg = (RadioGroup) viewGroup;
                String[] option = new String[1];
                Id[0] = rg.getCheckedRadioButtonId();
                if(Id[0] ==-1)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(var.this);
                    builder.setTitle("Внимание!")
                            .setMessage("Вы не выбрали ни одного варианта ответа!")
                            .setCancelable(false).
                            setNegativeButton("Ок",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                            Id[0] =1;
                                            RadioButton rb = findViewById(Id[0]);
                                            rb.setChecked(true);
                                        }
                                    });
                    builder.setPositiveButton("Отмена", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            onPause();
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();

                }
                RadioButton rb = findViewById(Id[0]);
                option[0] = rb.getTag().toString();

                NextQuestion(this, option, base);
            }
             else{
                    String [] opt = new String[1];
                    opt[0] = "";
                    NextQuestion(this, opt,base);
        }

    }

    private void Foto(){
        //открыть камеру и передать фото в imageView
        Intent camera = new Intent();
        camera.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(camera, CAMERA_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            // Фотка сделана, извлекаем картинку
            Bitmap thumbnailBitmap = (Bitmap) data.getExtras().get("data");
            image.setImageBitmap(thumbnailBitmap);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        BackToMain();
    }

    private void BackToMain(){

        AlertDialog.Builder builder = new AlertDialog.Builder(var.this);
        builder.setTitle("Вернуться к начальному экрану???")
                .setMessage("Усли вы продолжите, то весь прогресс будет утерян безвозвратно. Хотите продолжить?");
        builder.setCancelable(false);

        builder.setPositiveButton("Продолжить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent main = new Intent(var.this, MainActivity.class);
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
        main.show();

    }

}
