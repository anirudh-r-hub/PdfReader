package com.example.pdfreader;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.itextpdf.text.pdf.PdfReader;
import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.os.FileUtils;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;


public class MainActivity extends AppCompatActivity {
    //private TextToSpeech text_to_speech;
    //private SpeechRecognizer speechRecognizer;
    Intent filePicker_intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
        != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    1001);
        }


    }

    //##########################FILE PICKER BUTTON#################################################################3

    /*
    This function will create a pdf picker.
    * */
    public void filePicker_btn(View view) {
       // filePicker_intent = new Intent(Intent.ACTION_GET_CONTENT);
        //filePicker_intent.setType("application/pdf");
        //startActivityForResult(Intent.createChooser(filePicker_intent,"Choose PDF"), 1);
        new MaterialFilePicker()
                .withActivity(this)
                .withFilter(Pattern.compile(".*\\.pdf$"))
                .withRootPath("/storage/")
                .withRequestCode(1000)
                .withHiddenFiles(true) // Show hidden files and folders
                .start();
    }
    /*
    This function will process the result returned by the startActivityforResult function
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1000 && resultCode == RESULT_OK) {


            String filePath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);;  //retreiving the data from the intent
            System.err.println(filePath);
            //Log.v("URI",filePath.getPath());
            Intent i = new Intent(MainActivity.this, Book1.class);
            i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            //Create the bundle
            Bundle bundle = new Bundle();
            bundle.putString("filePath",filePath); // putting the file path in bundle
            i.putExtras(bundle);

            startActivity(i);

        }

    }

    //#########################################################################################################3


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode)
        {
            case 1001:{
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
                {
                    Toast.makeText(getApplicationContext(),"Permission granted",Toast.LENGTH_LONG).show();
                }
                else
                {
                    finish();
                }
                return;
            }
        }

    }
}
