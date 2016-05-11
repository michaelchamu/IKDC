package com.example.icarus.ikdc;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import com.example.icarus.ikdc.database.DataAccessObject;

import java.io.File;

public class MainActivity extends ActionBarActivity {
    DataAccessObject datasource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        datasource = new DataAccessObject(this);
        datasource.open();

        File externCheck = new File(Environment.getExternalStorageDirectory() + "/IKDC");

        if(!externCheck.isDirectory()){
            File externalDir = new File(Environment.getExternalStorageDirectory().toString() + "/IKDC");
            File thmbDir = new File(Environment.getExternalStorageDirectory().toString() + "/IKDC/thumbnail");
            File commonStorage = new File(Environment.getExternalStorageDirectory().toString() + "/IKDC/commonStorage");
            File vids = new File (Environment.getExternalStorageDirectory().toString() + "/IKDC/commonStorage/videos");
            File img = new File (Environment.getExternalStorageDirectory().toString() + "/IKDC/commonStorage/images");
            File aud = new File (Environment.getExternalStorageDirectory().toString() + "/IKDC/commonStorage/audio");
            File text = new File (Environment.getExternalStorageDirectory().toString() + "/IKDC/commonStorage/text");
            externalDir.mkdirs();
            thmbDir.mkdirs();
            commonStorage.mkdirs();
            vids.mkdirs();
            img.mkdirs();
            aud.mkdirs();
            text.mkdirs();
        }

        OnClickListener audio = new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startAudio = new Intent(getApplicationContext(), Audio.class);
                startActivity(startAudio);
            }
        };
        ImageButton sound = (ImageButton) findViewById(R.id.button6);
        sound.setOnClickListener(audio);

        OnClickListener camera = new OnClickListener() {
            @Override
            public void onClick(View view) {
            Intent intent = new Intent(getApplicationContext(), IKDCStillCamera.class);
             startActivity(intent);
            }
        };
        ImageButton button = (ImageButton) findViewById(R.id.button);
        button.setOnClickListener(camera);

        OnClickListener video = new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startVideo = new Intent(getApplicationContext(),Video.class);
                startActivity(startVideo);
            }
        };
        ImageButton button2 = (ImageButton) findViewById(R.id.button5);
        button2.setOnClickListener(video);

        OnClickListener button4 =new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent starttyping = new Intent(getApplicationContext(), TextEditor.class);
                startActivity(starttyping);
            }
        };
        ImageButton starttext = (ImageButton) findViewById(R.id.button2);
        starttext.setOnClickListener(button4);

        ImageButton galleryButton = (ImageButton) findViewById(R.id.button3);
        galleryButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent(getApplicationContext(), Gallery.class);
                startActivity(gallery);
            }
        });

        ImageButton draw = (ImageButton) findViewById(R.id.button4);
        draw.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent draw = new Intent(getApplicationContext(), Draw.class);
                startActivity(draw);
            }
        });
      datasource.close();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
