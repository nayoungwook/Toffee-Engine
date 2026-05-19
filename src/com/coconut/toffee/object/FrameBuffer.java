package com.coconut.toffee.object;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import com.coconut.toffee.Display;
import com.coconut.toffee.camera.Camera;

public class FrameBuffer {

	private int fbo, rbo;
	private int texture;

	private float resolutionX, resolutionY;

	public float getResolutionX() {
		return resolutionX;
	}

	public float getResolutionY() {
		return resolutionY;
	}

	public FrameBuffer() {
		this.resolutionX = Camera.getResolutionX();
		this.resolutionY = Camera.getResolutionY();
		initialize();
	}

	public FrameBuffer(float width, float height) {
		this.resolutionX = width;
		this.resolutionY = height;
		initialize();
	}

	private void initialize() {
		fbo = GL30.glGenFramebuffers();
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fbo);

		texture = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, (int) resolutionX, (int) resolutionY, 0, GL11.GL_RGBA,
				GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);

		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL13.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL13.GL_CLAMP_TO_EDGE);

		GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, texture, 0);

		GL20.glDrawBuffers(GL30.GL_COLOR_ATTACHMENT0);

		rbo = GL30.glGenRenderbuffers();
		GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, rbo);
		GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL30.GL_DEPTH24_STENCIL8, (int) resolutionX,
				(int) resolutionY);
		GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_STENCIL_ATTACHMENT, GL30.GL_RENDERBUFFER,
				rbo);

		if (GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER) != GL30.GL_FRAMEBUFFER_COMPLETE) {
			throw new RuntimeException("FBO not complete!");
		}

		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
	}

	public void bindTexture(int textureUnit) {
		GL13.glActiveTexture(GL13.GL_TEXTURE0 + textureUnit);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
	}

	public void clear() {
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fbo);

		GL11.glClearColor(0f, 0f, 0f, 1f);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
	}

	public void bind() {
		Display.frameBuffer = fbo;
	}

	public void unbind() {
		Display.frameBuffer = 0;
	}

	public int getTexture() {
		return texture;
	}

	public int getFBO() {
		return fbo;
	}

	public void dispose() {
		GL30.glDeleteFramebuffers(fbo);
		GL11.glDeleteTextures(texture);
	}
}