package com.example.pdfreader;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.itextpdf.text.pdf.PdfReader;
import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,1);
      //          speechRecognizer.startListening(intent);
            }

        });
        //initialise_text_to_speech();
        //initialise_speech_recognizer();
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


    /*private void initialise_text_to_speech() {
        text_to_speech=new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(text_to_speech.getEngines().size()==0)
                {
                    Toast.makeText(MainActivity.this,"There is no speech engine",
                            Toast.LENGTH_LONG).show();
                    finish();
                }
                else
                {
                    text_to_speech.setLanguage(Locale.US);

                }

            }
        });
    }*/

    /*private void initialise_speech_recognizer() {

        if(SpeechRecognizer.isRecognitionAvailable(this))
        {
            speechRecognizer=SpeechRecognizer.createSpeechRecognizer(this);
            speechRecognizer.setRecognitionListener(new RecognitionListener() {
                @Override
                public void onReadyForSpeech(Bundle params) {

                }

                @Override
                public void onBeginningOfSpeech() {

                }

                @Override
                public void onRmsChanged(float rmsdB) {

                }

                @Override
                public void onBufferReceived(byte[] buffer) {

                }

                @Override
                public void onEndOfSpeech() {

                }

                @Override
                public void onError(int error) {

                }

                @Override
                public void onResults(Bundle results)
                {
                    List<String> speech_results=results.getStringArrayList(
                            SpeechRecognizer.RESULTS_RECOGNITION
                    );
                    process_result(speech_results.get(0));

                }

                @Override
                public void onPartialResults(Bundle partialResults) {

                }

                @Override
                public void onEvent(int eventType, Bundle params) {

                }
            });
        }
    }*/

    /*private void process_result(String command) {
        command=command.toLowerCase();

        if(command.equals("hello"))
        {
            speak("bye");

        }
        else if(command.equals("bye"))
        {
            speak("hello");
        }
        else
            speak("Sorry cannot understand");

    }*/

    /*private void speak(String message)
    {
        if(Build.VERSION.SDK_INT>=21)
        {
            text_to_speech.speak(message,TextToSpeech.QUEUE_ADD,null,null);
        }
    }*/


}
