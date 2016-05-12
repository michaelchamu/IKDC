package com.example.icarus.ikdc;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.VideoView;

public class VideoPopUp extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_pop_up);
        final Intent intent = getIntent();
        VideoView videoView = (VideoView) findViewById(R.id.videoView2);
        //intent.getExtras();
        Uri videoPath = Uri.parse(intent.getStringExtra("uri"));
        final Bitmap item = (Bitmap) intent.getParcelableExtra("video");
        videoView.setVideoURI(videoPath);
        videoView.requestFocus();
        videoView.start();

        View.OnClickListener save = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent saveVid = new Intent(getApplicationContext(), VideoFragment.class);
                saveVid.putExtra("bitmap", item);
                startActivity(saveVid);
            }
        };
        ImageButton sound = (ImageButton) findViewById(R.id.ibUse);
        sound.setOnClickListener(save);
    }

}
