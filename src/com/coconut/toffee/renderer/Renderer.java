package com.coconut.toffee.renderer;

import java.awt.Color;

import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;

import com.coconut.toffee.Display;
import com.coconut.toffee.font.TTFont;
import com.coconut.toffee.math.Vector;
import com.coconut.toffee.object.GameObject;
import com.coconut.toffee.shader.ShaderManager;
import com.coconut.toffee.sprite.Sprite;

public class Renderer {

	public static Color color = new Color(0, 0, 0);

	public static void setColor(Color color) {
		Renderer.color = color;
	}

	public static void clearFrameBuffer(int frameBuffer) {
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, frameBuffer);
		GL30.glClearColor(0, 0, 0, 0);
		GL30.glClear(GL13.GL_COLOR_BUFFER_BIT | GL13.GL_DEPTH_BUFFER_BIT);
	}

	public static void renderRect(Vector position, float width, float height) {
		RectRenderer rect = new RectRenderer(position, width, height, Renderer.color);
		rect.frameBuffer = Display.frameBuffer;

		Display.objects.add(rect);
	}

	public static void renderRect(Vector position, float width, float height, float rotation) {
		RectRenderer rect = new RectRenderer(position, width, height, Renderer.color);
		rect.rotation = rotation;
		rect.frameBuffer = Display.frameBuffer;
		Display.objects.add(rect);
	}

	public static void renderOval(Vector position, float width, float height) {
		OvalRenderer oval = new OvalRenderer(position, width, height, Renderer.color);
		oval.frameBuffer = Display.frameBuffer;
		Display.objects.add(oval);
	}

	public static void renderOval(Vector position, float width, float height, float rotation) {
		OvalRenderer oval = new OvalRenderer(position, width, height, Renderer.color);
		oval.rotation = rotation;
		oval.frameBuffer = Display.frameBuffer;
		Display.objects.add(oval);
	}

	public static void renderUIRect(Vector position, float width, float height) {
		UIRectRenderer rect = new UIRectRenderer(position, width, height, Renderer.color);
		rect.frameBuffer = Display.frameBuffer;

		Display.objects.add(rect);
	}

	public static void renderUIRect(Vector position, float width, float height, float rotation) {
		UIRectRenderer rect = new UIRectRenderer(position, width, height, Renderer.color);
		rect.rotation = rotation;
		rect.frameBuffer = Display.frameBuffer;
		Display.objects.add(rect);
	}

	public static void renderImage(Sprite sprite, Vector position, float width, float height) {
		if (sprite == null)
			return;

		GameObject object = new GameObject(0, 0, width, height);
		object.shader = ShaderManager.getCurrentShader();
		object.frameBuffer = Display.frameBuffer;
		object.position = position;
		object.sprite = sprite;
		Display.objects.add(object);
	}

	public static void renderImage(Sprite sprite, Vector position, float width, float height, float rotation) {
		if (sprite == null)
			return;

		GameObject object = new GameObject(0, 0, width, height);
		object.shader = ShaderManager.getCurrentShader();
		object.frameBuffer = Display.frameBuffer;
		object.position = position;
		object.sprite = sprite;
		object.rotation = rotation;
		Display.objects.add(object);
	}

	public static void renderFont(TTFont font, String text, Vector position) {
		if (font == null)
			return;

		FontRenderer object = new FontRenderer(position, 100, 100, color, text, font);
		object.shader = ShaderManager.defaultShader;
		object.frameBuffer = Display.frameBuffer;
		object.text = text;
		Display.objects.add(object);
	}

	public static void renderFont(TTFont font, String text, Vector position, String align) {
		if (font == null)
			return;

		FontRenderer object = new FontRenderer(new Vector(0, 0), 100, 100, color, text, font);
		object.shader = ShaderManager.defaultShader;
		object.frameBuffer = Display.frameBuffer;
		object.align = align;
		object.text = text;
		object.position = position;
		Display.objects.add(object);
	}

	public static void renderFont(TTFont font, String text, Vector position, Color outlineColor, float outlineWidth) {
		if (font == null)
			return;

		FontRenderer object = new FontRenderer(position, 100, 100, color, outlineColor, outlineWidth, text, font);
		object.shader = ShaderManager.defaultShader;
		object.frameBuffer = Display.frameBuffer;
		object.text = text;
		Display.objects.add(object);
	}

	public static void renderFont(TTFont font, String text, Vector position, String align, Color outlineColor,
			float outlineWidth) {
		if (font == null)
			return;

		FontRenderer object = new FontRenderer(new Vector(0, 0), 100, 100, color, outlineColor, outlineWidth, text,
				font);
		object.shader = ShaderManager.defaultShader;
		object.frameBuffer = Display.frameBuffer;
		object.align = align;
		object.text = text;
		object.position = position;
		Display.objects.add(object);
	}
}
