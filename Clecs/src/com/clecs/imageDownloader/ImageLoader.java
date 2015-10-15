package com.clecs.imageDownloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import com.clecs.R;

public class ImageLoader
	{
		boolean isFullSizeRequested;
		MemoryCache memoryCache = new MemoryCache();
		FileCache fileCache;
		private Map<ImageView, String> imageViews = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
		ExecutorService executorService;
		Handler handler = new Handler();// handler to display images in UI thread

		public ImageLoader( Context context )
			{
				fileCache = new FileCache(context);
				executorService = Executors.newFixedThreadPool(5);
			}

		final int stub_id = R.drawable.placeholder;

		public void displayImage(String url, ImageView imageView)
			{
				imageViews.put(imageView, url);
				Bitmap bitmap = memoryCache.get(url);
				if (bitmap != null)
					{
						imageView.setImageBitmap(bitmap);
						// animate(imageView, bitmap);
					}
				else
					{
						queuePhoto(url, imageView);
						imageView.setImageResource(stub_id);
					}
			}

		public void displayFullImage(String url, ImageView imageView)
			{
				isFullSizeRequested = true;
				displayImage(url, imageView);
			}

		private void queuePhoto(String url, ImageView imageView)
			{
				PhotoToLoad p = new PhotoToLoad(url, imageView);
				executorService.submit(new PhotosLoader(p));
			}

		private Bitmap getBitmap(String url)
			{
				File f = fileCache.getFile(url);

				// from SD cache
				Bitmap b = decodeFile(f);
				if (b != null)
					return b;

				// from web
				try
					{
						Bitmap bitmap = null;
//						if (url.contains("//clecs."))
//							url = url.replace("//clecs.", "//www.clecs.");

						URL imageUrl = new URL(url);
						HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
						conn.setConnectTimeout(30000);
						conn.setReadTimeout(30000);
						conn.setInstanceFollowRedirects(true);
						InputStream is = conn.getInputStream();
						OutputStream os = new FileOutputStream(f);
						copyStream(is, os);
						os.close();
						bitmap = decodeFile(f);
						return bitmap;
					}
				catch (Throwable ex)
					{
						ex.printStackTrace();
						if (ex instanceof OutOfMemoryError)
							memoryCache.clear();
						return null;
					}
			}

		// decodes image and scales it to reduce memory consumption
		private Bitmap decodeFile(File f)
			{
				try
					{
						// decode image size
						BitmapFactory.Options o = new BitmapFactory.Options();
						o.inJustDecodeBounds = true;
						FileInputStream stream1 = new FileInputStream(f);
						BitmapFactory.decodeStream(stream1, null, o);
						stream1.close();

						// Find the correct scale value. It should be the power of 2.
						final int REQUIRED_SIZE = 150;
						int width_tmp = o.outWidth, height_tmp = o.outHeight;
						int scale = 1;
						if (!isFullSizeRequested)
							while (true)
								{
									if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE)
										break;
									width_tmp /= 2;
									height_tmp /= 2;
									scale *= 2;
								}

						// decode with inSampleSize
						BitmapFactory.Options o2 = new BitmapFactory.Options();
						o2.inSampleSize = scale;
						FileInputStream stream2 = new FileInputStream(f);
						Bitmap bitmap = BitmapFactory.decodeStream(stream2, null, o2);
						stream2.close();
						return bitmap;
					}
				catch (FileNotFoundException e)
					{
					}
				catch (IOException e)
					{
						e.printStackTrace();
					}
				return null;
			}

		// Task for the queue
		private class PhotoToLoad
			{
				public String url;
				public ImageView imageView;

				public PhotoToLoad( String u, ImageView i )
					{
						url = u;
						imageView = i;
					}
			}

		class PhotosLoader implements Runnable
			{
				PhotoToLoad photoToLoad;

				PhotosLoader( PhotoToLoad photoToLoad )
					{
						this.photoToLoad = photoToLoad;
					}

				@Override
				public void run()
					{
						try
							{
								if (imageViewReused(photoToLoad))
									return;
								Bitmap bmp = getBitmap(photoToLoad.url);
								memoryCache.put(photoToLoad.url, bmp);
								if (imageViewReused(photoToLoad))
									return;
								BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad);
								handler.post(bd);
							}
						catch (Throwable th)
							{
								th.printStackTrace();
							}
					}
			}

		boolean imageViewReused(PhotoToLoad photoToLoad)
			{
				String tag = imageViews.get(photoToLoad.imageView);
				if (tag == null || !tag.equals(photoToLoad.url))
					return true;
				return false;
			}

		// Used to display bitmap in the UI thread
		class BitmapDisplayer implements Runnable
			{
				Bitmap bitmap;
				PhotoToLoad photoToLoad;

				public BitmapDisplayer( Bitmap b, PhotoToLoad p )
					{
						bitmap = b;
						photoToLoad = p;
					}

				@Override
				public void run()
					{
						if (imageViewReused(photoToLoad))
							return;
						if (bitmap != null)
							{
								// photoToLoad.imageView.setImageBitmap(bitmap);
								animate(photoToLoad.imageView, bitmap);
							}
						else
							photoToLoad.imageView.setImageResource(stub_id);
					}
			}

		public void clearCache()
			{
				memoryCache.clear();
				fileCache.clear();
			}

		public static void copyStream(InputStream is, OutputStream os)
			{
				final int buffer_size = 1024;
				try
					{
						byte[] bytes = new byte[buffer_size];
						for (;;)
							{
								int count = is.read(bytes, 0, buffer_size);
								if (count == -1)
									break;
								os.write(bytes, 0, count);
							}
					}
				catch (Exception ex)
					{
					}
			}

		private void animate(ImageView imageView, Bitmap imgBitmap)
			{

				// imageView <-- The View which displays the images
				// images[] <-- Holds R references to the images to display
				// imageIndex <-- index of the first image to show in images[]
				// forever <-- If equals true then after the last image it starts all over again with the first image resulting in an infinite loop. You have been warned.

				int fadeInDuration = 500; // Configure time values here
				int timeBetween = 3000;
				int fadeOutDuration = 1000;

				imageView.setVisibility(View.VISIBLE); // Visible or invisible by default - this will apply when the animation ends
				imageView.setImageBitmap(imgBitmap);

				Animation fadeIn = new AlphaAnimation(0, 1);
				fadeIn.setInterpolator(new DecelerateInterpolator()); // add this
				fadeIn.setDuration(fadeInDuration);

				Animation fadeOut = new AlphaAnimation(1, 0);
				fadeOut.setInterpolator(new AccelerateInterpolator()); // and this
				fadeOut.setStartOffset(fadeInDuration + timeBetween);
				fadeOut.setDuration(fadeOutDuration);

				AnimationSet animation = new AnimationSet(false); // change to false
				animation.addAnimation(fadeIn);
				// animation.addAnimation(fadeOut);
				animation.setRepeatCount(0);
//				imageView.setAnimation(animation);

				animation.setAnimationListener(new AnimationListener()
					{
						public void onAnimationEnd(Animation animation)
							{
								// if (images.length - 1 > imageIndex)
								// {
								// animate(imageView, images, imageIndex + 1, forever); // Calls itself until it gets to the end of the array
								// }
								// else
								// {
								// if (forever == true)
								// {
								// animate(imageView, images, 0, forever); // Calls itself to start the animation all over again in a loop if forever = true
								// }
								// }
							}

						public void onAnimationRepeat(Animation animation)
							{
								// TODO Auto-generated method stub
							}

						public void onAnimationStart(Animation animation)
							{
								// TODO Auto-generated method stub
							}
					});
			}
	}