package com.example.icarus.ikdc;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.VideoView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.media.ThumbnailUtils.createVideoThumbnail;

/**
 * Created by donovan on 4/26/15.
 */
public class VideoFragment extends Fragment {

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_videos, container, false);

        //Tis is where the call to fetch gallery needs to be

        final List<GridViewItem> videos = new ArrayList<GridViewItem>(fetchGallery());
        //videos = fetchGallery();

        GridView storyGallery = (GridView) rootView.findViewById(R.id.videoGrid);
        storyGallery.setAdapter(new GridViewAdapter(getActivity().getApplicationContext(), videos));
        storyGallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {

<<<<<<< HEAD
               // super.onItemClick(parent, view, pos, id);
                Intent intent = new Intent(getActivity().getApplicationContext(), VideoPopUp.class);
                intent.putExtra("video", videos.get(pos).bm);
                intent.putExtra("uri",Environment.getExternalStorageDirectory() + "/IKDC/commonStorage/videos/" + videos.get(pos).name );
                startActivity(intent);

               // ((OrganiseActivity)getActivity()).itemTransfer(videos.get(pos));
=======
                try
                {
                    ((OrganiseActivity)getActivity()).itemTransfer(videos.get(pos));
                }
                catch (Exception e)
                {
                    ((EditActivity)getActivity()).itemTransfer(videos.get(pos));
                }
>>>>>>> 12a682416e38a444663ebed54c0df8154b3bd608

            }
        }); //The Above commented code needs to deliver the result of fetch gallery into the actual view layout for processing.

        return rootView;
    }

    private List<GridViewItem> fetchGallery(){
        String ExternalStorageDirectoryPath = Environment
                .getExternalStorageDirectory()
                .getAbsolutePath();

        String targetPath = ExternalStorageDirectoryPath + "/IKDC/";
        File targetDirector = new File(Environment.getExternalStorageDirectory() + "/IKDC/commonStorage/videos");
        File[] files = targetDirector.listFiles();

        final List<GridViewItem> gridItems = new ArrayList<GridViewItem>();

        for (File file : files) {
            try {

                //GridViewItem testItem = new GridViewItem(file.getPath(), file.getAbsolutePath(), createVideoThumbnail(file.getAbsolutePath(), 0));
                Bitmap testImage = createVideoThumbnail(file.getAbsolutePath(), 0);

                Bitmap overlay = BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_media_play);

                /*Canvas canvas = new Canvas(testImage);
                Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
                canvas.drawBitmap(testImage, 0, 0, paint);
                canvas.drawBitmap(overlay, 0, 0, paint);*/

                Bitmap bmOverlay = Bitmap.createBitmap(testImage.getWidth(), testImage.getHeight(), testImage.getConfig());
                Canvas canvas = new Canvas(bmOverlay);
                canvas.drawBitmap(testImage, 0, 0, null);
                canvas.drawBitmap(overlay, 125, 80, null);

                gridItems.add(new GridViewItem(file.getName(), file.getAbsolutePath(), bmOverlay));

            } catch (Exception e) {

            }

            //storyGallery.addView(layout);

        }

        return gridItems;

    }

}
