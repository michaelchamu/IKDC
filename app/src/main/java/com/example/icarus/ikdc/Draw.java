package com.example.icarus.ikdc;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.icarus.ikdc.database.DataAccessObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;


public class Draw extends AppCompatActivity implements View.OnClickListener {

    private DrawingView drawView;
    private ImageButton currPaint, newBtn ,eraseBtn, drawBtn,saveBtn, backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_draw);
            drawView = (DrawingView) findViewById(R.id.drawing);
            LinearLayout paintLayout = (LinearLayout) findViewById(R.id.paint_colors);

            currPaint = (ImageButton) paintLayout.getChildAt(0);
            currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));

            drawBtn = (ImageButton) findViewById(R.id.drawing);
            drawBtn.setOnClickListener(this);

            eraseBtn = (ImageButton) findViewById(R.id.rub);
            eraseBtn.setOnClickListener(this);

            newBtn = (ImageButton) findViewById(R.id.new_button);
            newBtn.setOnClickListener(this);

            saveBtn = (ImageButton)findViewById(R.id.save);
            saveBtn.setOnClickListener(this);

            backButton = (ImageButton) findViewById(R.id.back_button);
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }
        catch(Exception ex)
        {
            //Toast.makeText(this, ex.toString(), Toast.LENGTH_LONG).show();
        }

        final List<GridViewItem> images = new ArrayList<GridViewItem>(fetchGallery());

        GridView storyGallery = (GridView) findViewById(R.id.drawGrid);
        storyGallery.setAdapter(new GridViewAdapter(this, images));
        storyGallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {

                drawView.backgroundChange(images.get(pos).getAbsolutePath());

            }
        }); //The Above commented code needs to deliver the result of fetch gallery into the actual view layout for processing.
    }

    private List<GridViewItem> fetchGallery(){
        String ExternalStorageDirectoryPath = Environment
                .getExternalStorageDirectory()
                .getAbsolutePath();

        String targetPath = ExternalStorageDirectoryPath + "/IKDC/";
        File targetDirector = new File(Environment.getExternalStorageDirectory() + "/IKDC/commonStorage/images");
        File[] files = targetDirector.listFiles();

        final List<GridViewItem> gridItems = new ArrayList<GridViewItem>();

        for (File file : files) {

            gridItems.add(new GridViewItem(file.getName(), file.getAbsolutePath(), decodeSampledBitmapFromUri(file.getAbsolutePath(), 220, 220)));

            //storyGallery.addView(layout);

        }

        return gridItems;

    }

    public Bitmap decodeSampledBitmapFromUri(String path, int reqWidth, int reqHeight) {
        Bitmap bm = null;

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        bm = BitmapFactory.decodeFile(path, options);

        return bm;
    }

    public int calculateInSampleSize(

            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float)height / (float)reqHeight);
            } else {
                inSampleSize = Math.round((float)width / (float)reqWidth);
            }
        }

        return inSampleSize;
    }



    public void paintClicked(View view){
        //use chosen color
        if(view!=currPaint){
            //update color
            //retrieve tag set for every paint button
            drawView.setErase(false);
            ImageButton imgView = (ImageButton)view;
            String color = view.getTag().toString();
            drawView.setColor(color);
            imgView.setImageDrawable(getResources().getDrawable(R.drawable.paint));
            currPaint = (ImageButton)view;
        }
    }


    @Override
    public void onClick(View view){
        //respond to clicks
        if(view.getId() == R.id.drawing){
            //draw button clicked
            drawView.setErase(false);
        }
        else if(view.getId()==R.id.rub){
            //switchto erase
            drawView.setErase(true);

        }
        else if(view.getId() == R.id.new_button){
            //start new canvas
            drawView.setErase(false);
            drawView.startNew();
        }
        else if(view.getId()==R.id.save){
            //save drawing
            try{
                //save drawing
                drawView.setDrawingCacheEnabled(true);

                DataAccessObject myData = new DataAccessObject(getApplicationContext());

                myData.open();

                int imgNum = myData.retrieveImageNum() + 1;
                File file;
            /*String imgSaved = MediaStore.Images.Media.insertImage(
                    getContentResolver(),drawView.getDrawingCache(),UUID.randomUUID().toString()+".jpg","drawing");*/
                String root = Environment.getExternalStorageDirectory().toString();
                File myDir = new File(root + "/IKDC/commonStorage/images");
                myDir.mkdirs();
                String fname = "IMG_" + imgNum + ".jpg";
                file = new File(myDir, fname);
                if (file.exists())
                    file.delete();
                try {
                    FileOutputStream out = new FileOutputStream(file);
                    Bitmap bm = drawView.getDrawingCache();
                    bm.compress(Bitmap.CompressFormat.JPEG, 90, out);
                    out.flush();
                    out.close();
                } catch (Exception e) {
                    Toast unsavedToast = Toast.makeText(getApplicationContext(),
                            "Oops! Image could not be saved.", Toast.LENGTH_SHORT);
                    unsavedToast.show();
                }


                myData.createCImage(fname, file.getAbsolutePath());

                Toast savedToast = Toast.makeText(getApplicationContext(),
                        "Drawing saved to Gallery!", Toast.LENGTH_SHORT);
                savedToast.show();
                drawView.startNew();
                setContentView(R.layout.activity_draw);
            }catch(Exception ex)
            {
                Toast unsavedToast = Toast.makeText(getApplicationContext(),
                        "Oops! Image could not be saved.", Toast.LENGTH_SHORT);
                unsavedToast.show();
            }
            finally {
                finish();
            }


            drawView.destroyDrawingCache();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_draw, menu);
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
