package com.example.icarus.ikdc;

import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.icarus.ikdc.database.DataAccessObject;

import java.io.File;
import java.io.IOException;


public class Audio extends AppCompatActivity {

    private DataAccessObject myData;

    private static final String AUDIO_RECORDER_FILE_EXT_3GP = ".3gp";
    private static final String AUDIO_RECORDER_FILE_EXT_MP4 = ".mp4";
    private static final String AUDIO_RECORDER_FOLDER = "/IKDC/commonStorage/audio";
    String filepath = Environment.getExternalStorageDirectory().getPath();
    File file = new File(filepath, AUDIO_RECORDER_FOLDER);
    private FrameLayout container;
    private ImageButton bt1;
    private ImageButton bt2;
    private MediaRecorder recorder = null;
    private int currentFormat = 0;
    private int output_formats[] = { MediaRecorder.OutputFormat.MPEG_4,
            MediaRecorder.OutputFormat.THREE_GPP };
    private String file_exts[] = { AUDIO_RECORDER_FILE_EXT_MP4,
            AUDIO_RECORDER_FILE_EXT_3GP };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);

        myData = new DataAccessObject(this);
        myData.open();

        setButtonHandlers();
        enableButtons(false);
        //setFormatButtonCaption();
        bt1 = (ImageButton) findViewById(R.id.saveClip);
        bt2 = (ImageButton) findViewById(R.id.discardClip);
        container = (FrameLayout) findViewById(R.id.btnCont);

        ImageButton backButton = (ImageButton) findViewById(R.id.back_button);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        View.OnClickListener save = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent backHome = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(backHome);
            }
        };

        bt1.setOnClickListener(save);

        View.OnClickListener discard = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent delete = new Intent(getApplicationContext(), MainActivity.class);
                myData.close();
                startActivity(delete);
            }
        };

        bt2.setOnClickListener(discard);

    }

    private void setButtonHandlers() {
        ((Button) findViewById(R.id.record)).setOnClickListener(btnClick);
        ((Button) findViewById(R.id.stop)).setOnClickListener(btnClick);
       // ((Button) findViewById(R.id.btnFormat)).setOnClickListener(btnClick);
    }

    private void enableButton(int id, boolean isEnable) {
        ((Button) findViewById(id)).setEnabled(isEnable);
    }

    private void enableButtons(boolean isRecording) {
        enableButton(R.id.record, !isRecording);
        //enableButton(R.id.btnFormat, !isRecording);
        enableButton(R.id.stop, isRecording);
    }

    /*private void setFormatButtonCaption() {
        ((Button) findViewById(R.id.btnFormat))
                .setText(getString(R.string.audio_format) + " ("
                        + file_exts[currentFormat] + ")");
    }*/

    private String getFilename() {


        if (!file.exists()) {
            file.mkdirs();
        }

        int audNum = myData.retrieveAudioNum() + 1;

        return (file.getAbsolutePath() + "/" + "rec_"+ audNum + file_exts[currentFormat]);
    }

    private void deleteFile(File delFile) {
        if(delFile == null) {
            return;
        }
        final File file2 = new File(delFile.getAbsolutePath());
        delFile = null;
        new Thread() {
            @Override
            public void run() {
                super.run();
                if(file2.exists()) {
                    file2.delete();
                }
            }
        }.start();
    }

    private void startRecording() {
        recorder = new MediaRecorder();

        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(output_formats[currentFormat]);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile(getFilename());

        int audNum = myData.retrieveAudioNum() + 1;
        myData.createCAudio("rec_"+audNum + file_exts[currentFormat], getFilename());

        recorder.setOnErrorListener(errorListener);
        //recorder.setOnInfoListener(infoListener);

        try {
            recorder.prepare();
            recorder.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopRecording() {
        if (null != recorder) {
            recorder.stop();
            recorder.reset();
            recorder.release();
            recorder = null;
        }
    }

    /*private void displayFormatDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String formats[] = { "MPEG 4", "3GPP" };

        builder.setTitle(getString(R.string.choose_format_title))
                .setSingleChoiceItems(formats, currentFormat,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                currentFormat = which;
                                setFormatButtonCaption();

                                dialog.dismiss();
                            }
                        }).show();
    }*/

    private MediaRecorder.OnErrorListener errorListener = new MediaRecorder.OnErrorListener() {
        @Override
        public void onError(MediaRecorder mr, int what, int extra) {
            Toast.makeText(Audio.this,
                    "Error: " + what + ", " + extra, Toast.LENGTH_SHORT).show();
        }
    };

    /*private MediaRecorder.OnInfoListener infoListener = new MediaRecorder.OnInfoListener() {
        @Override
        public void onInfo(MediaRecorder mr, int what, int extra) {
            Toast.makeText(Audio.this,
                    "Warning: " + what + ", " + extra, Toast.LENGTH_SHORT)
                    .show();
        }
    };*/

    private View.OnClickListener btnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.record: {
                    Toast.makeText(Audio.this, "Start Recording",
                            Toast.LENGTH_SHORT).show();

                    enableButtons(true);
                    startRecording();

                    break;
                }
                case R.id.stop: {
                    Toast.makeText(Audio.this, "Stop Recording",
                            Toast.LENGTH_SHORT).show();
                    enableButtons(false);
                    stopRecording();
                    container.setVisibility(View.GONE);
                    bt1.setVisibility(View.VISIBLE);
                    bt2.setVisibility(View.VISIBLE);

                    break;
                }


            }
        }
    };
}
