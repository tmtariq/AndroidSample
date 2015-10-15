package com.clecs.objects;

import java.io.Serializable;

public class Link implements Serializable
{
	String link;
	int index;
	int length;

	public String getText()
		{
			return link;
		}

	public void setText(String text)
		{
			this.link = text;
		}

	public int getIndex()
		{
			return index;
		}

	public void setIndex(int index)
		{
			this.index = index;
		}

	public int getLength()
		{
			return length;
		}

	public void setLength(int length)
		{
			this.length = length;
		}
}