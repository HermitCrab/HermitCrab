package org.hermitcrab.entity;

import com.google.gson.annotations.SerializedName;

public class Software {
	@SerializedName("ID")
	public String id;
	
	@SerializedName("Name")
	public String name;
	
	@SerializedName("Url")
	public String url;
	
	@SerializedName("RelativeUrl")
	public String relativeUrl;
	
	@SerializedName("Votes")
	public int votes;
	
	@SerializedName("IconUrl")
	public String iconUrl;
	
	@SerializedName("Platforms")
	public String[] platforms;
	
	@SerializedName("License")
	public String license;
	
	@SerializedName("ShortDescription")
	public String shortDescription;
	
	@SerializedName("Tags")
	public String[] tags;
	
	@SerializedName("Items")
	public Software[] items;
}
