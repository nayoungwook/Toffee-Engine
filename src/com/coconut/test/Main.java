package com.coconut.test;

import java.awt.event.KeyEvent;
import java.util.ArrayList;

import org.lwjgl.opengl.GL30;

import com.coconut.toffee.Display;
import com.coconut.toffee.camera.Camera;
import com.coconut.toffee.input.Input;
import com.coconut.toffee.math.Vector;
import com.coconut.toffee.object.FrameBuffer;
import com.coconut.toffee.object.GameObject;
import com.coconut.toffee.renderer.ScreenQuad;
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

class Knight extends GameObject {

	public Knight(Sprite sprite, float x, float y) {
		super(x, y, 100, 100);

		this.sprite = sprite;
		this.setRenderAtlasIndex(0);
	}

}

class Aru extends GameObject {

	public Aru(Vector position) {
		super(position, 433, 797);
		this.sprite = Workspace.aruSprite;
	}

	@Override
	public void glRender() {
		super.glRender();
	}

}

class Workspace implements Scene {

	public static Sprite aruSprite = null;

	public static final int MS = 60;

	public FrameBuffer testFrameBuffer;

	private Shader blendShader;
	private Shader testShader;

	public ArrayList<Aru> arus = new ArrayList<>();

	@Override
	public void init() {
		aruSprite = new Sprite("engineResources/img/aru.png");

		arus.add(new Aru(new Vector(0, 0)));
		arus.add(new Aru(new Vector(50, 0)));
		testFrameBuffer = new FrameBuffer();

		blendShader = new Shader("engineResources/shader/blend.vert", "engineResources/shader/blend.frag");
		testShader = new Shader("engineResources/shader/blend.vert", "engineResources/shader/test.frag");
		ScreenQuad.init();
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
			Camera.rotation -= 0.1f;
		if (Input.keys[KeyEvent.VK_E])
			Camera.rotation += 0.1f;

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

	}

	@Override
	public void render() {
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, testFrameBuffer.getFBO());
		testShader.bind();

		// frame buffer 에 그리기 때문에 true
		ScreenQuad.render(true);

		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);

		blendShader.bind();
		testFrameBuffer.bindTexture(0);
		// 일반 화면 fbo == 0 에 그리기 때문에 false
		ScreenQuad.render(false);
		blendShader.unbind();
//		aruSprite.bind();
	}
}
