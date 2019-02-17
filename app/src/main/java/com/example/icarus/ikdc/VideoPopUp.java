package com.example.icarus.ikdc;

import android.content.Intent;
import android.view.View.OnClickListener;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.VideoView;

public class VideoPopUp extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_pop_up);
        final VideoFragment fragment = new VideoFragment();
        Intent intent = getIntent();
        VideoView videoView = (VideoView) findViewById(R.id.videoView2);
        //intent.getExtras();
        Toast.makeText(getApplicationContext(),
                "Video obtained",
                Toast.LENGTH_SHORT).show();
        Uri videoPath = Uri.parse(intent.getStringExtra("uri"));
        final int videoPosition  = (int) intent.getIntExtra("videoPosition", 0);
        videoView.setVideoURI(videoPath);
        videoView.requestFocus();
        videoView.start();

        ImageButton save = (ImageButton) findViewById(R.id.saveFromPreview);

        save.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent saveVid = new Intent(getApplicationContext(), VideoFragment.class);
                saveVid.putExtra("position", videoPosition);
                //fragment.saveItem(videoPosition);
                startActivity(saveVid);
            }
        });
    }

}
