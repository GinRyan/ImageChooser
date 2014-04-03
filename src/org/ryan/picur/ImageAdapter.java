package org.ryan.picur;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter implements OnItemClickListener {
	Resolver resolver;
	private Activity ctx;
	GridView grid;
	List<String> list = new ArrayList<String>();

	public List<String> getCheckedImagesPaths() {
		return list;
	}

	public Activity getActivity() {
		return ctx;
	}

	public ImageAdapter(Activity ctx, GridView grid) {
		this.ctx = ctx;
		this.grid = grid;
		resolver = new Resolver().init(getActivity());
		this.grid.setOnItemClickListener(this);
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
		ImageView image_item;
		View check;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder vh = new ViewHolder();
		if (convertView == null) {
			convertView = LayoutInflater.from(getActivity()).inflate(R.layout.item_image_checkable, null, false);
			vh.image_item = (ImageView) convertView.findViewById(R.id.image_item);
			vh.check = convertView.findViewById(R.id.check);
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
			vh.image_item.setImageBitmap(null);
		}
		boolean checkedCurrentImage = list.contains(getPath(position));
		vh.check.setVisibility(checkedCurrentImage ? View.VISIBLE : View.GONE);
		resolver.readByPosition(vh.image_item, position);
		return convertView;
	}

	public String getPath(int position) {
		return resolver.readByPositionOnlyPath(position);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		View check = view.findViewById(R.id.check);
		if (check.isShown()) {
			check.setVisibility(View.GONE);
			list.remove(getPath(position));
		} else {
			check.setVisibility(View.VISIBLE);
			list.add(getPath(position));
		}
	}
}