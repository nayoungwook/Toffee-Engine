package com.coconut.toffee.math;

public class Vector {

	private float x, y, z = 1;

	public Vector(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public Vector(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vector cloneVector() {
		Vector result = new Vector(0, 0);
		result.setTransform(x, y, z);
		return result;
	}

	public Vector normalized() {
		float length = (float) Math.sqrt(x * x + y * y + z * z);
		if (length == 0)
			return new Vector(0, 0, 0);

		return new Vector(x / length, y / length, z / length);
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public float getZ() {
		return z;
	}

	public void setX(float x) {
		this.x = x;
	}

	public void setY(float y) {
		this.y = y;
	}

	public void setZ(float z) {
		this.z = z;
	}

	public void setTransform(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public void setTransform(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public void translate(float xv, float yv) {
		this.x += xv;
		this.y += yv;
	}

	public void translate(float xv, float yv, float zv) {
		this.x += xv;
		this.y += yv;
		this.z += zv;
	}

}