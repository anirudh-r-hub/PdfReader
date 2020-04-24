package com.example.pdfreader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;

public class Book1 extends AppCompatActivity implements SettingsDialog.SettingsDialogListener {

    PDFView book1;
    private TextToSpeech text_to_speech;
    private static final String COLON = ":";
    boolean speak,stop,fullscr;
    EditText edit_goto;
    TextView text_totalpages;
    int number_of_pages;
    LinearLayout l1;
    Button btn_next,btn_prev,btn_goto;
    boolean stop_session_enabled;
    FloatingActionButton fab,stop_session;
    HashMap<String, String> map = new HashMap<String, String>();
    HashMap<String,String>map1=new HashMap<String, String>();
    Uri filePath;
    String filePath_str;
    float this_pitch, this_speed;
    int pitch_progress, speed_progress;
    int read_line=0;
    String[] stringParser;
    int selected_langage = 0;
    DatabaseHelper recentdb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book1);

        //night_mode = (Button)findViewById(R.id.btn_nightmode);
        text_totalpages = (TextView)findViewById(R.id.text_noofpages);
        stop_session=findViewById(R.id.stop_session);
        edit_goto = (EditText) findViewById(R.id.edit_goto);
        l1 = (LinearLayout) findViewById(R.id.l1);
        btn_next=(Button)findViewById(R.id.btn_next);
        btn_prev=(Button)findViewById(R.id.btn_prev);
        btn_goto=(Button)findViewById(R.id.btn_goto);

        stop_session_enabled = false;
        stop_session.setEnabled(false);
        book1 = (PDFView) findViewById(R.id.book1); // creating a view which will display the pdf
        fab = findViewById(R.id.fab);
        final FloatingActionButton full_screen = findViewById(R.id.fab_fullscreen);
        speak = true;
        stop = false;
        fullscr = false;
        this_pitch = this_speed = 0.5f;

        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "UniqueID");
        map1.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,"newUniqueID");

        //****************open the file and initialize tts**********************************************
        //book1.fromAsset("book1.pdf").load();

        //Get the bundle



        Bundle bundle = getIntent().getExtras();
        filePath = Uri.parse(bundle.getString("filePath")); //gving a filepath
        filePath_str = bundle.getString("filePath");

        recentdb = new DatabaseHelper(this);
        int recent_page = recentdb.getRecentPage(filePath_str);
        recentdb.close();

        final File file=new File(filePath.getPath());
        book1.fromFile(file)

                .enableDoubletap(true)
                .enableSwipe(true)
                .defaultPage(recent_page - 1)
                .scrollHandle(new DefaultScrollHandle(this, true))


                .onPageChange(new OnPageChangeListener() {
                    @Override
                    public void onPageChanged(int page, int pageCount) {
                        edit_goto.setText(new Integer(page+1).toString());
                    }
                })
                .load(); // put the pdf in the pdf view



        //Toast.makeText(Book1.this, ""+filePath.toString(), Toast.LENGTH_LONG).show();
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
                    full_screen.setForeground(getResources().getDrawable(R.drawable.ic_fullscreen_exit_black_24dp,null));
                    fullscr = true;


                } else
                {
                    //show views
                    l1.setVisibility(LinearLayout.VISIBLE);
                    //change icon
                    //full_screen.setImageResource(R.drawable.ic_fullscreen_black_24dp);
                    full_screen.setForeground(getResources().getDrawable(R.drawable.ic_fullscreen_black_24dp,null));
                    //change fullscr status
                    fullscr = false;
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

                if (stop_session_enabled == false) {

                    stop_session.setEnabled(true);
                    stop_session_enabled = true;
                    stop_session.setForeground(getResources().getDrawable(R.drawable.ic_stop_black,null));

                    if (edit_goto.getText().toString().equals("")) {
                        edit_goto.requestFocus();

                    } else {
                        int entered_page = Integer.parseInt(edit_goto.getText().toString());

                        fab.setImageResource(R.drawable.ic_pause);
                        read_pdf_file(entered_page);
                        speak = false;
                        stop = true;
                        btn_next.setEnabled(false);
                        btn_next.setForeground(getResources().getDrawable(R.drawable.ic_navigate_next_disabled,null));

                        btn_prev.setEnabled(false);
                        btn_prev.setForeground(getResources().getDrawable(R.drawable.ic_navigate_before_disabled,null));
                        btn_goto.setEnabled(false);
                        edit_goto.setEnabled(false);
                        //book1.setSwipeEnabled(false);
                        book1.setSwipeEnabled(false);
                    }


                } else {
                    if (edit_goto.getText().toString().equals("")) {
                        edit_goto.requestFocus();

                    } else {
                        int entered_page = Integer.parseInt(edit_goto.getText().toString());

                        if (speak) {

                            fab.setImageResource(R.drawable.ic_pause);
                            read_pdf_file(entered_page);
                            speak = false;
                            stop = true;
                            /*btn_next.setEnabled(false);
                            btn_prev.setEnabled(false);
                            btn_goto.setEnabled(false);
                            edit_goto.setEnabled(false);
                            book1.setSwipeEnabled(false);*/


                        } else if (stop) {
                            text_to_speech.stop();
                            fab.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                            stop = false;
                            speak = true;

                        }
                    }

                }
            }
        });

        stop_session.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speak=false;
                stop=true;
                text_to_speech.stop();
                fab.setImageResource(R.drawable.ic_speaker_phone_black_24dp);

                //fab.performClick();
                //btn_next.setEnabled(false);

                read_line=0;
                btn_next.setEnabled(true);
                btn_next.setForeground(getResources().getDrawable(R.drawable.ic_navigate_next_black_24dp,null));

                btn_prev.setEnabled(true);
                btn_prev.setForeground(getResources().getDrawable(R.drawable.ic_navigate_before_black_24dp,null));
                btn_goto.setEnabled(true);
                edit_goto.setEnabled(true);
                //book1.setSwipeEnabled(true);
                book1.setSwipeEnabled(true);

                stop_session.setEnabled(false);
                stop_session.setForeground(getResources().getDrawable(R.drawable.ic_stop_black_disabled_24dp,null));
                stop_session_enabled = false;


            }
        });




    }
