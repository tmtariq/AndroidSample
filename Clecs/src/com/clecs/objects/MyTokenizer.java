package com.clecs.objects;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.widget.MultiAutoCompleteTextView.Tokenizer;

public class MyTokenizer implements Tokenizer
	{
		char startToken = ' ';

		public MyTokenizer( char startToken )
			{
				this.startToken = startToken;
			}

		@Override
		public CharSequence terminateToken(CharSequence text)
			{
				int i = text.length();

				while (i > 0 && text.charAt(i - 1) == ' ')
					{
						i--;
					}

				if (i > 0 && text.charAt(i - 1) == ' ')
					{
						return text;
					}
				else
					{
						if (text instanceof Spanned)
							{
								SpannableString sp = new SpannableString(text + " ");
								TextUtils.copySpansFrom((Spanned) text, 0, text.length(), Object.class, sp, 0);
								return sp;
							}
						else
							{
								return text + " ";
							}
					}
			}

		@Override
		public int findTokenStart(CharSequence text, int cursor)
			{
				int i = cursor;

				while (i > 0 && text.charAt(i - 1) != startToken)
					{
						i--;
					}

				// Check if token really started with @, else we don't have a valid token
				if (i < 1 || text.charAt(i - 1) != startToken)
					{
						return cursor;
					}

				return i;
			}

		@Override
		public int findTokenEnd(CharSequence text, int cursor)
			{
				int i = cursor;
				int len = text.length();

				while (i < len)
					{
						if (text.charAt(i) == ' ')
							{
								return i;
							}
						else
							{
								i++;
							}
					}

				return len;
			}

	}
