package com.example.medapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
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

import java.util.ArrayList;

import static com.example.medapp.ActivitiesController.ConverBase64;
import static com.example.medapp.ActivitiesController.FillActivity;
import static com.example.medapp.ActivitiesController.NextQuestion;

public class var extends AppCompatActivity {

    private TextView qst;
    private ScrollView sv;
    private TextView sect;
    private Button next;
    private  Button foto;
    String base = "";
    private static final int CAMERA_REQUEST = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_var);

        qst = (TextView) findViewById(R.id.Qst);
        sect = (TextView) findViewById(R.id.SectId);
        sv = (ScrollView) findViewById(R.id.sv);
        foto = (Button) findViewById(R.id.foto);

        foto.setVisibility(View.INVISIBLE);

        //показ вопроса
        FillActivity(qst,sect,sv, this,foto);

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
                foto.setText("Удалить картинку");
            }
        });

    }

    private void  Next(){

        ViewGroup viewGroup = (ViewGroup) sv.getChildAt(0);

        ArrayList<String> options = new ArrayList<>();


        if (viewGroup.getId() == R.id.checkbox){
            LinearLayout ll = (LinearLayout) viewGroup;

            for(int i = 0; i < ll.getChildCount(); i++){
                if ( ((CheckBox)ll.getChildAt(i)).isChecked() ){
                    options.add(ll.getChildAt(i).getTag().toString());
                }
            }
        } else if ( viewGroup.getId()==R.id.radio ){
            RadioGroup rg = (RadioGroup) viewGroup;

            RadioButton rb = findViewById(rg.getCheckedRadioButtonId());
            options.add(rb.getTag().toString());

        }

        if (options.size() == 0){
            AlertDialog.Builder builder = new AlertDialog.Builder(var.this);
            builder.setTitle("Внимание!")
                    .setMessage("Вы не выбрали ни одного варианта ответа!")
                    .setCancelable(false).
                    setNegativeButton("Ок",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
            final AlertDialog alertDialog = builder.create();
            alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.DKGRAY);
                }
            });
            alertDialog.show();
            return;
        }

        NextQuestion(this, options.toArray(new String[options.size()]), base);

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
            base = ConverBase64(thumbnailBitmap);
            //image.setImageBitmap(thumbnailBitmap);
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        BackToMain();
    }

    private void BackToMain(){

        AlertDialog.Builder builder = new AlertDialog.Builder(var.this);
        builder.setTitle("Вернуться к начальному экрану?")
                .setMessage("Если вы продолжите, то весь прогресс будет утерян безвозвратно. Хотите продолжить?");
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
        final AlertDialog main = builder.create();
        main.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                main.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.DKGRAY);
                main.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.DKGRAY);
            }
        });
        main.show();

    }

}
