package com.example.medapp;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.example.medapp.ActivitiesController.ConvertBase64;
import static com.example.medapp.ActivitiesController.FillActivity;
import static com.example.medapp.ActivitiesController.NextQuestion;

public class var extends AppCompatActivity {

    private TextView qst;
    private ScrollView sv;
    private TextView sect;
    private Button next;
    private Button foto;
    private ProgressBar spinner;

    private String currentPhotoPath;
    private String base = "";
    static final int REQUEST_TAKE_PHOTO = 1;

    private static final int CAMERA_REQUEST = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_var);

        qst = findViewById(R.id.Qst);
        sect = findViewById(R.id.SectId);
        sv = findViewById(R.id.sv);
        foto = findViewById(R.id.foto);
        spinner = findViewById(R.id.progressBar1);
        spinner.setVisibility(View.INVISIBLE);

        FillActivity(qst, sect, sv,this, foto);

        next = findViewById(R.id.Next);
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

    class NextQuestTask extends AsyncTask<Void, Void, Void> {

        public Context context;
        public String [] options;
        public String base;

        public NextQuestTask(Context Context, String [] Options, String Base){
            context = Context;
            options = Options;
            base = Base;
        }

        @Override
        protected void onPreExecute() {
            spinner.setVisibility(View.VISIBLE);

            sv.setVisibility(View.INVISIBLE);
            qst.setVisibility(View.INVISIBLE);

            sect.setVisibility(View.INVISIBLE);
            foto.setVisibility(View.INVISIBLE);
            next.setVisibility(View.INVISIBLE);

            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            NextQuestion(context, options, base);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
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
            if (rb != null){
                options.add(rb.getTag().toString());
            }
        }

        if (options.isEmpty()){
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

        new NextQuestTask(this, options.toArray(new String[options.size()]), base).execute();
    }


    private void Foto(){
        if (foto.getText().equals("Удалить фото")){
            base = "";
            foto.setText("Добавить фото");
        } else {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                if (photoFile != null) {

                    Uri photoURI;

                    if ((Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT))
                        photoURI = FileProvider.getUriForFile(getApplicationContext(), "com.example.android.fileprovider", photoFile);
                    else
                        photoURI = Uri.fromFile(photoFile);

                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, 1);
                }
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            File imgFile = new  File(currentPhotoPath);
            if(imgFile.exists()) {
                Bitmap originalBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

                int height = 0;
                int width = 0;

                if (originalBitmap.getHeight() > 480){
                    height = 480;
                    double coeff = 480.0 / (double) originalBitmap.getHeight();
                    width = (int)(originalBitmap.getWidth() * coeff);
                } else {
                    height = originalBitmap.getHeight();
                    width = originalBitmap.getWidth();
                }

                ExifInterface exif;

                try{
                    exif = new ExifInterface(imgFile.getAbsolutePath());
                } catch (Exception e){
                    e.printStackTrace();
                    return;
                }

                int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                int rotationInDegrees = exifToDegrees(rotation);

                Matrix matrix = new Matrix();
                if (rotation != 0) {matrix.preRotate(rotationInDegrees);}


                Bitmap adjustedBitmap = Bitmap.createBitmap(
                    originalBitmap,
                    0,
                    0,
                    originalBitmap.getWidth(),
                    originalBitmap.getHeight(),
                    matrix,
                    false
                );

                Bitmap scaledBitmap;

                if (rotation == 0){
                    scaledBitmap = Bitmap.createScaledBitmap(adjustedBitmap, width, height, false);
                } else {
                    scaledBitmap = Bitmap.createScaledBitmap(adjustedBitmap, height, width, false);
                }

                base = ConvertBase64(scaledBitmap);
                foto.setText("Удалить фото");
            }
        }
    }

    private static int exifToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) { return 90; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {  return 180; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {  return 270; }
        return 0;
    }

    @Override
    public void onBackPressed() {
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
                triggerRebirth(var.this);
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

    public static void triggerRebirth(Context context) {
        PackageManager packageManager = context.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(context.getPackageName());
        ComponentName componentName = intent.getComponent();
        Intent mainIntent = Intent.makeRestartActivityTask(componentName);
        context.startActivity(mainIntent);
        Runtime.getRuntime().exit(0);
    }

}
