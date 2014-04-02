package org.ryan.picur;

import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import android.app.Application;
import android.graphics.Bitmap;

public class SelfApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		DisplayImageOptions defaultDisplayImageOptions = new DisplayImageOptions.Builder()//
				.bitmapConfig(Bitmap.Config.RGB_565)//
				.cacheInMemory(true)//
				.imageScaleType(ImageScaleType.IN_SAMPLE_INT)//
				.build();
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)//
				.memoryCache(new WeakMemoryCache())//
				.memoryCacheSize(10 * 1024 * 1024)//
				.threadPoolSize(5)//
				.defaultDisplayImageOptions(defaultDisplayImageOptions)//
				.build();
		ImageLoader.getInstance().init(config);
	}

}
