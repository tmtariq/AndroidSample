package com.clecs.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;

public class MyTextView extends EditText
{
	public MyTextView( Context context )
		{
			super(context);
		}

	public MyTextView( Context context, AttributeSet attrs )
		{
			super(context, attrs);
		}

	public MyTextView( Context context, AttributeSet attrs, int defStyle )
		{
			super(context, attrs, defStyle);
		}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
		{
			if (keyCode == KeyEvent.KEYCODE_ENTER)
				{
					// Just ignore the [Enter] key
					return true;
				}
			// Handle all other keys in the default way
			return super.onKeyDown(keyCode, event);
		}
}
