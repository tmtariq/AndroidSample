package com.clecs.utils;

import com.clecs.R;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;

public class App extends Application {
	static Context mContext;
	private volatile static App instance;

	/** Returns singleton class instance */
	public static App getInstance() {
		if (instance == null) {
			synchronized (App.class) {
				if (instance == null) {
					instance = new App();
				}
			}
		}
		return instance;
	}

	public static void initImageLoader(Context context) {

		DisplayImageOptions defaultDisplayImageOptions = new DisplayImageOptions.Builder()
				.showImageForEmptyUri(R.drawable.dummy)
				.showImageOnFail(R.drawable.dummy)
				.showImageOnLoading(R.drawable.dummy).cacheInMemory(true)
				.cacheOnDisk(true)
				.bitmapConfig(Bitmap.Config.RGB_565)
				//.imageScaleType(ImageScaleType.EXACTLY)
				// .displayer(RoundedBitmapDisplayer)
				.build();

		ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(
				mContext);
		//config.threadPriority(Thread.NORM_PRIORITY - 2);
		//config.denyCacheImageMultipleSizesInMemory();
		config.threadPoolSize(5);
		config.diskCacheExtraOptions(500, 500, null);
		config.memoryCacheExtraOptions(500, 500);
		config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
		config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
		// config.memoryCacheSizePercentage(TRIM_MEMORY_MODERATE);
		config.tasksProcessingOrder(QueueProcessingType.LIFO);
		config.defaultDisplayImageOptions(defaultDisplayImageOptions);
		// config.writeDebugLogs(); // Remove for release app

		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config.build());
	}

	public Context getContext() {
		// return instance.getApplicationContext();
		return mContext;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		mContext = getApplicationContext();
		initImageLoader(getApplicationContext());
	}

	public void setContext(Context context) {
		mContext = context;
	}
}
