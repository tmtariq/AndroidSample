package com.clecs.objects;

import java.io.Serializable;
import java.util.ArrayList;

public class PostWithLinks extends Post implements Serializable
{

	private static final long serialVersionUID = 1L;
	ArrayList<String> hashes;
	ArrayList<String> mentions;
	ArrayList<String> links;

	public ArrayList<String> getLinks()
		{
			return links;
		}

	public void setLinks(ArrayList<String> links)
		{
			this.links = links;
		}
}
