package org.ryan.picur;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;

public class AsyncReadBitmapTashThread implements Runnable {
	static class Pool {
		public static final HashMap<String, WeakReference<Bitmap>> mContainer = new HashMap<String, WeakReference<Bitmap>>();
	}

	public static void runSelf(AsyncReadBitmapTashThread asyncThread) {
		executorService.execute(asyncThread);
	}

	private static ExecutorService executorService = Executors.newScheduledThreadPool(8);
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
		if (Pool.mContainer.containsKey(imageFilePath)) {
			Log.i("async", "--- mContainer found:" + imageFilePath);
			if (Pool.mContainer.get(imageFilePath).get() != null) {
				Log.i("async", "+++ mContainer found and ref not null:" + imageFilePath);
				return Pool.mContainer.get(imageFilePath).get();
			} else {
				Log.i("async", "+++ mContainer found but ref is null:" + imageFilePath);
				Bitmap tmpbm = getBitmap(imageFilePath);
				Pool.mContainer.put(imageFilePath, new WeakReference<Bitmap>(tmpbm));
				return tmpbm;
			}
		} else {
			Log.i("async", "--- mContainer not found:" + imageFilePath);
			Bitmap tmpbm = getBitmap(imageFilePath);
			Pool.mContainer.put(imageFilePath, new WeakReference<Bitmap>(tmpbm));
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
		((Activity) imageview.getContext()).getWindowManager().getDefaultDisplay().getMetrics(dm);
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
