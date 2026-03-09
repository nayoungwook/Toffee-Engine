package com.coconut.toffee.renderer;

import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import com.coconut.toffee.Display;
import com.coconut.toffee.camera.Camera;

public class ScreenQuad {

	private static int vao;

	public static void init() {

		float[] vertices = { -1f, -1f, 0, 0, 1f, -1f, 1, 0, 1f, 1f, 1, 1, -1f, 1f, 0, 1 };

		vao = GL30.glGenVertexArrays();
		int vbo = GL30.glGenBuffers();

		GL30.glBindVertexArray(vao);
		GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, vbo);
		GL30.glBufferData(GL30.GL_ARRAY_BUFFER, vertices, GL30.GL_STATIC_DRAW);

		GL20.glVertexAttribPointer(0, 2, GL30.GL_FLOAT, false, 16, 0);
		GL20.glEnableVertexAttribArray(0);

		GL20.glVertexAttribPointer(1, 2, GL30.GL_FLOAT, false, 16, 8);
		GL20.glEnableVertexAttribArray(1);
	}

	public static void render(boolean isFrameBuffer) {
		if (isFrameBuffer) {
			GL30.glViewport(0, 0, (int) Camera.getResolutionX(), (int) Camera.getResolutionY());
		} else {
			GL30.glViewport(0, 0, Display.width, Display.height);
		}

		GL13.glClearColor(0, 0, 0, 1);
		GL30.glClear(GL13.GL_COLOR_BUFFER_BIT | GL13.GL_DEPTH_BUFFER_BIT);

		GL30.glBindVertexArray(vao);
		GL30.glDrawArrays(GL30.GL_TRIANGLE_FAN, 0, 4);
	}
}
