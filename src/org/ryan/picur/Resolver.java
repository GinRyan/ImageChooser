package org.ryan.picur;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;

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
	private Context context;
	private static HashMap<String, WeakReference<Bitmap>> mContainer = new HashMap<String, WeakReference<Bitmap>>();

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
		this.context = context;
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
			// bitmap = getBitmap(imageFilePath);
			// imageview.setImageBitmap(bitmap);
			executorService.execute(new AsyncReadBitmapTashThread(imageview, imageFilePath));
			return bitmap;
		}
		return bitmap;
	}

	ExecutorService executorService = Executors.newScheduledThreadPool(8);

	class AsyncReadBitmapTashThread implements Runnable {
		private ImageView imageview;
		private String imageFilePath;

		public AsyncReadBitmapTashThread(ImageView imageview, String imageFilePath) {
			this.imageview = imageview;
			this.imageFilePath = imageFilePath;
		}

		@Override
		public void run() {
			WeakReference<Bitmap> ref = new WeakReference<Bitmap>(asyncReadBitmap());
			imageview.post(new AsyncNotifyController(ref.get()));
		}

		private Bitmap asyncReadBitmap() {
			Log.i("async", "*** start to async reading file:" + imageFilePath);
			if (mContainer.containsKey(imageFilePath)) {
				Log.i("async", "--- mContainer found:" + imageFilePath);
				if (mContainer.get(imageFilePath).get() != null) {
					Log.i("async", "+++ mContainer found and ref not null:" + imageFilePath);
					return mContainer.get(imageFilePath).get();
				} else {
					Log.i("async", "+++ mContainer found but ref is null:" + imageFilePath);
					Bitmap tmpbm = getBitmap(imageFilePath);
					mContainer.put(imageFilePath, new WeakReference<Bitmap>(tmpbm));
					return tmpbm;
				}
			} else {
				Log.i("async", "--- mContainer not found:" + imageFilePath);
				Bitmap tmpbm = getBitmap(imageFilePath);
				mContainer.put(imageFilePath, new WeakReference<Bitmap>(tmpbm));
				return tmpbm;
			}
		}

		class AsyncNotifyController implements Runnable {
			private Bitmap resultbitmap;

			public AsyncNotifyController(Bitmap resultbitmap) {
				this.resultbitmap = resultbitmap;
			}

			@Override
			public void run() {
				imageview.setImageBitmap(resultbitmap);
			}
		}
	}

	/**
	 * 处理图片，将大图片进行一定比例的缩放
	 * 
	 * @param imageFilePath
	 *            路径
	 * @return
	 */
	private Bitmap getBitmap(String imageFilePath) {
		// 獲取DefaultDisplay
		DisplayMetrics dm = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
		int dh = dm.heightPixels;
		int dw = dm.widthPixels;
		BitmapFactory.Options bmFactoryOptions = new Options();
		bmFactoryOptions.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(imageFilePath, bmFactoryOptions);
		int hRatio = (int) Math.ceil(bmFactoryOptions.outHeight / (float) dh);
		int wRatio = (int) Math.ceil(bmFactoryOptions.outWidth / (float) dw);
		// 判断是按高比率缩放还是宽比例缩放
		if (hRatio > 1 || wRatio > 1) {
			if (hRatio > wRatio) {
				bmFactoryOptions.inSampleSize = hRatio;
			} else {
				bmFactoryOptions.inSampleSize = wRatio;
			}
		}
		// 对图像进行真正的解码
		bmFactoryOptions.inJustDecodeBounds = false;
		bitmap = BitmapFactory.decodeFile(imageFilePath, bmFactoryOptions);
		return bitmap;
	}
}