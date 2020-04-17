package com.example.pdfreader;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;

public class Book1 extends AppCompatActivity {

    PDFView book1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book1);


        book1 = (PDFView) findViewById(R.id.book1);

        //book1.fromAsset("book1.pdf").load();

        //Get the bundle
        Bundle bundle = getIntent().getExtras();
        Uri filePath = Uri.parse(bundle.getString("filePath"));

        book1.fromUri(filePath).load();

        Toast.makeText(Book1.this, filePath.getPath(), Toast.LENGTH_LONG).show();

    }
}
