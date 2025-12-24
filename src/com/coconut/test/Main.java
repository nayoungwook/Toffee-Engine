package com.coconut.test;

import java.awt.Color;
import java.awt.event.KeyEvent;

import com.coconut.toffee.Display;
import com.coconut.toffee.camera.Camera;
import com.coconut.toffee.font.TTFont;
import com.coconut.toffee.input.Input;
import com.coconut.toffee.math.Mathf;
import com.coconut.toffee.math.Vector;
import com.coconut.toffee.renderer.Renderer;
import com.coconut.toffee.shader.Shader;
import com.coconut.toffee.sprite.Sprite;
import com.coconut.toffee.state.Scene;
import com.coconut.toffee.state.SceneManager;

public class Main {

	public static Display window;

	public static void main(String[] args) {
		window = new Display("Workspace", 1280, 720);
		SceneManager.setScene(new Workspace());
		window.run();
	}

}

class Workspace implements Scene {

	private TTFont font = null;
	private TTFont fontBig = null;
	private Shader testShader = new Shader("engineResources/shader/test/testVertex.glsl",
			"engineResources/shader/test/testFragment.glsl");
	private Shader blurShader = new Shader("engineResources/shader/test/testVertex.glsl",
			"engineResources/shader/test/blur.glsl");

	private Sprite dungeon = null;
	private Sprite knight = null;
	private Sprite torch = null;
	private Sprite dark = null;

	private LightFrameBuffer lightFrameBuffer = null;
	private GameFrameBuffer gameFrameBuffer = null;

	@Override
	public void init() {
		dungeon = new Sprite("engineResources/img/dungeon.png");
		knight = new Sprite("engineResources/img/knight.png");
		torch = new Sprite("engineResources/img/torch.png");
		dark = new Sprite("engineResources/img/dark.png");
		knight.fetchAtlasData(new Vector(0, 0), new Vector(16, 16));
		knight.fetchAtlasData(new Vector(16, 0), new Vector(16, 16));
		knight.fetchAtlasData(new Vector(32, 0), new Vector(16, 16));
		knight.fetchAtlasData(new Vector(48, 0), new Vector(16, 16));

		font = new TTFont("font/font.ttf", 32f);
		fontBig = new TTFont("font/font.ttf", 32f * 2);

		gameFrameBuffer = new GameFrameBuffer();
		lightFrameBuffer = new LightFrameBuffer();
	}

	private boolean fs = false;
	private float timer = 0f;

	@Override
	public void update() {

		if (Input.keys[KeyEvent.VK_W])
			Camera.position.translate(0, 10f);
		if (Input.keys[KeyEvent.VK_S])
			Camera.position.translate(0, -10f);
		if (Input.keys[KeyEvent.VK_A])
			Camera.position.translate(-10f, 0);
		if (Input.keys[KeyEvent.VK_D])
			Camera.position.translate(10f, 0);

		if (Input.keys[KeyEvent.VK_Q])
			Camera.rotation -= 0.05f;
		if (Input.keys[KeyEvent.VK_E])
			Camera.rotation += 0.05f;

		if (Input.keys[KeyEvent.VK_F]) {
			if (!fs)
				Main.window.setFullScreen();
			else
				Main.window.setWindowScreen(1280, 720);
			fs = !fs;
			Input.keys[KeyEvent.VK_F] = false;
		}

		Camera.position.translate(0, 0, Input.scrollYOffset * 0.05f);
		Input.scrollYOffset = 0;

		timer += 0.02f;
	}

	private void renderFn() {
		Renderer.setColor(new Color(20, 20, 20));
		Renderer.renderUIRect(new Vector(0, 0), Display.width, Display.height);
		knight.setAtlasIndex(1);
		Renderer.renderImage(knight, new Vector(-100, 0, 10), 600, 600);
		Renderer.renderImage(knight, new Vector(200, 100), 600, 600);
		knight.setAtlasIndex(2);
		Renderer.renderImage(knight, new Vector(100, 0, 3), 300, 600);
		Renderer.setColor(new Color(255, 67, 79, 10));
		Renderer.renderRect(new Vector(100, 0, 20), 200, 200);
	}

	@Override
	public void render() {
		renderFn();

		Renderer.setColor(new Color(255, 100, 150));
		Renderer.renderOval(Mathf.screenToWorld(Input.mousePointer), 30, 30);

		if (Input.keys[KeyEvent.VK_SPACE]) {
			testShader.bind();
			knight.setAtlasIndex(2);
			Renderer.renderImage(knight, new Vector(-150, 0, 10), 600, 600);
			Renderer.renderImage(knight, new Vector(250, 100), 600, 600);
			Renderer.renderImage(knight, new Vector(110, 0, 3), 300, 600);
			testShader.unbind();
		}

	}
}
