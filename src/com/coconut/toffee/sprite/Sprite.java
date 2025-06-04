package com.coconut.toffee.sprite;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryUtil;

public class Sprite {

	protected float subXOffset = 0f, subYOffset = 0f;
	protected float subWOffset = 1f, subHOffset = 1f;

	protected float[] vertexArray;

	public float getSubXOffset() {
		return subXOffset;
	}

	public float getSubYOffset() {
		return subYOffset;
	}

	public float getSubWOffset() {
		return subWOffset;
	}

	public float getSubHOffset() {
		return subHOffset;
	}

	protected int[] elementArray = { 2, 1, 0, // Top right triangle
			0, 1, 3 // bottom left triangle
	};

	public int[] getElementArray() {
		return elementArray;
	}

	protected int vaoID, vboID, eboID;

	public int getVaoID() {
		return vaoID;
	}

	protected String filepath;
	protected int texID;
	protected float originalWidth = 1, originalHeight = 1;

	protected void initializeGlSettings() {
		GL30.glPixelStorei(GL30.GL_UNPACK_ALIGNMENT, 1);

		GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_S, GL30.GL_CLAMP_TO_BORDER);
		GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_T, GL30.GL_CLAMP_TO_BORDER);

		GL13.glTexParameteri(GL13.GL_TEXTURE_2D, GL13.GL_TEXTURE_MAG_FILTER, GL13.GL_NEAREST);
		GL13.glTexParameteri(GL13.GL_TEXTURE_2D, GL13.GL_TEXTURE_MIN_FILTER, GL13.GL_NEAREST);
	}

	protected void bindImage(String path) {
		this.filepath = path;
		// Generate texture on GPU
		texID = GL30.glGenTextures();

		GL30.glActiveTexture(GL30.GL_TEXTURE0);
		GL30.glBindTexture(GL30.GL_TEXTURE_2D, texID);

		IntBuffer width = BufferUtils.createIntBuffer(1);
		IntBuffer height = BufferUtils.createIntBuffer(1);
		IntBuffer channels = BufferUtils.createIntBuffer(1);

		ByteBuffer image = STBImage.stbi_load(filepath, width, height, channels, 0);

		originalWidth = width.get(0);
		originalHeight = height.get(0);

		// bind into gl texture
		if (image != null) {
			if (channels.get(0) == 3) {
				GL30.glTexImage2D(GL30.GL_TEXTURE_2D, 0, GL13.GL_RGB, width.get(0), height.get(0), 0, GL13.GL_RGB,
						GL30.GL_UNSIGNED_BYTE, image);
			} else if (channels.get(0) == 4) {
				GL30.glTexImage2D(GL30.GL_TEXTURE_2D, 0, GL13.GL_RGBA, width.get(0), height.get(0), 0, GL13.GL_RGBA,
						GL30.GL_UNSIGNED_BYTE, image);
			}
		}
		GL30.glGenerateMipmap(GL30.GL_TEXTURE_2D);

		STBImage.stbi_image_free(image);
	}

	public Sprite(String path, float subXOffset, float subYOffset, float subWOffset, float subHOffset) {
		bindImage(path);

		subXOffset /= originalWidth;
		subYOffset /= originalHeight;
		this.subXOffset = subXOffset;
		this.subYOffset = subYOffset;
		subWOffset /= originalWidth;
		subHOffset /= originalHeight;
		this.subWOffset = subWOffset;
		this.subHOffset = subHOffset;

		vertexArray = new float[] {
				// position // color // UV Coordinates
				0.5f, -0.5f, 0.0f, subXOffset + subWOffset, subYOffset + subHOffset, // Bottom
				-0.5f, 0.5f, 0.0f, subXOffset, subYOffset, // Top left 1
				0.5f, 0.5f, 0.0f, subXOffset + subWOffset, subYOffset, // Top right 2
				-0.5f, -0.5f, 0.0f, subXOffset, subYOffset + subHOffset // Bottom left 3
		};

		initialize();
		initializeGlSettings();
	}

	public Sprite(String path) {

		vertexArray = new float[] {
				// position // color // UV Coordinates
				0.5f, -0.5f, 0.0f, subXOffset + subWOffset, subYOffset + subHOffset, // Bottom
				-0.5f, 0.5f, 0.0f, subXOffset, subYOffset, // Top left 1
				0.5f, 0.5f, 0.0f, subXOffset + subWOffset, subYOffset, // Top right 2
				-0.5f, -0.5f, 0.0f, subXOffset, subYOffset + subHOffset // Bottom left 3
		};

		initialize();
		initializeGlSettings();
		bindImage(path);
	}

	public void cutImage(float subXOffset, float subYOffset, float subWOffset, float subHOffset) {
		subXOffset /= originalWidth;
		subYOffset /= originalHeight;
		this.subXOffset = subXOffset;
		this.subYOffset = subYOffset;
		subWOffset /= originalWidth;
		subHOffset /= originalHeight;
		this.subWOffset = subWOffset;
		this.subHOffset = subHOffset;

		vertexArray = new float[] {
				// position // color // UV Coordinates
				0.5f, -0.5f, 0.0f, subXOffset + subWOffset, subYOffset + subHOffset, // Bottom
				-0.5f, 0.5f, 0.0f, subXOffset, subYOffset, // Top left 1
				0.5f, 0.5f, 0.0f, subXOffset + subWOffset, subYOffset, // Top right 2
				-0.5f, -0.5f, 0.0f, subXOffset, subYOffset + subHOffset // Bottom left 3
		};

		FloatBuffer vertexBuffer = MemoryUtil.memAllocFloat(vertexArray.length);
		vertexBuffer.put(vertexArray).flip(); // 버퍼 준비

		// GL_DYNAMIC_DRAW로 버퍼 데이터 생성
		GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, vboID);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertexBuffer, GL15.GL_DYNAMIC_DRAW);
	}

	public Sprite() {
	}

	public Sprite(int texID) {

		vertexArray = new float[] {
				// position // color // UV Coordinates
				0.5f, -0.5f, 0.0f, subXOffset + subWOffset, subYOffset + subHOffset, // Bottom
				-0.5f, 0.5f, 0.0f, subXOffset, subYOffset, // Top left 1
				0.5f, 0.5f, 0.0f, subXOffset + subWOffset, subYOffset, // Top right 2
				-0.5f, -0.5f, 0.0f, subXOffset, subYOffset + subHOffset // Bottom left 3
		};

		initialize();
		initializeGlSettings();
		this.texID = texID;
	}

	// for frame buffer sprite.
	public Sprite(int texID, float[] vertexArray) {
		this.vertexArray = vertexArray;

		initialize();
		initializeGlSettings();
		this.texID = texID;
	}

	protected void uploadAttribPointers() {
		// Add the vertex attribute pointers
		int positionsSize = 3;
		int uvSize = 2;
		int vertexSizeBytes = (positionsSize + uvSize) * 4;
		GL30.glVertexAttribPointer(0, positionsSize, GL30.GL_FLOAT, false, vertexSizeBytes, 0);
		GL30.glEnableVertexAttribArray(0);

		GL30.glVertexAttribPointer(1, uvSize, GL30.GL_FLOAT, false, vertexSizeBytes, positionsSize * 4);
		GL30.glEnableVertexAttribArray(1);
	}

	protected void initialize() {
		vaoID = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vaoID);

		// Create a float buffer of vertices
		FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexArray.length);
		vertexBuffer.put(vertexArray).flip();

		// Create VBO upload the vertex buffer
		vboID = GL30.glGenBuffers();
		GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, vboID);
		GL30.glBufferData(GL30.GL_ARRAY_BUFFER, vertexBuffer, GL30.GL_DYNAMIC_DRAW);

		// Create the indices and upload
		IntBuffer elementBuffer = BufferUtils.createIntBuffer(elementArray.length);
		elementBuffer.put(elementArray).flip();

		eboID = GL30.glGenBuffers();
		GL30.glBindBuffer(GL30.GL_ELEMENT_ARRAY_BUFFER, eboID);
		GL30.glBufferData(GL30.GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL30.GL_DYNAMIC_DRAW);

		uploadAttribPointers();
	}

	public void bind() {
		GL30.glActiveTexture(GL30.GL_TEXTURE0);
		GL30.glBindTexture(GL30.GL_TEXTURE_2D, texID);
	}

	public void unbind() {
		GL30.glActiveTexture(GL30.GL_TEXTURE0);
		GL30.glBindTexture(GL30.GL_TEXTURE_2D, 0);
	}

}
