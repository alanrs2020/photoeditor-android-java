package com.alanrs.photoeditor;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;

public class GridAdapter extends BaseAdapter {
    Context context;
    ArrayList<Uri> images;
    LayoutInflater inflter;
    public GridAdapter(Context applicationContext, ArrayList images) {
        this.context = applicationContext;
        this.images = images;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        view = inflter.inflate(R.layout.grid_view, null); // inflate the layout
        ImageView icon = (ImageView) view.findViewById(R.id.icon); // get the reference of ImageView
        icon.setImageURI(images.get(position)); // set logo images
        return view;
    }
}