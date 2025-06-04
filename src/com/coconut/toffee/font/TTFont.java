package com.coconut.toffee.font;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL13;

import com.coconut.toffee.sprite.Sprite;

public class TTFont extends Sprite {

	private Font awtFont = null;
	public float size = 15f;
	public String textAlign = "center";

	public Font getFont(String path, float size) {
		try (InputStream stream = getClass().getClassLoader().getResourceAsStream(path)) {
			if (stream == null) {
				throw new IllegalArgumentException("Font file not found: " + path);
			}
			return Font.createFont(Font.TRUETYPE_FONT, stream).deriveFont(size);
		} catch (Exception e) {
			System.err.println("FONT ERROR: " + e.getMessage());
			return null;
		}
	}

	public TTFont(String fontPath, float fontSize) {
		awtFont = getFont(fontPath, fontSize);

		vertexArray = new float[] {
				// position // UV Coordinates
				0.5f, -0.5f, 0.0f, subXOffset + subWOffset, subYOffset + subHOffset, // Bottom
																						// right 0
				-0.5f, 0.5f, 0.0f, subXOffset, subYOffset, // Top left 1
				0.5f, 0.5f, 0.0f, subXOffset + subWOffset, subYOffset, // Top right 2
				-0.5f, -0.5f, 0.0f, subXOffset, subYOffset + subHOffset // Bottom left 3
		};

		initialize();
		initializeGlSettings();

		BufferedImage image = new BufferedImage(128, 128, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = (Graphics2D) image.getGraphics();

		this.texID = loadTexture(image);
	}

	public BufferedImage createTextImage(String text, Font font, Color color, Color outlineColor, float outlineWidth) {
		String[] lines = text.split("\n");

		// 1. Measure dimensions
		BufferedImage tempImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		Graphics2D tempG2d = tempImage.createGraphics();
		tempG2d.setFont(font);
		FontMetrics fontMetrics = tempG2d.getFontMetrics();
		int lineHeight = fontMetrics.getHeight();
		int ascent = fontMetrics.getAscent();

		int maxLineWidth = 0;
		for (String line : lines) {
			int lineWidth = fontMetrics.stringWidth(line);
			if (lineWidth > maxLineWidth) {
				maxLineWidth = lineWidth;
			}
		}
		tempG2d.dispose();

		int padding = (int) Math.ceil(outlineWidth);
		int imageWidth = maxLineWidth + padding * 2;
		int imageHeight = lineHeight * lines.length + padding * 2;

		this.width = imageWidth;
		this.height = imageHeight;

		// 2. Create final image
		BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = image.createGraphics();

		// 3. Anti-aliasing
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2d.setFont(font);

		// 4. Render each line
		FontRenderContext frc = g2d.getFontRenderContext();

		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];
			GlyphVector gv = font.createGlyphVector(frc, line);
			int y = padding + ascent + i * lineHeight;
			Shape textShape = gv.getOutline(padding, y);

			// Outline
			g2d.setStroke(new BasicStroke(outlineWidth));
			g2d.setColor(outlineColor);
			g2d.draw(textShape);

			// Fill
			g2d.setColor(color);
			g2d.fill(textShape);
		}

		g2d.dispose();
		return image;
	}

	public void bakeFont(String text, Color color, Color outlineColor, float outlineWidth) {
		BufferedImage image = null;
		image = createTextImage(text, awtFont, color, outlineColor, outlineWidth);

		updateTexture(image);
	}

	private int width = 0, height = 0;

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	private void updateTexture(BufferedImage image) {
		width = image.getWidth();
		height = image.getHeight();

		int[] pixels = new int[width * height];
		image.getRGB(0, 0, width, height, pixels, 0, width);

		ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * 4); // RGBA 4바이트

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int pixel = pixels[y * width + x];
				buffer.put((byte) ((pixel >> 16) & 0xFF)); // Red
				buffer.put((byte) ((pixel >> 8) & 0xFF)); // Green
				buffer.put((byte) (pixel & 0xFF)); // Blue
				buffer.put((byte) ((pixel >> 24) & 0xFF)); // Alpha
			}
		}
		buffer.flip(); // OpenGL이 읽을 수 있도록 버퍼를 준비

		GL13.glBindTexture(GL13.GL_TEXTURE_2D, texID);

		// 텍스처 데이터 업로드
		GL13.glTexImage2D(GL13.GL_TEXTURE_2D, 0, GL13.GL_RGBA, width, height, 0, GL13.GL_RGBA, GL13.GL_UNSIGNED_BYTE,
				buffer);
	}

	private int loadTexture(BufferedImage image) {
		width = image.getWidth();
		height = image.getHeight();

		int[] pixels = new int[width * height];
		image.getRGB(0, 0, width, height, pixels, 0, width);

		ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * 4); // RGBA 4바이트

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int pixel = pixels[y * width + x];
				buffer.put((byte) ((pixel >> 16) & 0xFF)); // Red
				buffer.put((byte) ((pixel >> 8) & 0xFF)); // Green
				buffer.put((byte) (pixel & 0xFF)); // Blue
				buffer.put((byte) ((pixel >> 24) & 0xFF)); // Alpha
			}
		}
		buffer.flip(); // OpenGL이 읽을 수 있도록 버퍼를 준비

		// OpenGL 텍스처 생성
		int textureID = GL13.glGenTextures();
		GL13.glBindTexture(GL13.GL_TEXTURE_2D, textureID);

		// 텍스처 설정
		GL13.glTexParameteri(GL13.GL_TEXTURE_2D, GL13.GL_TEXTURE_MIN_FILTER, GL13.GL_LINEAR);
		GL13.glTexParameteri(GL13.GL_TEXTURE_2D, GL13.GL_TEXTURE_MAG_FILTER, GL13.GL_LINEAR);

		// 텍스처 데이터 업로드
		GL13.glTexImage2D(GL13.GL_TEXTURE_2D, 0, GL13.GL_RGBA, width, height, 0, GL13.GL_RGBA, GL13.GL_UNSIGNED_BYTE,
				buffer);

		return textureID;
	}

}
