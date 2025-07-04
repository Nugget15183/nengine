package org.Landen.engine.graphics;

import org.Landen.engine.maths.Vector3f;
import org.Landen.engine.maths.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.stb.STBImage.*;

public class Material {
	private String path;
	private int textureID;
	private float width, height;

	private Vector3f color3;
	private float alpha = 0;

	public Material(String path) {
		this.path = path;
	}

	public Material(Vector4f rgba) {
		this.color3 = new Vector3f(rgba.getX(), rgba.getY(), rgba.getZ());
		this.alpha = rgba.getW();
	}

	public void create() {
		// Check if this is a solid color material
		if (color3 != null) {
			createSolidColorTexture();
		} else {
			createTextureFromFile();
		}
	}

	private void createSolidColorTexture() {
		// Create a 1x1 pixel texture with the specified color
		width = 1;
		height = 1;

		// Convert color values from 0.0-1.0 range to 0-255 byte range
		byte r = (byte) (Math.max(0, Math.min(1, color3.getX())) * 255);
		byte g = (byte) (Math.max(0, Math.min(1, color3.getY())) * 255);
		byte b = (byte) (Math.max(0, Math.min(1, color3.getZ())) * 255);
		byte a = (byte) (Math.max(0, Math.min(1, alpha)) * 255);

		// Create a ByteBuffer with RGBA data
		ByteBuffer pixelData = MemoryUtil.memAlloc(4); // 4 bytes for RGBA
		pixelData.put(r).put(g).put(b).put(a);
		pixelData.flip();

		// Generate and bind texture
		textureID = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, textureID);

		// Upload the pixel data
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, 1, 1, 0, GL_RGBA, GL_UNSIGNED_BYTE, pixelData);

		// Set texture parameters
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

		// Clean up
		MemoryUtil.memFree(pixelData);
	}

	private void createTextureFromFile() {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer w = stack.mallocInt(1);
			IntBuffer h = stack.mallocInt(1);
			IntBuffer comp = stack.mallocInt(1);

			// Build full path using filesystem
			String fullPath = "resources/" + path;
			File file = new File(fullPath);
			if (!file.exists()) {
				throw new IOException("Texture not found at: " + fullPath);
			}

			byte[] bytes = java.nio.file.Files.readAllBytes(file.toPath());
			ByteBuffer imageBuffer = MemoryUtil.memAlloc(bytes.length);
			imageBuffer.put(bytes).flip();

			stbi_set_flip_vertically_on_load(true);
			ByteBuffer image = stbi_load_from_memory(imageBuffer, w, h, comp, 4);
			MemoryUtil.memFree(imageBuffer);

			if (image == null) {
				throw new IOException("Failed to load image: " + stbi_failure_reason());
			}

			width = w.get();
			height = h.get();

			textureID = glGenTextures();
			glBindTexture(GL_TEXTURE_2D, textureID);
			glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, (int) width, (int) height, 0, GL_RGBA, GL_UNSIGNED_BYTE, image);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

			stbi_image_free(image);
		} catch (IOException e) {
			System.err.println("Can't find or load texture at " + path + ": " + e.getMessage());
		}
	}

	public void destroy() {
		glDeleteTextures(textureID);
	}

	public float getWidth() {
		return width;
	}

	public float getHeight() {
		return height;
	}

	public int getTextureID() {
		return textureID;
	}
}