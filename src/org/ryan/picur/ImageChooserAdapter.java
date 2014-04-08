package org.ryan.picur;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewParent;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public abstract class ImageChooserAdapter extends BaseAdapter implements OnItemClickListener {
	Resolver resolver;
	private Activity ctx;
	GridView grid;
	List<String> list = new ArrayList<String>();
	private ViewParent parent;
	ViewGroup viewroot;
	private SelectionMode selectionMode;

	int maxImagesCount = 3;

	public List<String> getCheckedImagesPaths() {
		return list;
	}

	public Activity getActivity() {
		return ctx;
	}

	public void setMaxImagesCount(int maxImagesCount) {
		this.maxImagesCount = maxImagesCount;
	}

	public ImageChooserAdapter(Activity ctx, GridView grid) {
		this.ctx = ctx;
		this.grid = grid;
		resolver = new Resolver().init(getActivity());
		this.grid.setOnItemClickListener(this);
		initparent();
		readd();
	}

	public void initparent() {
		parent = grid.getParent();
		viewroot = (ViewGroup) parent;
		viewroot.removeView(grid);
	}

	public void readd() {
		LinearLayout layout = new LinearLayout(getActivity());
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		selectionMode = new SelectionMode(ctx) {

			@Override
			public void doDeliver(View v) {
				Toast.makeText(ctx, "完成选取", Toast.LENGTH_SHORT).show();
			}
		};
		layout.addView(selectionMode.inflateBuild());
		layout.addView(grid);
		layout.invalidate();
		layout.requestLayout();
		viewroot.addView(layout);
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
			vh.check.setVisibility(View.GONE);
		}
		boolean checkedCurrentImage = list.contains(getPath(position));
		Log.i("info", "显示位置position: " + position + ", 绘制图片: " + getPath(position) + "，是否选取：" + checkedCurrentImage);
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
			check.setVisibility(View.INVISIBLE);
			list.remove(getPath(position));
		} else {
			if (list.size() < maxImagesCount) {
				check.setVisibility(View.VISIBLE);
				list.add(getPath(position));
			} else {
				overTheMaxImagesCount(maxImagesCount);
			}
		}
		selectionMode.assignNum(list.size());
	}

	public abstract void overTheMaxImagesCount(int max);
}