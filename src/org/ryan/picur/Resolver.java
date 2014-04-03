package org.ryan.picur;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.util.Log;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 系统相册拾取器
 * 
 * @author Liang
 * 
 */
public class Resolver {
	private Cursor cursor;
	private int fileColumn;
	boolean isNotEmpty;

	public Object getItem(int position) {
		cursor.move(position);
		return cursor.getString(fileColumn);
	}

	public int getCount() {
		return cursor.getCount();
	}

	/**
	 * 初始化系统Provider读取器
	 * 
	 * @param context
	 * @return
	 */
	public Resolver init(Context context) {
		// 设置要返回的字段
		String[] columns = { Media.DATA, Media._ID, Media.TITLE, Media.DISPLAY_NAME };
		// 执行查询，返回一个cursor
		cursor = context.getContentResolver().query(Media.EXTERNAL_CONTENT_URI, columns, null, null, null);
		fileColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		isNotEmpty = cursor.moveToFirst();
		return this;
	}

	/**
	 * <pre>
	 * 读取下一张图片，这个方法非常重要，在这个方法中还会收集到图片的实际路径，所以必须想办法取到路径
	 * </pre>
	 * 
	 * TODO 这里还缺少内存管理
	 * 
	 * @param imageview
	 * @param position
	 * 
	 * @return 返回位图
	 */
	public Bitmap readNext(ImageView imageview, int position) {
		Bitmap bitmap = null;
		if (!isNotEmpty) {
			return bitmap;
		}
		if (cursor.moveToPosition(position)) {
			String imageFilePath = cursor.getString(fileColumn);
			Log.d("Image Data", "Uri: " + imageFilePath);
			ImageLoader.getInstance().displayImage("file://" + imageFilePath, imageview);
			return bitmap;
		}
		return bitmap;
	}

}