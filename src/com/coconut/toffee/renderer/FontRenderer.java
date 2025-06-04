package com.coconut.toffee.renderer;

import java.awt.Color;

import org.lwjgl.opengl.GL30;

import com.coconut.toffee.font.TTFont;
import com.coconut.toffee.math.Vector;
import com.coconut.toffee.object.GameObject;
import com.coconut.toffee.shader.ShaderManager;

public class FontRenderer extends GameObject {

	protected Color color = null;
	protected Color outlineColor = new Color(39, 39, 54);
	protected float outlineWidth = 0f;

	public TTFont font = null;
	public String text = "";
	public String align = "center";

	@Override
	public void glRender() {
		font.bakeFont(text, color, outlineColor, outlineWidth);
		sprite = font;
		width = font.getWidth();
		height = font.getHeight();

		if (sprite == null)
			return;

		sprite.bind();

		if (this.align == "left") {
			position.translate(width / 2, 0);
		} else if (this.align == "right") {
			position.translate(width / -2, 0);
		}

		modelMatrix = makeModelMatrix();

		ShaderManager.getCurrentShader().uploadMat4f("uModel", modelMatrix);

		GL30.glBindVertexArray(sprite.getVaoID());

		GL30.glDrawElements(GL30.GL_TRIANGLES, sprite.getElementArray().length, GL30.GL_UNSIGNED_INT, 0);
	}

	public FontRenderer(Vector position, float width, float height, Color color, String text, TTFont font) {
		super(position, width, height);
		this.color = color;
		this.font = font;
		this.outlineWidth = 0f;
	}

	public FontRenderer(Vector position, float width, float height, Color color, Color outlineColor, float outlineWidth,
			String text, TTFont font) {
		super(position, width, height);
		this.color = color;
		this.font = font;
		this.outlineColor = outlineColor;
		this.outlineWidth = outlineWidth;
	}

}
