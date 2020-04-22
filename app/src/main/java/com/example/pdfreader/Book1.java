package com.example.pdfreader;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnTapListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.github.barteksc.pdfviewer.source.DocumentSource;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.itextpdf.text.pdf.PdfObject;
import com.itextpdf.text.pdf.PdfPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.locks.ReentrantLock;

public class Book1 extends AppCompatActivity {

    PDFView book1;
    private TextToSpeech text_to_speech;
    private static final String COLON = ":";
    boolean speak,stop,fullscr, nightmode_state;
    EditText edit_goto;
    TextView text_totalpages;
    int number_of_pages;
    LinearLayout l1;
    Button btn_next;
    FloatingActionButton fab,night_mode;
    HashMap<String, String> map = new HashMap<String, String>();
    Uri filePath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book1);

        //night_mode = (Button)findViewById(R.id.btn_nightmode);
        text_totalpages = (TextView)findViewById(R.id.text_noofpages);
        night_mode=findViewById(R.id.night_mode);
        edit_goto = (EditText) findViewById(R.id.edit_goto);
        l1 = (LinearLayout) findViewById(R.id.l1);
        btn_next=(Button)findViewById(R.id.btn_next);
        book1 = (PDFView) findViewById(R.id.book1); // creating a view which will display the pdf
         fab = findViewById(R.id.fab);
        final FloatingActionButton full_screen = findViewById(R.id.fab_fullscreen);
        speak = true;
        stop = false;
        fullscr = false;
        nightmode_state = false;

        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "UniqueID");

        //****************open the file and initialize tts**********************************************
        //book1.fromAsset("book1.pdf").load();

        //Get the bundle



        Bundle bundle = getIntent().getExtras();
        filePath = Uri.parse(bundle.getString("filePath")); //gving a filepath

        final File file=new File(filePath.getPath());
        book1.fromFile(file)

            .enableDoubletap(true)
                .enableSwipe(false)
                .scrollHandle(new DefaultScrollHandle(this, true))




                .onPageChange(new OnPageChangeListener() {
                    @Override
                    public void onPageChanged(int page, int pageCount) {
                        edit_goto.setText(new Integer(page+1).toString());
                    }
                })
                .load(); // put the pdf in the pdf view

        Toast.makeText(Book1.this, ""+filePath.toString(), Toast.LENGTH_LONG).show();
        initialise_text_to_speech();

        //*****************handle the hiding toolbar button****************************************
        full_screen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!fullscr){
                    //hide views
                    l1.setVisibility(LinearLayout.GONE);
                    //make it full screen
                    //change icon
                    full_screen.setImageResource(R.drawable.ic_fullscreen_exit_black_24dp);
                    fullscr = true;


                } else
                {
                    //show views
                    l1.setVisibility(LinearLayout.VISIBLE);
                    //change icon
                    full_screen.setImageResource(R.drawable.ic_fullscreen_black_24dp);

                    //change fullscr status
                    fullscr = false;
                }

            }
        });

        night_mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"fhgjhg",Toast.LENGTH_LONG).show();
                if(nightmode_state==false)
                {
                    book1.setNightMode(true);
                    nightmode_state=true;
                }
                else
                {
                    book1.setNightMode(false);
                    nightmode_state=false;
                }
            }
        });



        //********************find the number of pages*********************************************
        PdfReader pdfReader = null;
        try {
            pdfReader = new PdfReader(filePath.toString());
            number_of_pages = pdfReader.getNumberOfPages();
            pdfReader.close();


        } catch (IOException e) {
            e.printStackTrace();
        }

        text_totalpages.setText("/ "+number_of_pages);


        //*****************************handle the night mode***************************************
        /*night_mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!nightmode_state) {
                    //set night mode
                    //change icon
                    night_mode.setBackground(getResources().getDrawable(R.drawable.ic_night_4_black_24dp,null));
                    //change nightmode_state
                    nightmode_state = true;
                } else {

                    book1.fromFile(file)
                            .enableDoubletap(true)
                            .scrollHandle(new DefaultScrollHandle(Book1.this, true))
                            .load(); // put the pdf in the pdf view

                    night_mode.setBackground(getResources().getDrawable(R.drawable.day_5_black_24dp,null));
                    //change nightmode_state
                    nightmode_state = false;
                }
            }
        });*/

        //*******************************speech to text button*************************************************
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(edit_goto.getText().toString().equals("")) {
                    edit_goto.requestFocus();

                } else {
                    int entered_page = Integer.parseInt(edit_goto.getText().toString());

                    if (speak) {

                        fab.setImageResource(R.drawable.ic_pause);
                        read_pdf_file(entered_page);
                        speak = false;
                        stop = true;
                    } else if (stop) {
                        text_to_speech.stop();
                        fab.setImageResource(R.drawable.ic_speaker_phone_black_24dp);
                        stop = false;
                        speak = true;
                    }
                }
            }
        });




    }
