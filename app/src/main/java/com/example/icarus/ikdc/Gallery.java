package com.example.icarus.ikdc;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.icarus.ikdc.database.DataAccessObject;
import com.example.icarus.ikdc.database.Thumbnail;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.media.ThumbnailUtils.createVideoThumbnail;


public class Gallery extends ActionBarActivity {

    LinearLayout myGallery;
    GridView storyGallery;
    VideoView videoView;
    DataAccessObject datasource;
    private final String[] imageExtensions =  new String[] {"jpg", "png", "gif","jpeg"};
    private final String[] videoExtensions =  new String[] {"3gp"};
    private final String[] audioExtensions =  new String[] {"mp4"};
    private final String[] textExtensions =  new String[] {"text"};
    private MediaController mediaControls;
    public int position = 0;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        ImageButton addStory;

        addStory = (ImageButton)findViewById(R.id.addStory);
        addStory.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent addStory = new Intent(getApplicationContext(), OrganiseActivity.class);
                Toast.makeText(getApplicationContext(), "Processing...", Toast.LENGTH_SHORT).show();
                startActivity(addStory);
            }
        });

        datasource = new DataAccessObject(this);
        datasource.open();

        myGallery = (LinearLayout)findViewById(R.id.myGalleryView);
        storyGallery = (GridView)findViewById(R.id.storyGallery);

        List<Thumbnail> thmbs = datasource.getAllThumbnails();
        if (thmbs != null) {
            for (Thumbnail thmb : thmbs) {
                myGallery.addView(insertPhoto(Environment.getExternalStorageDirectory() + "/IKDC/thumbnail/" + thmb.getThmb_name(), thmb.getID()));
            }
        }



        datasource.close();
    }

    View insertPhoto(String path, final int id){
        SharedFunctions functions = new SharedFunctions();
        Bitmap bm = functions.decodeSampledBitmapFromUri(path, 150, 100);
        final LinearLayout layout = new LinearLayout(getApplicationContext());
        layout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        layout.setGravity(Gravity.CENTER);

        final ImageButton imageButton = new ImageButton(getApplicationContext());
        imageButton.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        imageButton.setPadding(30, 50, 30, 50);
        //imageButton.setBackgroundResource(R.drawable.selector);
        imageButton.setBackgroundResource(R.drawable.selector);
        imageButton.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageButton.setImageBitmap(bm);
        //imageButton.setFocusableInTouchMode(true);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //storyGallery.removeAllViews();

                int count = myGallery.getChildCount();

                storyGallery.removeAllViewsInLayout();
                fetchGallery(id);
            }
        });

        layout.addView(imageButton);
        return layout;
    }

    public void fetchGallery(final int id){
        if(id != 0) {
            String ExternalStorageDirectoryPath = Environment
                    .getExternalStorageDirectory()
                    .getAbsolutePath();

            videoView = (VideoView) findViewById(R.id.videoView);

            //String targetPath = ExternalStorageDirectoryPath + "/IKDC/";
            File targetDirector1 = new File(Environment.getExternalStorageDirectory() + "/IKDC/" + id + " - Story/images");
            File targetDirector2 = new File(Environment.getExternalStorageDirectory() + "/IKDC/" + id + " - Story/videos");
            File targetDirector3 = new File(Environment.getExternalStorageDirectory() + "/IKDC/" + id + " - Story/audio");
            File targetDirector4 = new File(Environment.getExternalStorageDirectory() + "/IKDC/" + id + " - Story/text");
            File[] files1 = targetDirector1.listFiles();
            File[] files2 = targetDirector2.listFiles();
            File[] files3 = targetDirector3.listFiles();
            File[] files4 = targetDirector4.listFiles();

            final List<GridViewItem> gridItems = new ArrayList<GridViewItem>();

            for (File file : files1) {

                /*
                 *New GridView implimentation start
                 */
                    SharedFunctions functions = new SharedFunctions();
                    gridItems.add(new GridViewItem(file.getPath(), file.getAbsolutePath(), functions.decodeSampledBitmapFromUri(file.getAbsolutePath(), 220, 220)));

                /*
                 *New GridView implimentation end
                 */
            }

            for (File thisFile : files2)
            {
                    try {

                        //GridViewItem testItem = new GridViewItem(file.getPath(), file.getAbsolutePath(), createVideoThumbnail(file.getAbsolutePath(), 0));
                        Bitmap testImage = createVideoThumbnail(thisFile.getAbsolutePath(), 0);

                        Bitmap overlay = BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_media_play);

                        Bitmap bmOverlay = Bitmap.createBitmap(testImage.getWidth(), testImage.getHeight(), testImage.getConfig());
                        Canvas canvas = new Canvas(bmOverlay);
                        canvas.drawBitmap(testImage, 0, 0, null);
                        canvas.drawBitmap(overlay, 125, 80, null);

                        gridItems.add(new GridViewItem(thisFile.getPath(), thisFile.getAbsolutePath(), bmOverlay));

                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(),
                                "File - Error: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }

            }

            for (File file : files3) {

                /*
                 *New GridView implimentation start
                 */
                Bitmap icon = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.audio);
                gridItems.add(new GridViewItem(file.getPath(), file.getAbsolutePath(), icon));

                /*
                 *New GridView implimentation end
                 */
            }

            for (File file : files4) {

                /*
                 *New GridView implimentation start
                 */

                Bitmap icon = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.text);
                gridItems.add(new GridViewItem(file.getPath(), file.getAbsolutePath(), icon));

                /*
                 *New GridView implimentation end
                 */
            }
            /*else
                {
                    Bitmap overlay = BitmapFactory.decodeResource(getResources(), R.drawable.play);
                    Bitmap finalImage = Bitmap.createScaledBitmap(overlay, 220, 220, false);

                    gridItems.add(new GridViewItem(file.getPath(), file.getAbsolutePath(), finalImage));
                }*/
            storyGallery = (GridView) findViewById(R.id.storyGallery);
            storyGallery.setAdapter(new GridViewAdapter(this, gridItems));
            storyGallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                    //GridViewItem galleryItem = new GridViewItem();
                    //storyGallery.getAdapter().getItem(position);
                    String ext = null;

                    int i = gridItems.get(pos).absoluteLocation.lastIndexOf('.');
                    if (i > 0) {
                        ext = gridItems.get(pos).absoluteLocation.substring(i + 1);
                    }

                    if (exttest(ext) == 2 || exttest(ext) == 3) {
                        if (mediaControls == null) {
                            mediaControls = new MediaController(Gallery.this);
                            mediaControls.setMinimumWidth(506);
                        }
                        // create a progress bar while the video filerca is loading
                        progressDialog = new ProgressDialog(Gallery.this);
                        // set a message for the progress bar
                        progressDialog.setMessage("Loading...");
                        //set the progress bar not cancelable on users' touch
                        progressDialog.setCancelable(true);
                        // show the progress bar
                        progressDialog.show();
                        try {
                            //set the media controller in the VideoView
                            videoView.setMediaController(mediaControls);
                            //set the uri of the video to be played

                            videoView.setVideoPath(gridItems.get(pos).absoluteLocation.toString());
                        } catch (Exception e) {
                            Log.e("Error: --", e.getMessage());
                        }
                        videoView.requestFocus();
                        //we also set an setOnPreparedListener in order to know when the video file is ready for playback
                        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            public void onPrepared(MediaPlayer mediaPlayer) {
                                // close the progress bar and play the video
                                progressDialog.dismiss();
                                //if we have a position on savedInstanceState, the video playback should start from here
                                videoView.seekTo(position);
                                if (position == 0) {
                                    videoView.start();
                                } else {
                                    //if we come from a resumed activity, video playback will be paused
                                    videoView.pause();
                                }
                            }
                        });
                    }

                    Toast.makeText(getApplicationContext(),
                            "File - Clicked: " + gridItems.get(pos).absoluteLocation,
                            Toast.LENGTH_SHORT).show();
                }
            });

            ImageButton editGallery;
            final String location = Environment.getExternalStorageDirectory() + "/IKDC/" + id;

            editGallery = (ImageButton) findViewById(R.id.editGallery);
            editGallery.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Intent editGallery = new Intent(getApplicationContext(), EditActivity.class);
                    Toast.makeText(getApplicationContext(), "Opening Edit Screen...", Toast.LENGTH_SHORT).show();
                    editGallery.putExtra("GalleryLocation", location);
                    editGallery.putExtra("GalleryID", id);
                    startActivity(editGallery);
                }
            });
        }
    }

    public int exttest(String ext)
    {
        for (String extension : imageExtensions)
        {
            if (ext.equals(extension))
            {
                return 1;
            }
        }

        for (String extension : videoExtensions)
        {
            if (ext.equals(extension))
            {
                return 2;
            }
        }

        for (String extension : audioExtensions)
        {
            if (ext.equals(extension))
            {
                return 3;
            }
        }

        for (String extension : textExtensions)
        {
            if (ext.equals(extension))
            {
                return 4;
            }
        }

        return 3;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_gallery, menu);
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
