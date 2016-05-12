package com.example.icarus.ikdc;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by donovan on 3/7/15.
 */
public class GridViewItem implements Serializable
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