//##############################################################################################################3

    // add recent page of the book
    @Override
    public void onBackPressed() {

        int current_page = Integer.parseInt(edit_goto.getText().toString());

        System.err.println("finalpath" + filePath_str+" current page : "+current_page);

        recentdb = new DatabaseHelper(this);

        recentdb.addRecentPage(filePath_str, current_page);

        recentdb.close();
        finish();

        //super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_settings, menu);
        return super.onCreateOptionsMenu(menu);
    }


    //##################################### SETTINGS MENU ##################################################################
    //##################################### SETTINGS MENU ##################################################################

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        final String[] listitems;


        switch (item.getItemId()) {
            case R.id.action_settings:

                Toast.makeText(this, "Settings selected", Toast.LENGTH_LONG).show();
                openSettingsDialog();
                return true;

            case R.id.action_language:

                Toast.makeText(this, "Select Language", Toast.LENGTH_SHORT).show();
                listitems = new String[]{"ENGLISH", "FRENCH","GERMAN","ITALIAN","ENGLISH (UK)","ENGLISH (CANADA)","FRENCH (CANADA)"};
                AlertDialog.Builder builder = new AlertDialog.Builder(Book1.this);
                builder.setTitle("Select Language for Reader");
                builder.setSingleChoiceItems(listitems, selected_langage, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selected_langage = which;
                        selectedItemListener(listitems[which]);

                    }
                });

                builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void selectedItemListener(String language) {
        Toast.makeText(this, "Selected language: "+language, Toast.LENGTH_SHORT).show();

        switch(language) {

            case "ENGLISH":
                Toast.makeText(this, "english selected", Toast.LENGTH_SHORT).show();
                text_to_speech.setLanguage(Locale.ENGLISH);
                break;

            case "FRENCH":
                Toast.makeText(this, "french selected", Toast.LENGTH_SHORT).show();

                text_to_speech.setLanguage(Locale.FRENCH);
                break;

            case "GERMAN":
                Toast.makeText(this, "german selected", Toast.LENGTH_SHORT).show();

                text_to_speech.setLanguage(Locale.GERMAN);

                break;

            case "ITALIAN":
                Toast.makeText(this, "italian selected", Toast.LENGTH_SHORT).show();

                text_to_speech.setLanguage(Locale.ITALIAN);

                break;

            case "ENGLISH (UK)":
                Toast.makeText(this, "English(UK) selected", Toast.LENGTH_SHORT).show();

                text_to_speech.setLanguage(Locale.UK);

                break;

            case "ENGLISH (CANADA)":
                Toast.makeText(this, "English (CANADA) selected", Toast.LENGTH_SHORT).show();

                text_to_speech.setLanguage(Locale.CANADA);

                break;

            case "FRENCH (CANADA)":
                Toast.makeText(this, "french (CANADA) selected", Toast.LENGTH_SHORT).show();

                text_to_speech.setLanguage(Locale.CANADA_FRENCH);
                break;



        }

    }

    // ######################################## VOICE SETTINGS DIALOG ###################################################
    // ######################################## VOICE SETTINGS DIALOG ###################################################

    public void openSettingsDialog() {
        SettingsDialog settingsDialog = new SettingsDialog(pitch_progress, speed_progress);
        settingsDialog.show(getSupportFragmentManager(), "settings_dialog");

    }


    @Override
    public void apply(int pitch, int speed) {
        pitch_progress = pitch;
        speed_progress = speed;

        //convert progrees into actual value
        this_pitch = (float) pitch_progress / 50;
        if (this_pitch < 0.1) this_pitch = 0.1f;
        this_speed = (float) speed_progress / 50;
        if (this_speed < 0.1) this_speed = 0.1f;

        text_to_speech.setPitch(this_pitch);
        text_to_speech.setSpeechRate(this_speed);


    }

    //################################ TEXT TO SPEECH FUNCTIONS ######################################################
    //################################ TEXT TO SPEECH FUNCTIONS ######################################################

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
                            public void onDone(final String utteranceId) {
                                if(read_line==stringParser.length) {
                                    runOnUiThread(new Runnable() {

                                        public void run() {
                                            //Toast.makeText(getApplicationContext(), ""+utteranceId, Toast.LENGTH_LONG).show();
                                            btn_next.performClick();
                                            speak = true;
                                            stop = false;
                                            read_line = 0;
                                            fab.performClick();

                                        }
                                    });

                                }
                                else
                                {
                                    runOnUiThread(new Runnable() {

                                        public void run() {
                                            //Toast.makeText(getApplicationContext(), ""+utteranceId, Toast.LENGTH_LONG).show();
                                            //btn_next.performClick();
                                            speak = true;
                                            stop = false;
                                            read_line++;
                                            fab.performClick();

                                        }
                                    });

                                }



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

    //************** speak *******************************************************************
    private void speak(String message)
    {
        if(Build.VERSION.SDK_INT>=21)
        {
            if(read_line==stringParser.length) {
                text_to_speech.speak(message, TextToSpeech.QUEUE_ADD, null, map.toString());
                //Toast.makeText(getApplicationContext(),"finished page",Toast.LENGTH_SHORT).show();
            }
            else
                text_to_speech.speak(message,TextToSpeech.QUEUE_ADD,null,map.toString());
        }
    }

    //************** read one page of pdf *******************************************************************
    public void read_pdf_file(int page_no) {
        try {

            File file = new File(filePath.toString());
            PdfReader pdfReader = new PdfReader(filePath.toString());


            //for(int i=page_no;i<=pdfReader.getNumberOfPages();i++) {

            // access the resource protected by this lock

            stringParser = PdfTextExtractor.getTextFromPage(pdfReader, page_no).replaceAll("\n"," ").split("\\.");
            //Toast.makeText(getApplicationContext(), stringParser, Toast.LENGTH_LONG).show();
            if(read_line<stringParser.length)
                speak("" + stringParser[read_line]);
            else
                speak("");
            //read_line++;


            //}
            //pdfReader.close();
            //outputTextView.setText(stringParser);


            //Toast.makeText(this,stringParser,Toast.LENGTH_LONG).show();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    //******************* destroy tts object **************************************
    @Override
    protected void onDestroy() {

        if(text_to_speech != null) {
            text_to_speech.stop();
            text_to_speech.shutdown();
        }
        super.onDestroy();
    }

    //################################### PAGE NAVIGATION ##########################################################
    //################################### PAGE NAVIGATION ##########################################################

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