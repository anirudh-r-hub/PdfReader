package com.example.pdfreader;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.speech.tts.TextToSpeech;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
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
import java.util.Locale;

public class Book1 extends AppCompatActivity {

    PDFView book1;
    private TextToSpeech text_to_speech;
    private static final String COLON = ":";
    boolean speak,stop,fullscr;
    EditText edit_goto;
    LinearLayout l1;

    Uri filePath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book1);

        edit_goto = (EditText) findViewById(R.id.edit_goto);
        l1 = (LinearLayout) findViewById(R.id.l1);
        book1 = (PDFView) findViewById(R.id.book1); // creating a view which will display the pdf
        final FloatingActionButton fab = findViewById(R.id.fab);
        final FloatingActionButton full_screen = findViewById(R.id.fab_fullscreen);
        speak = true;
        stop = false;
        fullscr = false;

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

        //book1.fromAsset("book1.pdf").load();

        //Get the bundle
        Bundle bundle = getIntent().getExtras();
        filePath = Uri.parse(bundle.getString("filePath")); //gving a filepath

        File file=new File(filePath.getPath());
        book1.fromFile(file)
            .enableDoubletap(true)
                .scrollHandle(new DefaultScrollHandle(this, true))
                .load(); // put the pdf in the pdf view

        Toast.makeText(Book1.this, ""+filePath.toString(), Toast.LENGTH_LONG).show();
        initialise_text_to_speech();

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
                        fab.setImageResource(R.drawable.ic_add_circle);
                        stop = false;
                        speak = true;
                    }
                }
            }
        });
    }



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
                    text_to_speech.setLanguage(Locale.US);
                    //read_pdf_file();

                }

            }
        });
    }
    private void speak(String message)
    {
        if(Build.VERSION.SDK_INT>=21)
        {
            text_to_speech.speak(message,TextToSpeech.QUEUE_ADD,null,null);
        }
    }
    public void read_pdf_file(int page_no) {
        try {
            String stringParser="";
            File file=new File(filePath.toString());
            PdfReader pdfReader = new PdfReader(filePath.toString());

            //for(int i=page_no;i<=pdfReader.getNumberOfPages();i++) {
                    stringParser += PdfTextExtractor.getTextFromPage(pdfReader, page_no).trim();
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

    public void gotopage(View view) {
        if(edit_goto.getText().toString().equals(""))
            edit_goto.requestFocus();
        else {
            int entered_page = Integer.parseInt(edit_goto.getText().toString());
            book1.jumpTo(entered_page-1);
        }
    }
}
