package com.example.icarus.ikdc;


import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Toast;

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

public class OrganiseActivity extends Activity {

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        myData = new DataAccessObject(this);
        myData.open();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organise);

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
    public void onBackPressed(){
        myData.close();
        /*Intent gal = new Intent(getApplicationContext(), Gallery.class);
        startActivity(gal);*/
        finish();
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

    public void itemTransfer(GridViewItem element){
        try{
            elements.add(element);
            final GridViewAdapter storyAdapter = new GridViewAdapter(this, elements);

            storyGallery.setAdapter(storyAdapter);

            storyGallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                    elements.remove(pos);
                    storyAdapter.removeItem(pos);
                    storyAdapter.notifyDataSetChanged();
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
