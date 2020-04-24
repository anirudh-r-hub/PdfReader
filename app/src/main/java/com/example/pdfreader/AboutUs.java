package com.example.pdfreader;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import java.util.Calendar;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

public class AboutUs extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_about_us);
        String description = "This is an application to read aloud any PDF from your device with Play-Pause-Resume facility." +
                "You can also select options such as language, pitch and speed of the reader of voice. " +
                "If you like this app, please share it with your friends.";

        View aboutPage = new AboutPage(this)
                .isRTL(false)
                .setDescription(description)
                .setImage(R.drawable.about_icon_link)
                .addItem(new Element().setTitle("Version 1.0"))
                .addGroup("Connect with me")
                .addEmail("abc@gmail.com")
                .addGitHub("github link")
                .addYoutube("youtube link")
                .addItem(createCopyright())
                .create();

        setContentView(aboutPage);
    }

    private Element createCopyright() {

        Element copyright = new Element();
        String copyrightString = String.format("Copyright 2020 by Star Android developers");

        copyright.setTitle(copyrightString);
        copyright.setIconDrawable(R.drawable.about_icon_email);

        copyright.setGravity(Gravity.CENTER);

        return copyright;
    }
}
