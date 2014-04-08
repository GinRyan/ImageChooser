package org.ryan.picur;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewParent;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * 图片显示和拾取适配器
 * 
 * @author Liang
 * 
 */
public abstract class ImageChooserAdapter extends BaseAdapter implements OnItemClickListener {
	/**
	 * <pre>
	 * 这个参数是用于跳转到回上一个页面后选取已选择的路径，
	 * 获取的对象为ArrayList类型
	 * </pre>
	 */
	public static final String URI = "uri";
	Resolver resolver;
	private Activity ctx;
	GridView grid;
	ArrayList<String> list = new ArrayList<String>();
	private ViewParent parent;
	ViewGroup viewroot;
	private SelectionMode selectionMode;

	int maxImagesCount = 3;

	/**
	 * 获取已选择的图片路径集合
	 * 
	 * @return
	 */
	public List<String> getCheckedImagesPaths() {
		return list;
	}

	public Activity getActivity() {
		return ctx;
	}

	/**
	 * 设置可以选取最多的图片数量
	 * 
	 * @param maxImagesCount
	 */
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

	/**
	 * 取出父布局
	 */
	public void initparent() {
		parent = grid.getParent();
		viewroot = (ViewGroup) parent;
		viewroot.removeView(grid);
	}

	/**
	 * 重新载入提示条
	 */
	public void readd() {
		LinearLayout layout = new LinearLayout(getActivity());
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		selectionMode = new SelectionMode(ctx) {

			@Override
			public void doDeliver(View v) {
				/*
				 * 这一部分是为了保证已选择的图片路径可以被传递到上个界面
				 */
				Intent intent = new Intent();
				intent.putExtra(URI, list);
				ctx.setResult(Activity.RESULT_OK, intent);
				ctx.finish();
			}
		};
		layout.addView(selectionMode.inflateBuild());
		layout.addView(grid);
		layout.invalidate();// 请求自身重绘
		layout.requestLayout();//
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
			vh.check.clearAnimation();
		}
		boolean checkedCurrentImage = list.contains(getPath(position));
		Log.i("info", "显示位置position: " + position + ", 绘制图片: " + getPath(position) + "，是否选取：" + checkedCurrentImage);
		vh.check.setVisibility(checkedCurrentImage ? View.VISIBLE : View.GONE);
		resolver.readByPosition(vh.image_item, position);
		return convertView;
	}

	/**
	 * 获取某位置上的文件路径
	 * 
	 * @param position
	 * @return
	 */
	public String getPath(int position) {
		return resolver.readByPositionOnlyPath(position);
	}

	/**
	 * 动画淡出隐藏
	 * 
	 * @param v
	 */
	private void animateToInvisible(final View v) {
		AlphaAnimation alphaAnim = new AlphaAnimation(1.0f, 0f);
		alphaAnim.setFillAfter(true);
		alphaAnim.setFillBefore(false);
		alphaAnim.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				v.setVisibility(View.VISIBLE);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				v.setVisibility(View.INVISIBLE);
			}
		});
		alphaAnim.setDuration(300);
		v.startAnimation(alphaAnim);
	}

	/**
	 * 动画淡入显示
	 * 
	 * @param v
	 */
	private void animateToVisible(final View v) {
		AlphaAnimation alphaAnim = new AlphaAnimation(0f, 1.0f);
		alphaAnim.setFillAfter(true);
		alphaAnim.setFillBefore(false);
		alphaAnim.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				v.setVisibility(View.INVISIBLE);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				v.setVisibility(View.VISIBLE);
			}
		});
		alphaAnim.setDuration(300);
		v.startAnimation(alphaAnim);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		View check = view.findViewById(R.id.check);
		if (check.isShown()) {
			animateToInvisible(check);
			list.remove(getPath(position));
		} else {
			if (list.size() < maxImagesCount) {
				animateToVisible(check);
				list.add(getPath(position));
			} else {
				overTheMaxImagesCount(maxImagesCount);
			}
		}
		selectionMode.assignNum(list.size());
	}

	/**
	 * 当选择达到最多选取图片的数量时执行动作？
	 * 
	 * @param max
	 */
	public abstract void overTheMaxImagesCount(int max);
}