package com.clecs.objects;

import java.io.Serializable;

public class HashMention implements Serializable
{
	String text;
	int index;
	int length;

	public String getText()
		{
			return text;
		}

	public void setText(String text)
		{
			this.text = text;
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