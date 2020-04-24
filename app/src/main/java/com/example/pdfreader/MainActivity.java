package com.example.pdfreader;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
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
import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.zip.Inflater;


public class MainActivity extends AppCompatActivity {
    //private TextToSpeech text_to_speech;
    //private SpeechRecognizer speechRecognizer;
    Intent filePicker_intent;
    DatabaseHelper recentdb;
    ListView listview_recentfiles;
    LinearLayout l1;
    ArrayList<String> listoffiles = new ArrayList<>();


    //############################# ON CREATE ############################################################
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listview_recentfiles = (ListView) findViewById(R.id.recentfiles);
        l1 = (LinearLayout) findViewById(R.id.select_files);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //************* permission for storage ***************************

        if(ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
        != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    1001);
        }


        //************** file picker listener **************************
        l1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // filePicker_intent = new Intent(Intent.ACTION_GET_CONTENT);
                //filePicker_intent.setType("application/pdf");
                //startActivityForResult(Intent.createChooser(filePicker_intent,"Choose PDF"), 1);
                new MaterialFilePicker()
                        .withActivity(MainActivity.this)
                        .withFilter(Pattern.compile(".*\\.pdf$"))
                        .withRootPath("/storage/")
                        .withRequestCode(1000)
                        .withHiddenFiles(true) // Show hidden files and folders
                        .start();
            }
        });


        //*******************List view code****************************
        //recentdb.insertData();

        fetchRecent();

        listview_recentfiles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(MainActivity.this, Book1.class);
                i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                //Create the bundle
                Bundle bundle = new Bundle();
                bundle.putString("filePath",listoffiles.get(position)); // putting the file path in bundle
                i.putExtras(bundle);

                startActivity(i);

            }
        });

    }

    public void fetchRecent() {
        listoffiles.clear();
        recentdb = new DatabaseHelper(this);

        listoffiles = recentdb.getAllData();
        if(listoffiles.size()!= 0)
        {
            listview_recentfiles.setVisibility(View.VISIBLE);

            /*while(res.moveToNext()) {
                listoffiles.add(res.getString(1));
            }*/

            ArrayAdapter arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listoffiles);

            listview_recentfiles.setAdapter(arrayAdapter);
        }
        else {
            listview_recentfiles.setVisibility(View.GONE);
        }

        recentdb.close();

    }

    //############################### MENU ON TOP BAR ################################################################

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId()) {

            case R.id.action_aboutus:
                startActivity(new Intent(MainActivity.this, AboutUs.class));
                return true;

            case R.id.action_exit:
                exitApp();
                return true;

            case R.id.action_clear_recent:
                recentdb = new DatabaseHelper(this);
                int rows_deleted = recentdb.clearAllData();
                Toast.makeText(this, "All "+rows_deleted+" entries removed!", Toast.LENGTH_SHORT).show();
                fetchRecent();
                recentdb.close();
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    //##########################FILE PICKER #############################################################################

    /*
    This function will create a pdf picker.
    * */
    /*public void filePicker_btn(View view) {
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

            //***********insert into database of recent files
            recentdb = new DatabaseHelper(this);
            recentdb.insertSingle(filePath);
            fetchRecent();
            recentdb.close();
            //Log.d("Insert result", "res: "+res);

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

    //################################## STORAGE PERMISSION ####################################################################3


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

    //############################################### on back pressed exit ########################################




    @Override
    public void onBackPressed() {

        exitApp();

        //super.onBackPressed();
    }

    public void exitApp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = builder.create();

        alertDialog.show();
    }
}