//##############################################################################################################3


    private void initialise_text_to_speech() {
        text_to_speech=new TextToSpeech(Book1.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(text_to_speech.getEngines().size()==0)
                {
                    Toast.makeText(Book1.this,"There is no speech engine",
                            Toast.LENGTH_LONG).show();
                    finish();
                }
                else
                {
                    text_to_speech.setLanguage(Locale.ENGLISH);

                    if(status==text_to_speech.SUCCESS){
                        text_to_speech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                            @Override
                            public void onStart(String utteranceId) {


                            }

                            @Override
                            public void onDone(String utteranceId) {
                                runOnUiThread(new Runnable() {

                                    public void run() {
                                        Toast.makeText(getApplicationContext(),"bye bye",Toast.LENGTH_LONG).show();
                                        btn_next.performClick();
                                        speak=true;
                                        stop=false;
                                        fab.performClick();
                                    }
                                });


                            }

                            @Override
                            public void onError(String utteranceId) {

                            }
                        });
                    }

                }

            }
        });
    }
    private void speak(String message)
    {
        if(Build.VERSION.SDK_INT>=21)
        {
            text_to_speech.speak(message,TextToSpeech.QUEUE_ADD,null,map.toString());
        }
    }
    public void read_pdf_file(int page_no) {
        try {
            String stringParser="";
            File file=new File(filePath.toString());
            PdfReader pdfReader = new PdfReader(filePath.toString());


            //for(int i=page_no;i<=pdfReader.getNumberOfPages();i++) {

                    // access the resource protected by this lock
                    stringParser = PdfTextExtractor.getTextFromPage(pdfReader, page_no).trim();
                    //Toast.makeText(getApplicationContext(), stringParser, Toast.LENGTH_LONG).show();
                    speak("" + stringParser);


            //}
            pdfReader.close();
            //outputTextView.setText(stringParser);


            //Toast.makeText(this,stringParser,Toast.LENGTH_LONG).show();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        text_to_speech.stop();
    }

    @Override
    protected void onDestroy() {

        if(text_to_speech != null) {
            text_to_speech.stop();
            text_to_speech.shutdown();
        }
        super.onDestroy();
    }

    //#########################################################################################################3

    public void gotopage(View view) {
        if(edit_goto.getText().toString().equals(""))
            edit_goto.requestFocus();
        else {
            int entered_page = Integer.parseInt(edit_goto.getText().toString());
            if(entered_page > number_of_pages)
                entered_page = number_of_pages;
            book1.jumpTo(entered_page-1);
        }
    }

    public void nextpage(View view) {
        if(edit_goto.getText().toString().equals(""))
            book1.jumpTo(1);
        else{
            int entered_page = Integer.parseInt(edit_goto.getText().toString());
            if(entered_page == number_of_pages) {
                book1.jumpTo(0);
                edit_goto.setText(new Integer( 1).toString());
            }
            else {
                book1.jumpTo(entered_page);
                edit_goto.setText(new Integer(entered_page + 1).toString());
            }
        }


    }

    public void prevpage(View view) {
        if(edit_goto.getText().toString().equals(""))
            book1.jumpTo(1);
        else{
            int entered_page = Integer.parseInt(edit_goto.getText().toString());
            if(entered_page - 1 == 0) {
                book1.jumpTo(number_of_pages - 1);
                edit_goto.setText(new Integer( number_of_pages).toString());
            }
            else {
                book1.jumpTo(entered_page - 2);
                edit_goto.setText(new Integer( entered_page - 1).toString());
            }
        }
    }

    //#######################################################################################################
}
//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
