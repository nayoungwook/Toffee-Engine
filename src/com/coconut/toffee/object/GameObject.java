package com.coconut.toffee.object;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL30;

import com.coconut.toffee.Display;
import com.coconut.toffee.math.Vector;
import com.coconut.toffee.shader.Shader;
import com.coconut.toffee.shader.ShaderManager;
import com.coconut.toffee.sprite.Sprite;

public class GameObject {

	public Sprite sprite;
	public Vector position = new Vector(0, 0);
	public float rotation = 0f;
	private int renderAtlasIndex = 0;
	
	public int getRenderAtlasIndex() {
		return renderAtlasIndex;
	}
	
	public void setRenderAtlasIndex(int atlasIndex) {
		this.renderAtlasIndex = atlasIndex;
	}

	public float width, height;

	public Shader shader = ShaderManager.defaultShader;

	public Vector anchor = new Vector(0.5f, 0.5f);
	public boolean flipX = false, flipY = false;

	public int frameBuffer = 0;

	public GameObject(Vector position, float width, float height) {
		this.position = position;
		this.width = width;
		this.height = height;
	}

	public GameObject(float x, float y, float width, float height) {
		this.position = new Vector(x, y);
		this.width = width;
		this.height = height;
	}

	protected Matrix4f modelMatrix;
	protected Vector3f glmAnchor;

	public void render() {
		this.frameBuffer = Display.frameBuffer;
		this.shader = ShaderManager.getCurrentShader();
		this.setRenderAtlasIndex(this.sprite.getAtlasIndex());
		Display.objects.add(this);
	}

	public void update() {
	}

	protected Matrix4f makeModelMatrix() {
		Matrix4f modelMatrix = new Matrix4f();
		glmAnchor = new Vector3f(anchor.getX(), anchor.getY(), 0f);

		modelMatrix.translate(new Vector3f(position.getX(), position.getY(), 0));

		modelMatrix.translate(new Vector3f((glmAnchor.x - 0.5f) * width, (glmAnchor.y - 0.5f) * height, 0f));
		modelMatrix.rotate(-this.rotation, new Vector3f(0.0f, 0.0f, 1.0f));
		modelMatrix.translate(new Vector3f((glmAnchor.x - 0.5f) * -1 * width, (glmAnchor.y - 0.5f) * -1 * height, 0f));

		modelMatrix.scale(width * (this.flipX ? -1 : 1), height * (this.flipY ? -1 : 1), 1);
		return modelMatrix;
	}

	public void glRender() {
		if (sprite == null)
			return;

		sprite.bind();

		modelMatrix = makeModelMatrix();

		ShaderManager.getCurrentShader().uploadMat4f("uModel", modelMatrix);
		GL30.glBindVertexArray(sprite.getVaoID());

		GL30.glDrawElements(GL30.GL_TRIANGLES, sprite.getElementArray().length, GL30.GL_UNSIGNED_INT, 0);

	}

}
