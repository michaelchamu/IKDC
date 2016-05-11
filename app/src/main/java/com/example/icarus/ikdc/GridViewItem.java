package com.example.icarus.ikdc;

import android.graphics.Bitmap;

/**
 * Created by donovan on 3/7/15.
 */
public class GridViewItem
{
    final String name;
    final String absoluteLocation;
    final Bitmap bm;

    GridViewItem(String name, String absoluteLocation, Bitmap bm)
    {
        this.name = name;
        this.absoluteLocation = absoluteLocation;
        this.bm = bm;
    }

    public String getAbsolutePath(){
        return this.absoluteLocation;
    }
}
