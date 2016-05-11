package com.example.icarus.ikdc;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by donovan on 3/7/15.
 */

/*
     * This is my declaration on the adapter innerclass that will make use of the SquareImageView to pass data to
     * the gridview.
     */
public class GridViewAdapter extends BaseAdapter
{
    private List<GridViewItem> items = new ArrayList<GridViewItem>();
    private LayoutInflater inflater;

    public GridViewAdapter(Context context, List<GridViewItem> items)
    {
        inflater = LayoutInflater.from(context);

        for(GridViewItem item: items){
            this.items.add(new GridViewItem(item.name, item.absoluteLocation, item.bm));
        }
    }

    public void addItem(GridViewItem addition){
        items.add(addition);
    }

    public void removeItem(int pos){
        items.remove(pos);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int i)
    {
        return items.get(i);
    }

    @Override
    public long getItemId(int i){
        return  i;
    }

    public String getItemLocation(int i)
    {
        return items.get(i).absoluteLocation;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup)
    {
        View v = view;
        ImageView picture;
        TextView name;

        if(v == null)
        {
            v = inflater.inflate(R.layout.gridview_row, viewGroup, false);
            v.setTag(R.id.picture, v.findViewById(R.id.picture));
            v.setTag(R.id.text, v.findViewById(R.id.text));
        }

        picture = (ImageView)v.getTag(R.id.picture);
        name = (TextView)v.getTag(R.id.text);

        GridViewItem item = (GridViewItem)getItem(i);

        //Bitmap bm = decodeSampledBitmapFromUri(items.get(i).absoluteLocation, 220, 220);

        picture.setImageBitmap(item.bm);
        name.setText(item.name);

        return v;
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
