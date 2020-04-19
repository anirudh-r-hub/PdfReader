package com.example.pdfreader;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.source.DocumentSource;
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

    Uri filePath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book1);


        book1 = (PDFView) findViewById(R.id.book1); // creating a view which will display the pdf

        //book1.fromAsset("book1.pdf").load();

        //Get the bundle
        Bundle bundle = getIntent().getExtras();
        filePath = Uri.parse(bundle.getString("filePath")); //gving a filepath

        File file=new File(filePath.getPath());
        book1.fromFile(file)
            .enableDoubletap(true)
                .load(); // put the pdf in the pdf view

        Toast.makeText(Book1.this, ""+filePath.toString(), Toast.LENGTH_LONG).show();
        initialise_text_to_speech();



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
                    read_pdf_file();

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
    public void read_pdf_file() {
        try {
            String stringParser="";
            File file=new File(filePath.toString());
            PdfReader pdfReader = new PdfReader(filePath.toString());

            //for(int i=1;i<=pdfReader.getNumberOfPages();i++)
            stringParser += PdfTextExtractor.getTextFromPage(pdfReader, 2).trim();
            pdfReader.close();
            //outputTextView.setText(stringParser);
            Toast.makeText(getApplicationContext(), stringParser, Toast.LENGTH_LONG).show();
            speak(""+stringParser);


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
}
