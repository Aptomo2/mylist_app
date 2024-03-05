package com.example.mylist;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class About extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View aboutPage = new AboutPage(this)
                .isRTL(false)
                .setImage(R.mipmap.ic_launcher)
                .setDescription(getString(R.string.description))
                .addItem(new Element("Versi " + BuildConfig.VERSION_NAME, R.drawable.ic_info_24))
                .addGroup("Terhubung dengan saya")
                .addEmail("apsky.ap09@gmail.com", "Email")
                .addWebsite(getString(R.string.bg), "Website")
                .addFacebook(getString(R.string.fb), "Facebook")
                .addTwitter(getString(R.string.tw), "Twitter")
                .addInstagram(getString(R.string.ig), "Instagram")
                .addYoutube(getString(R.string.yt), "Youtube")
                .addItem(createCopyright())
                .create();
        setContentView(aboutPage);
    }
        private Element createCopyright() {
            Element copyright = new Element();
            String copyrightString = String.format("Copyright %d by Andang Pratomo", Calendar.getInstance().get(Calendar.YEAR));
            copyright.setTitle(copyrightString);
            copyright.setIconDrawable(R.drawable.copyright_24);
            copyright.setGravity(Gravity.CENTER);
            copyright.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(About.this,copyrightString,Toast.LENGTH_SHORT).show();
                }
            });
            return copyright;
    }
}