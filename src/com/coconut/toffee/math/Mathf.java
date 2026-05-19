package com.coconut.toffee.math;

import org.joml.Matrix4f;
import org.joml.Vector4f;

import com.coconut.toffee.Display;
import com.coconut.toffee.camera.Camera;

public class Mathf {

	public static float getDistance(Vector position, Vector position2) {
		return (float) Math.abs(Math.sqrt(((position.getX() - position2.getX()) * (position.getX() - position2.getX())
				+ (position.getY() - position2.getY()) * (position.getY() - position2.getY()))));
	}

	public static float getAngle(Vector position, Vector position2) {
		return (float) -Math.atan2(position2.getY() - position.getY(), position2.getX() - position.getX());
	}

	public static float getXv(float moveSpeed, Vector position, Vector position2) {
		return (float) Math.cos(getAngle(position, position2)) * moveSpeed;
	}

	public static float getYv(float moveSpeed, Vector position, Vector position2) {
		return (float) Math.sin(getAngle(position, position2)) * moveSpeed;
	}

	public static float dot(Vector v1, Vector v2) {
		return v1.getX() * v2.getX() + v1.getY() * v2.getY() + v1.getZ() * v2.getZ();
	}

	public static Vector worldToScreen(Vector worldPos) {
		// 1. MVP 변환
		Vector4f clipSpacePos = new Vector4f(worldPos.getX(), worldPos.getY(), worldPos.getZ(), 1.0f)
				.mul(Camera.getViewMatrix()).mul(Camera.getProjectionMatrix());

		// 2. 정규화 장치 좌표계(NDC)로 만들기
		clipSpacePos.div(clipSpacePos.w);

		// 3. NDC (-1 ~ 1)을 스크린 좌표로 변환
		float x = (clipSpacePos.x * 0.5f) * Display.width;
		float y = (clipSpacePos.y * 0.5f) * Display.height;

		return new Vector(x, y, worldPos.getZ());
	}

	public static Vector screenToWorld(Vector screenPos) {
		float x = (2.0f * screenPos.getX()) / Display.width;
		float y = -(2.0f * screenPos.getY()) / -Display.height;
		float z = 0;

		Vector4f ndc = new Vector4f(x, y, z, 1.0f);

		Matrix4f inverseVP = new Matrix4f(Camera.getProjectionMatrix()).mul(Camera.getViewMatrix()).invert();

		Vector4f world = ndc.mul(inverseVP);

		world.div(world.w);

		return new Vector(world.x, world.y, screenPos.getZ());
	}
}
