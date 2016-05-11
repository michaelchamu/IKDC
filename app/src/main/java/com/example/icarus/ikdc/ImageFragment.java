package com.example.icarus.ikdc;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by donovan on 4/26/15.
 */
public class ImageFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_images, container, false);

        //Tis is where the call to fetch gallery needs to be

        final List<GridViewItem> images = new ArrayList<GridViewItem>(fetchGallery());

        GridView storyGallery = (GridView) rootView.findViewById(R.id.imageGrid);
        storyGallery.setAdapter(new GridViewAdapter(getActivity().getApplicationContext(), images));
        storyGallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {

                ((OrganiseActivity)getActivity()).itemTransfer(images.get(pos));

            }
        }); //The Above commented code needs to deliver the result of fetch gallery into the actual view layout for processing.

        return rootView;
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

}
