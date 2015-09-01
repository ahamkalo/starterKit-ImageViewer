package com.capgemini.starterkit.imageViewer.dataprovider.data;

public class ImageVO {

	private int id;
	private String name;
	private String path;

	public ImageVO(int id, String name, String path) {
		this.id = id;
		this.name = name;
		this.path = path;
	}

	public ImageVO() {

	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}

}
