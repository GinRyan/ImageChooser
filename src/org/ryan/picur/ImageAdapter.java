package org.ryan.picur;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class ImageAdapter extends BaseAdapter {
	Resolver resolver;
	private DisplayMetrics outMetrics;
	int screenWidth;
	int oneImageBlockWidthHeight;
	private Activity ctx;

	public Activity getActivity() {
		return ctx;
	}

	public ImageAdapter(Activity ctx) {
		this.ctx = ctx;
		resolver = new Resolver().init(getActivity());
		outMetrics = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
		screenWidth = outMetrics.widthPixels;
		oneImageBlockWidthHeight = (int) (screenWidth - TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, outMetrics)) / 3;
	}

	@Override
	public int getCount() {
		return resolver.getCount();
	}

	@Override
	public Object getItem(int position) {
		return resolver.getItem(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	class ViewHolder {
		ImageView imageview;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder vh = new ViewHolder();
		if (convertView == null) {
			vh.imageview = new ImageView(getActivity());
			AbsListView.LayoutParams params = new AbsListView.LayoutParams(oneImageBlockWidthHeight, oneImageBlockWidthHeight);
			vh.imageview.setLayoutParams(params);
			vh.imageview.setScaleType(ScaleType.CENTER_CROP);
			convertView = vh.imageview;
		} else {
			vh.imageview = (ImageView) convertView;
			vh.imageview.setImageBitmap(null);
		}
		resolver.readNext(vh.imageview, position);
		return convertView;
	}
}