package com.coconut.toffee.renderer;

import java.awt.Color;

import org.joml.Vector4f;
import org.lwjgl.opengl.GL30;

import com.coconut.toffee.math.Vector;
import com.coconut.toffee.object.GameObject;
import com.coconut.toffee.shader.ShaderManager;

public class OvalRenderer extends GameObject {

	@Override
	public void glRender() {

		modelMatrix = makeModelMatrix();

		float r = (float) color.getRed() / 255, g = (float) color.getGreen() / 255, b = (float) color.getBlue() / 255,
				a = (float) color.getAlpha() / 255;

		ShaderManager.getCurrentShader().uploadMat4f("uModel", modelMatrix);
		ShaderManager.getCurrentShader().uploadVec4f("uColor", new Vector4f(r, g, b, a));

		GL30.glBindVertexArray(Shapes.oval.getVaoID());

		GL30.glDrawArrays(GL30.GL_TRIANGLE_FAN, 0, Shapes.oval.vertexArray.length / 3);
	}

	protected Color color;

	public OvalRenderer(Vector position, float width, float height, Color color) {
		super(position, width, height);
		this.color = color;
	}

}
