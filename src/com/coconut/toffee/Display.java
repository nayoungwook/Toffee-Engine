package com.coconut.toffee;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

import com.coconut.toffee.camera.Camera;
import com.coconut.toffee.input.Input;
import com.coconut.toffee.object.GameObject;
import com.coconut.toffee.shader.Shader;
import com.coconut.toffee.state.Scene;
import com.coconut.toffee.state.SceneManager;

public class Display {

	public static int width, height;
	String title;

	private long glfwWindow;

	private Input input = new Input();

	public static int frameBuffer = 0;

	public Display(String title, int width, int height) {
		this.title = title;
		Display.width = width;
		Display.height = height;

		inintializeOpenGL();
		init();

		Camera.adjustProjection(width, height);
	}

	public void setFullScreen() {
		long monitor = GLFW.glfwGetPrimaryMonitor();
		GLFWVidMode vidMode = GLFW.glfwGetVideoMode(monitor);
		Display.width = vidMode.width();
		Display.height = vidMode.height();
		GL20.glViewport(0, 0, width, height);
		GLFW.glfwSetWindowMonitor(glfwWindow, monitor, 0, 0, vidMode.width(), vidMode.height(), vidMode.refreshRate());
	}

	public void setWindowScreen(int width, int height) {
		Display.width = width;
		Display.height = height;
		GL20.glViewport(0, 0, width, height);
		GLFW.glfwSetWindowMonitor(glfwWindow, 0, 100, 100, width, height, GLFW.GLFW_DONT_CARE);
	}

	public void inintializeOpenGL() { // Setup an error callback
		GLFWErrorCallback.createPrint(System.err).set();

		if (!GLFW.glfwInit()) {
			throw new IllegalStateException("Unable to initialize GLFW.");
		}

		GLFW.glfwDefaultWindowHints();
		GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
		GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_FALSE);
		GLFW.glfwWindowHint(GLFW.GLFW_MAXIMIZED, GLFW.GLFW_FALSE);

		glfwWindow = GLFW.glfwCreateWindow(Display.width, Display.height, this.title, MemoryUtil.NULL, MemoryUtil.NULL);
		if (glfwWindow == MemoryUtil.NULL) {
			throw new IllegalStateException("Failed to create the GLFW window.");
		}

		GLFW.glfwMakeContextCurrent(glfwWindow);
		GLFW.glfwSwapInterval(1);

		GLFW.glfwShowWindow(glfwWindow);

		GLFW.glfwSetKeyCallback(glfwWindow, input.getKeyboardCallback());
		GLFW.glfwSetCursorPosCallback(glfwWindow, input.getMouseMoveCallback());
		GLFW.glfwSetMouseButtonCallback(glfwWindow, input.getMouseButtonsCallback());
		GLFW.glfwSetScrollCallback(glfwWindow, input.getMouseScrollCallback());

		GL.createCapabilities();

		GL30.glEnableVertexAttribArray(0);
		GL30.glEnableVertexAttribArray(1);

		GL13.glEnable(GL13.GL_BLEND);
		GL30.glBlendFunc(GL30.GL_ONE, GL30.GL_ONE_MINUS_SRC_ALPHA);
	}

	public void init() {
	}

	public static ArrayList<GameObject> renderQueue = new ArrayList<>();

	public void update() {
		GLFW.glfwPollEvents();

		Scene scene = SceneManager.getScene();
		if (scene != null)
			scene.update();
	}

	public static List<GameObject> objects = new ArrayList<>();

	public void render() {
		GL30.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
		GL30.glClear(GL13.GL_COLOR_BUFFER_BIT | GL13.GL_DEPTH_BUFFER_BIT);

		Scene scene = SceneManager.getScene();
		objects.clear();

		if (scene != null)
			scene.render();

		objects.sort(Comparator.comparingDouble(obj -> obj.position.getZ()));

		for (int i = 0; i < objects.size(); i++) {
			Shader shader = objects.get(i).shader;
			shader.bind();

			GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, objects.get(i).frameBuffer);

			shader.uploadMat4f("uProjection", Camera.getProjectionMatrix());
			shader.uploadMat4f("uView", Camera.getViewMatrix());
			shader.uploadTexture("uTexture", 0);

			objects.get(i).glRender();
		}

		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
		GLFW.glfwSwapBuffers(glfwWindow);
	}

	public void run() {
		long initialTime = System.nanoTime();
		final double timeU = 1000000000 / 60;
		final double timeF = 1000000000 / 60;
		double deltaU = 0, deltaF = 0;
		int frames = 0, ticks = 0;
		long timer = System.currentTimeMillis();

		while (!GLFW.glfwWindowShouldClose(glfwWindow)) {

			long currentTime = System.nanoTime();
			deltaU += (currentTime - initialTime) / timeU;
			deltaF += (currentTime - initialTime) / timeF;
			initialTime = currentTime;

			if (deltaU >= 1) {
				update();
				ticks++;
				deltaU--;
			}

			if (deltaF >= 1) {
				render();
				frames++;
				deltaF--;
			}

			if (System.currentTimeMillis() - timer > 1000) {
				System.out.println(String.format("UPS : %s, FPS : %s", ticks, frames));
				frames = 0;
				ticks = 0;
				timer += 1000;
			}
		}

		// Free the memory
		Callbacks.glfwFreeCallbacks(glfwWindow);
		GLFW.glfwDestroyWindow(glfwWindow);

		// Terminate GLFW and the free the error callback
		GLFW.glfwTerminate();
		GLFW.glfwSetErrorCallback(null).free();
	}
}