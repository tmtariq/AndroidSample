package com.clecs;

import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;


public class ImageViewerActivity extends Activity
{
	public static final String KEY_IMG_URL = "imgUrl";
	ImageView iv;
	View cross;

	@Override
	protected void onCreate(Bundle savedInstanceState)
		{
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_image_viewer);
			iv = (ImageView) findViewById(R.id.aivIv);
			cross = findViewById(R.id.aivCross);
			cross.setOnClickListener(new OnClickListener()
				{

					@Override
					public void onClick(View v)
						{
							ImageViewerActivity.this.finish();
						}
				});

			if (getIntent().getExtras().containsKey(KEY_IMG_URL))
				{
					String imgUrl = getIntent().getExtras().getString(KEY_IMG_URL);
					ImageLoader.getInstance().displayImage(imgUrl, iv);
				}
		}

}
