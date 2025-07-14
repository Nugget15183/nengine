package org.Landen.engine.utils;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class FileUtils {
	public static String loadAsString(String path) {
		StringBuilder result = new StringBuilder();

		InputStream inputStream = FileUtils.class.getResourceAsStream(path);
		if (inputStream == null) {
			System.err.println("Couldn't find the file at " + path);
			return "";
		}

		try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
			String line;
			while ((line = reader.readLine()) != null) {
				result.append(line).append("\n");
			}
		} catch (IOException e) {
			System.err.println("Error reading file at " + path + ": " + e.getMessage());
		}

		return result.toString();
	}

	public static ByteBuffer ioResourceToByteBuffer(String resource, int bufferSize) throws IOException {
		ByteBuffer buffer;

		try (
				InputStream source = FileUtils.class.getResourceAsStream(resource);
				ReadableByteChannel rbc = Channels.newChannel(source)
		) {
			buffer = BufferUtils.createByteBuffer(bufferSize);

			while (true) {
				int bytes = rbc.read(buffer);
				if (bytes == -1)
					break;
				if (buffer.remaining() == 0)
					buffer = resizeBuffer(buffer, buffer.capacity() * 2);
			}
		}

		buffer.flip();
		return buffer;
	}

	private static ByteBuffer resizeBuffer(ByteBuffer buffer, int newCapacity) {
		ByteBuffer newBuffer = BufferUtils.createByteBuffer(newCapacity);
		buffer.flip();
		newBuffer.put(buffer);
		return newBuffer;
	}


	public static int loadCubemap(String[] faceFiles) {
		int texID = GL11.glGenTextures();
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texID);

		for (int i = 0; i < faceFiles.length; i++) {
			try (MemoryStack stack = MemoryStack.stackPush()) {
				IntBuffer width = stack.mallocInt(1);
				IntBuffer height = stack.mallocInt(1);
				IntBuffer comp = stack.mallocInt(1);

				ByteBuffer imageBuffer = ioResourceToByteBuffer(faceFiles[i], 8192);
				ByteBuffer image = STBImage.stbi_load_from_memory(imageBuffer, width, height, comp, 4);

				if (image == null) {
					throw new RuntimeException("Failed to load image: " + faceFiles[i] + " — " + STBImage.stbi_failure_reason());
				}

				GL11.glTexImage2D(
						GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i,
						0,
						GL11.GL_RGBA8,
						width.get(0),
						height.get(0),
						0,
						GL11.GL_RGBA,
						GL11.GL_UNSIGNED_BYTE,
						image
				);

				STBImage.stbi_image_free(image);
			} catch (IOException e) {
				throw new RuntimeException("Failed to load texture file " + faceFiles[i], e);
			}
		}

		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL12.GL_TEXTURE_WRAP_R, GL12.GL_CLAMP_TO_EDGE);

		return texID;
	}


	private static ByteBuffer loadImageResource(String resource, IntBuffer width, IntBuffer height, IntBuffer comp) throws IOException {
		ByteBuffer imageBuffer = ioResourceToByteBuffer(resource, 8192);
		ByteBuffer image = STBImage.stbi_load_from_memory(imageBuffer, width, height, comp, 4);
		if (image == null) {
			throw new RuntimeException("Failed to load image: " + resource + " — " + STBImage.stbi_failure_reason());
		}
		return image;
	}
}