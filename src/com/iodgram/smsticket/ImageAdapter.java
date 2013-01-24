package com.iodgram.smsticket;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter {
	private final Activity context;
	private final Integer[] images;
	
	public ImageAdapter(Activity context, Integer[] images) {
		this.context = context;
		this.images = images;
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		
		if (v == null) {
			LayoutInflater li = context.getLayoutInflater();
			v = li.inflate(R.layout.citybutton_item, null);
		}
		ImageView iv = (ImageView) v.findViewById(R.id.citybutton_image);
		iv.setImageResource(images[position]);
		
		return v;
	}

	public int getCount() {
		return images.length;
	}

	public Object getItem(int position) {
		return images[position];
	}

	public long getItemId(int position) {
		return position;
	}
}
