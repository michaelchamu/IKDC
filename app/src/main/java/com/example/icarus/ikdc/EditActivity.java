package com.example.icarus.ikdc;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.media.MediaPlayer;
import android.os.Environment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.icarus.ikdc.database.DataAccessObject;
import com.example.icarus.ikdc.database.Storage;
import com.example.icarus.ikdc.database.Story;
import com.example.icarus.ikdc.database.Thumbnail;
import com.example.icarus.ikdc.view.SlidingTabLayout;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import static android.media.ThumbnailUtils.createVideoThumbnail;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class EditActivity extends ActionBarActivity {

    private final String[] imageExtensions =  new String[] {"jpg", "png", "gif","jpeg"};
    private final String[] videoExtensions =  new String[] {"3gp"};
    private final String[] audioExtensions =  new String[] {"mp4"};
    private final String[] textExtensions =  new String[] {"text"};
    private DataAccessObject myData;

    private SlidingTabLayout slidingTabLayout;
    private ViewPager viewPager;
    private ArrayList<Fragment> fragments;
    private ActionTabsViewPagerAdapter myViewPageAdapter;
    GridView storyGallery;
    List<GridViewItem> elements = new ArrayList<GridViewItem>();

    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            /*mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);*/
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        myData = new DataAccessObject(this);
        myData.open();



        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);


        // Set up the user interaction to manually show or hide the system UI.
        /*mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });*/

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);

        Bundle extras = getIntent().getExtras();
        if(extras!=null)
        {
            fetchGallery(extras.getInt("GalleryID"));
            Toast.makeText(getApplicationContext(), extras.getString("GalleryLocation"), Toast.LENGTH_SHORT).show();

        }

        storyGallery =  (GridView) findViewById(R.id.newStoryGrid);
        // Define SlidingTabLayout (shown at top)
        // and ViewPager (shown at bottom) in the layout.
        // Get their instances.
        slidingTabLayout = (SlidingTabLayout) findViewById(R.id.tab);
        viewPager = (ViewPager) findViewById(R.id.viewpager);

        // create a fragment list in order.
        fragments = new ArrayList<Fragment>();
        fragments.add(new VideoFragment());
        fragments.add(new ImageFragment());
        fragments.add(new AudioFragment());
        fragments.add(new TextFragment());

        // use FragmentPagerAdapter to bind the slidingTabLayout (tabs with different titles)
        // and ViewPager (different pages of fragment) together.
        myViewPageAdapter =new ActionTabsViewPagerAdapter(getFragmentManager(),
                fragments);
        viewPager.setAdapter(myViewPageAdapter);

        // make sure the tabs are equally spaced.
        slidingTabLayout.setDistributeEvenly(true);
        slidingTabLayout.setViewPager(viewPager);

        ImageButton saveButton = (ImageButton) findViewById(R.id.imageButton);
        ImageButton cancelButton = (ImageButton) findViewById(R.id.imageButton2);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int storyId = myData.retrieveStoryNum() + 1;
                long thumbNum = myData.retrieveThumbNum() + 1;

                int flag = 0, count = 0;

                while (flag==0){

                    if (count == elements.size()){
                        flag = 1;
                        Toast.makeText(getApplicationContext(), "Please add image for Thumbnail!", Toast.LENGTH_SHORT).show();
                    }else{
                        if (exttest(getExt(elements.get(count).name)) == 1){
                            Thumbnail thisThumb = new Thumbnail();
                            Story thisStory = new Story(0, storyId + " - Story");
                            File thmbSource = new File(elements.get(count).absoluteLocation);
                            File thmbDest = new File(Environment.getExternalStorageDirectory() + "/IKDC/thumbnail/" + thumbNum + " - thmb.jpg");

                            try{
                                copyFile(thmbSource, thmbDest);
                                storyId =(int) myData.createStory(thisStory);
                                myData.createThmb(thumbNum + " - thmb.jpg", storyId);
                            } catch (Exception e){
                                Toast.makeText(getApplicationContext(), "THUMBNAIL TRANSFER FAILED: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }


                            flag = 2;
                        }
                    }
                    count++;
                }

                if (flag == 2){
                    File destination = new File(Environment.getExternalStorageDirectory() + "/IKDC/" + storyId + " - Story");
                    File sub1 = new File(Environment.getExternalStorageDirectory() + "/IKDC/" + storyId + " - Story/images");
                    File sub2 = new File(Environment.getExternalStorageDirectory() + "/IKDC/" + storyId + " - Story/audio");
                    File sub3 = new File(Environment.getExternalStorageDirectory() + "/IKDC/" + storyId + " - Story/text");
                    File sub4 = new File(Environment.getExternalStorageDirectory() + "/IKDC/" + storyId + " - Story/videos");
                    destination.mkdir();
                    sub1.mkdir();
                    sub2.mkdir();
                    sub3.mkdir();
                    sub4.mkdir();

                    int actNum = myData.getActivityCount(storyId);

                    for(GridViewItem elmnt : elements){
                        //Toast.makeText(getApplicationContext(), "ITEM NAME: " + elmnt.name, Toast.LENGTH_LONG).show();

                        com.example.icarus.ikdc.database.Activity thisActivity = new com.example.icarus.ikdc.database.Activity();

                        thisActivity.setId(0);
                        thisActivity.setStory_id(storyId);

                        Storage thisStorage = new Storage();
                        thisStorage.setFile_name(elmnt.name);
                        thisStorage.setFile_path(elmnt.getAbsolutePath());

                        thisActivity.setStory_id(myData.createStorage(thisStorage));

                        myData.createActivity(thisActivity);

                        if (exttest(getExt(elmnt.name)) == 1){

                            File dest = new File(destination.getAbsolutePath() + "/images/" + actNum + " - img.jpg");

                            File source = new File(elmnt.getAbsolutePath());

                            try{
                                copyFile(source, dest);
                            }catch(Exception e){
                                Toast.makeText(getApplicationContext(), "IMAGE TRANSFER FAILED: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }


                            //Create Activity - Copy File from commonStorage to Story
                        } else if (exttest(getExt(elmnt.name)) == 2){

                            File dest = new File(destination.getAbsolutePath() + "/videos/" + actNum + " - vid.3gp");

                            File source = new File(elmnt.getAbsolutePath());

                            try{
                                copyFile(source, dest);
                            }catch(Exception e){
                                Toast.makeText(getApplicationContext(), "VIDEO TRANSFER FAILED: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        } else if (exttest(getExt(elmnt.name)) == 3){
                            File dest = new File(destination.getAbsolutePath() + "/audio/" + actNum + " - aud.mp4");

                            File source = new File(elmnt.getAbsolutePath());

                            try{
                                copyFile(source, dest);
                            }catch(Exception e){
                                Toast.makeText(getApplicationContext(), "AUDIO TRANSFER FAILED: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        } else if (exttest(getExt(elmnt.name)) == 4){
                            File dest = new File(destination.getAbsolutePath() + "/text/" + actNum + " - text.text");

                            File source = new File(elmnt.getAbsolutePath());

                            try{
                                copyFile(source, dest);
                            }catch(Exception e){
                                Toast.makeText(getApplicationContext(), "TEXT TRANSFER FAILED: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                        actNum++;
                    }

                }

                /*Intent gal = new Intent(getApplicationContext(), Gallery.class);
                startActivity(gal);*/
                finish();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent(getApplicationContext(), Gallery.class);
                startActivity(gallery);
                finish();
            }
        });
    }

    public void fetchGallery(int id){
        if(id != 0) {
            String ExternalStorageDirectoryPath = Environment
                    .getExternalStorageDirectory()
                    .getAbsolutePath();

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

            storyGallery = (GridView) findViewById(R.id.newStoryGrid);
            storyGallery.setAdapter(new GridViewAdapter(this, gridItems));
            elements = gridItems;
            storyGallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {

                    Toast.makeText(getApplicationContext(), elements.get(pos).name, Toast.LENGTH_SHORT).show();

                    if (elements.get(pos).absoluteLocation == Environment.getExternalStorageDirectory() + "/IKDC/" + id + " - Story/images/"+elements.get(pos).name)
                    {
                        Toast.makeText(getApplicationContext(), "This File already exists : " + elements.get(pos).absoluteLocation, Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }
    }

    public static void copyFile(File sourceFile, File destFile) throws IOException {
        if(!destFile.exists()) {
            destFile.createNewFile();
        }

        FileChannel origin = null;
        FileChannel destination = null;
        try {
            origin = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();

            long count = 0;
            long size = origin.size();
            while((count += destination.transferFrom(origin, count, size-count))<size);
        }
        finally {
            if(origin != null) {
                origin.close();
            }
            if(destination != null) {
                destination.close();
            }
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button.
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    public void itemTransfer(final GridViewItem element){
        try{
            elements.add(element);
            final GridViewAdapter storyAdapter = new GridViewAdapter(this, elements);

            storyGallery.setAdapter(storyAdapter);

            storyGallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                    //elements.remove(pos);

                    File flag = new File(elements.get(pos).absoluteLocation);

                    if (flag.exists())
                    {
                        Toast.makeText(getApplicationContext(), "This File already exists : " + elements.get(pos).absoluteLocation, Toast.LENGTH_SHORT).show();
                    }

                    //storyAdapter.removeItem(pos);
                    //storyAdapter.notifyDataSetChanged();
                }
            });

        } catch (Exception e){
            Toast.makeText(this, "An error was experienced!", Toast.LENGTH_SHORT).show();
        }
    }

    public String getExt(String filepath){
        String ext = "";

        int i = filepath.lastIndexOf('.');
        if (i > 0) {
            ext = filepath.substring(i + 1);
        }

        return ext;
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

        return 5;
    }
}
