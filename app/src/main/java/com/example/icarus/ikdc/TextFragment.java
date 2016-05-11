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
public class TextFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_text, container, false);

        final List<GridViewItem> texts = new ArrayList<GridViewItem>(fetchGallery());

        GridView storyGallery = (GridView) rootView.findViewById(R.id.textGrid);
        storyGallery.setAdapter(new GridViewAdapter(getActivity().getApplicationContext(), texts));
        storyGallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {

                ((OrganiseActivity)getActivity()).itemTransfer(texts.get(pos));

            }
        }); //The Above commented code needs to deliver the result of fetch gallery into the actual view layout for processing.

        return rootView;
    }

    private List<GridViewItem> fetchGallery(){
        String ExternalStorageDirectoryPath = Environment
                .getExternalStorageDirectory()
                .getAbsolutePath();

        String targetPath = ExternalStorageDirectoryPath + "/IKDC/";
        File targetDirector = new File(Environment.getExternalStorageDirectory() + "/IKDC/commonStorage/text");
        File[] files = targetDirector.listFiles();

        final List<com.example.icarus.ikdc.GridViewItem> gridItems = new ArrayList<com.example.icarus.ikdc.GridViewItem>();

        for (File file : files) {

            Bitmap icon = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.text);
            gridItems.add(new com.example.icarus.ikdc.GridViewItem(file.getName(), file.getAbsolutePath(), icon));

            //storyGallery.addView(layout);

        }

        return gridItems;

    }

}
