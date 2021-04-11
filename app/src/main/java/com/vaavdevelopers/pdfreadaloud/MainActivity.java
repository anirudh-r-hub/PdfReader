package com.vaavdevelopers.pdfreadaloud;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.regex.Pattern;
import java.io.File;


public class MainActivity extends AppCompatActivity {

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
                new MaterialFilePicker()
                        .withActivity(MainActivity.this)

                        .withRootPath("/storage/")

                        .withFilter(Pattern.compile(".*\\.pdf$"))
                        .withFilterDirectories(false)
                        .withRequestCode(1000)
                        .withTitle("File Picker")
                        .withHiddenFiles(true) // Show hidden files and folders
                        .start();
                //
            }
        });


        //*******************List view code****************************

        fetchRecent();

        listview_recentfiles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(MainActivity.this, PDFViewActivity.class);
                i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                File file = new File(listoffiles.get(position));

                if(file.exists())
                {
                    Toast.makeText(MainActivity.this, "Opening "+file.getName()+"...", Toast.LENGTH_SHORT).show();
                    //Create the bundle
                    Bundle bundle = new Bundle();
                    bundle.putString("filePath",listoffiles.get(position)); // putting the file path in bundle
                    i.putExtras(bundle);

                    startActivity(i);

                } else {

                    Toast.makeText(MainActivity.this, "File not found!", Toast.LENGTH_SHORT).show();
                }
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
                Toast.makeText(this, "All entries removed!", Toast.LENGTH_SHORT).show();
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
            Intent i = new Intent(MainActivity.this, PDFViewActivity.class);
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
