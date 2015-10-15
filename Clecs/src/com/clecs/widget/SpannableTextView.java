package com.clecs.widget;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.clecs.MainActivity;
import com.clecs.R;
import com.clecs.fragments.FragmentSearch;
import com.clecs.fragments.FragmentUser;
import com.clecs.fragments.FragmentWebview;
import com.clecs.objects.HashMention;
import com.clecs.utils.AppUtils;

public class SpannableTextView extends TextView
	{
		SpannableString commentsContent = new SpannableString("");
		String searchText = null;
		ArrayList<String> listMentions = new ArrayList<String>();
		ArrayList<String> listHashes = new ArrayList<String>();
//		final String URL_REGEX = android.util.Patterns.WEB_URL.toString();//"<\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]>";

		public SpannableTextView( Context context )
			{
				super(context);
				setMovementMethod(new LinkTouchMovementMethod());
				setHighlightColor(Color.TRANSPARENT);
				setHovered(false);
				setLinkTextColor(Color.RED);
				// setLinksClickable(true);
			}

		public SpannableTextView( Context context, AttributeSet attrs )
			{
				super(context, attrs);
				setMovementMethod(new LinkTouchMovementMethod());
				setHighlightColor(Color.TRANSPARENT);
				setHovered(false);
				setLinkTextColor(Color.RED);
			}

		public SpannableTextView( Context context, AttributeSet attrs, int defStyle )
			{
				super(context, attrs, defStyle);
				setMovementMethod(new LinkMovementMethod());
				setHighlightColor(Color.TRANSPARENT);
				setHovered(false);
				setLinkTextColor(Color.RED);
			}

		@Override
		public boolean onTouchEvent(MotionEvent event)
			{

				return super.onTouchEvent(event);
			}

		private class LinkTouchMovementMethod extends LinkMovementMethod
			{
				@Override
				public boolean onTouchEvent(TextView textView, Spannable spannable, MotionEvent event)
					{
						Selection.removeSelection(spannable);
						return true;
					}

			}

		@Override
		public void setText(CharSequence text, BufferType type)
			{
			if(text==null)
				return;
				String mText = text.toString();
				ArrayList<int[]> hashtagSpans = getSpans(mText, '#');
				ArrayList<int[]> calloutSpans = getSpans(mText, '@');
				ArrayList<int[]> urlSpans = getSpans(mText, android.util.Patterns.WEB_URL.toString());
				ArrayList<int[]> removeableSpans = getRemoveableSpans(mText);

				commentsContent = new SpannableString(mText);
				for (int i = 0; i < hashtagSpans.size(); i++)
					{
						int[] span = hashtagSpans.get(i);
						int hashTagStart = span[0];
						int hashTagEnd = span[1];

						commentsContent.setSpan(new Hashtag(getContext()), hashTagStart, hashTagEnd, 0);

					}

				for (int i = 0; i < calloutSpans.size(); i++)
					{
						int[] span = calloutSpans.get(i);
						int calloutStart = span[0];
						int calloutEnd = span[1];

						commentsContent.setSpan(new CalloutLink(getContext()), calloutStart, calloutEnd, 0);

					}
				for (int i = 0; i < urlSpans.size(); i++)
					{
						int[] span = urlSpans.get(i);
						int calloutStart = span[0];
						int calloutEnd = span[1];

						commentsContent.setSpan(new UrlLink(getContext()), calloutStart, calloutEnd, 0);

					}

				for (int i = 0; i < removeableSpans.size(); i++)
					{
						int[] span = removeableSpans.get(i);
						int tagStart = span[0];
						int tagEnd = span[1];

						try {
							commentsContent.setSpan(new ClearText(getContext()), tagStart, tagEnd, 0);
						} catch (IndexOutOfBoundsException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				// style.setSpan(new ClearText(mActivity), 0, commentsContent.length(),
				// 0);
				if (searchText != null)
					setSearchedText(searchText);
				setMovementMethod(LinkMovementMethod.getInstance());
				super.setText(commentsContent, type);
			}

		public class ClearText extends ClickableSpan
			{
				Context context;
				TextPaint textPaint;

				public ClearText( Context ctx )
					{
						super();
						context = ctx;
					}

				@Override
				public void updateDrawState(TextPaint ds)
					{
						textPaint = ds;
						ds.setUnderlineText(false);
						ds.bgColor = Color.TRANSPARENT;
						// ds.setColor(ds.linkColor);
						// ds.setARGB(255, 0, 255, 0);
						// ds.setARGB(255, 30, 144, 255);
					}

				@Override
				public void onClick(View widget)
					{
						// widget.setBackground(new ColorDrawable(Color.TRANSPARENT));
						TextView tv = (TextView) widget;
						Spannable s = (Spannable) tv.getText();
						int start = s.getSpanStart(this);
						int end = s.getSpanEnd(this);
						String theWord = s.subSequence(start + 1, end).toString();
						// s.setSpan(new BackgroundColorSpan(Color.TRANSPARENT), start, end,
						// Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
						// Toast.makeText(context, String.format("Anywhere else", theWord),
						// 10).show();
						((View) getParent()).performClick();

					}
			}

		public class CalloutLink extends ClickableSpan
			{
				Context context;
				TextPaint ds;

				public CalloutLink( Context ctx )
					{
						super();
						context = ctx;
					}

				@Override
				public void updateDrawState(TextPaint ds)
					{
					 	ds.setARGB(255, 255, 0, 0);
						ds.setUnderlineText(false); // set to false to remove underline
						// ds.setColor(Color.TRANSPARENT);
						ds.bgColor = Color.TRANSPARENT;
						// this.ds = ds;
					}

				void updateDrawerState()
					{
						super.updateDrawState(ds);
					}

				@Override
				public void onClick(View widget)
					{
						TextView tv = (TextView) widget;
						Spannable s = (Spannable) tv.getText();
						int start = s.getSpanStart(this);
						int end = s.getSpanEnd(this);
						String theWord = s.subSequence(start + 1, end).toString();
						FragmentUser fragmentUser = new FragmentUser(theWord);
						AppUtils.replaceFragment(fragmentUser, MainActivity.mActivity.getSupportFragmentManager(), R.id.amMainLayout, false);
						// s.setSpan(new BackgroundColorSpan(Color.TRANSPARENT), start, end,
						// Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
						// Toast.makeText(context, String.format("Here's a cool person: %s",
						// theWord), 10).show();
						// ds.bgColor = Color.RED;
						// ds.set(ds);
						// updateDrawerState();

					}
			}

		public class Hashtag extends ClickableSpan
			{
				Context context;
				TextPaint textPaint;

				public Hashtag( Context ctx )
					{
						super();
						context = ctx;
		}

		@Override
		public void updateDrawState(TextPaint ds) {
			textPaint = ds;
			// ds.setColor(ds.linkColor);
			ds.setARGB(255, 255, 0, 0);
			ds.bgColor = Color.TRANSPARENT;
			// ds.setARGB(255, 30, 144, 255);
		}

		@SuppressLint("NewApi")
		@Override
		public void onClick(View widget) {
			widget.invalidate();
			// widget.setBackground(new ColorDrawable(Color.TRANSPARENT));
			TextView tv = (TextView) widget;
			Spanned s = (Spanned) tv.getText();
			int start = s.getSpanStart(this);
			int end = s.getSpanEnd(this);
			String theWord = s.subSequence(start + 1, end).toString();
			FragmentSearch fragmentSearch = new FragmentSearch("#" + theWord);
			AppUtils.replaceFragment(fragmentSearch,
					MainActivity.mActivity.getSupportFragmentManager(),
					R.id.amMainLayout, false);
			// Toast.makeText(context, String.format("Tags for tags: %s",
			// theWord), 10).show();

		}
	}

	public class UrlLink extends ClickableSpan {
		Context context;
		TextPaint textPaint;

		public UrlLink(Context ctx) {
			super();
			context = ctx;
		}

		@Override
		public void updateDrawState(TextPaint ds) {
			textPaint = ds;
			// ds.setColor(ds.linkColor);
			ds.setARGB(255, 255, 0, 0);
			ds.bgColor = Color.TRANSPARENT;
			// ds.setARGB(255, 30, 144, 255);
		}

		@SuppressLint("NewApi")
		@Override
		public void onClick(View widget) {
			widget.invalidate();
			// widget.setBackground(new ColorDrawable(Color.TRANSPARENT));
			TextView tv = (TextView) widget;
			Spanned s = (Spanned) tv.getText();
			int start = s.getSpanStart(this);
			int end = s.getSpanEnd(this);
			String theWord = s.subSequence(start, end).toString();
			// FragmentSearch fragmentSearch = new FragmentSearch("#" +
			// theWord);
			// AppUtils.replaceFragment(fragmentSearch,
			// MainActivity.mActivity.getSupportFragmentManager(),
			// R.id.amMainLayout, false);
			// Toast.makeText(context, String.format("Tags for tags: %s",
			// theWord), 10).show();
			FragmentWebview fragmentWebview = new FragmentWebview(theWord);
			AppUtils.replaceFragment(fragmentWebview,
					MainActivity.mActivity.getSupportFragmentManager(),
					R.id.amMainLayout, false);
			// AppUtils.showToast(theWord);
		}
	}

	public ArrayList<int[]> getSpans(String body, char prefix) {
		ArrayList<int[]> spans = new ArrayList<int[]>();

		Pattern pattern = Pattern.compile(prefix + "\\w+");
		Matcher matcher = pattern.matcher(body);

		// Check all occurrences
		while (matcher.find()) {
			int[] currentSpan = new int[2];
			currentSpan[0] = matcher.start();
			currentSpan[1] = matcher.end();

			String word = (body.substring(currentSpan[0], currentSpan[1]));
			if ((prefix == '@' && listMentions.contains(word.toLowerCase()))
					|| (prefix == '#' && listHashes
							.contains(word.toLowerCase())))
				spans.add(currentSpan);
		}

		return spans;
	}

	public ArrayList<int[]> getSpans(String body, String regex) {
		ArrayList<int[]> spans = new ArrayList<int[]>();

		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(body);

		// Check all occurrences
		while (matcher.find()) {
			int[] currentSpan = new int[2];
			currentSpan[0] = matcher.start();
			currentSpan[1] = matcher.end();

			// String word = (body.substring(currentSpan[0], currentSpan[1]));
			// if ((prefix == '@' && listMentions.contains(word.toLowerCase()))
			// || (prefix == '#' && listHashes.contains(word.toLowerCase())))
			spans.add(currentSpan);
		}

		return spans;
	}

	public ArrayList<int[]> getRemoveableSpans(String body) {
		ArrayList<int[]> spans = new ArrayList<int[]>();

		Pattern pattern = Pattern.compile("@\\w+|#\\w+");
		Matcher matcher = pattern.matcher(body);

		// Check all occurrences
		int[] currentSpan = new int[2];
		currentSpan[0] = 0;
		boolean found = false;
		while (matcher.find()) {
			found = true;
			// int[] currentSpan = new int[2];
			currentSpan[1] = matcher.start() == 0 ? 0
					: (matcher.start() - 1 < currentSpan[0]) ? currentSpan[0]
							: matcher.start() - 1;
			spans.add(currentSpan);
			currentSpan = new int[2];
			currentSpan[0] = matcher.end() == body.length() - 1 ? matcher.end()
					: (matcher.end() + 1);
		}
		// Pattern pattern2 = Pattern.compile(URL_REGEX);
		currentSpan[0] = 0;
		matcher = android.util.Patterns.WEB_URL.matcher(body);
		while (matcher.find()) {
			found = true;
			// int[] currentSpan = new int[2];
			currentSpan[1] = matcher.start() == 0 ? 0
					: (matcher.start() - 1 < currentSpan[0]) ? currentSpan[0]
							: matcher.start() - 1;
			spans.add(currentSpan);
			currentSpan = new int[2];
			currentSpan[0] = matcher.end() == body.length() - 1 ? matcher.end()
					: (matcher.end() + 1);
		}
		if (!found) {
			currentSpan[1] = body.length();
			spans.add(currentSpan);
		} else if (currentSpan[1] < body.length() - 1) {
			// int lastIndex = currentSpan[1];
			// currentSpan[0] = lastIndex;
			currentSpan[1] = body.length();
			spans.add(currentSpan);
		}

		return spans;
	}

	public SpannableString getSpanableString() {
		return commentsContent;
	}

	@SuppressLint("DefaultLocale")
	public void setSearchedText(String searchText) {
		this.searchText = searchText;
		final Pattern p = Pattern.compile(searchText.toLowerCase());
		final Matcher matcher = p.matcher(commentsContent.toString()
				.toLowerCase());
		// String pattern = ("?i" + searchText);

		// final SpannableStringBuilder spannable = new
		// SpannableStringBuilder(text);
		final BackgroundColorSpan span = new BackgroundColorSpan(
				Color.parseColor("#FEDBDB"));
		while (matcher.find()) {
			commentsContent.setSpan(span, matcher.start(), matcher.end(),
					Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
	}

	public void setHashes(HashMention[] hashList) {
		listHashes = new ArrayList<String>();
		for (int i = 0; i < hashList.length; i++) {
			listHashes.add(hashList[i].getText().toLowerCase());
		}
	}

	public void setMentions(HashMention[] mentionsList) {
		listMentions = new ArrayList<String>();
		for (int i = 0; i < mentionsList.length; i++) {
			listMentions.add(mentionsList[i].getText().toLowerCase());
		}
	}
}
