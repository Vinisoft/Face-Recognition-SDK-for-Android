package com.vinisoft.facesdk.detect;

/**
 * Class contain data after SDK detect success.
 * */
public class FaceDetectInfo {
	public int x;
	public int y;
	public int width;
	public int height;
	public int id;
	public String name;

	public FaceDetectInfo() {

	}

	public FaceDetectInfo(int id, String name, int x, int y, int width,
			int height) {
		this.id = id;
		this.name = name;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}
}
