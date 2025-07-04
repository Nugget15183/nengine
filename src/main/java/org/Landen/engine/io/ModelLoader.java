package org.Landen.engine.io;

import org.Landen.engine.graphics.Material;
import org.Landen.engine.graphics.Mesh;
import org.Landen.engine.graphics.Vertex;
import org.Landen.engine.maths.Vector2f;
import org.Landen.engine.maths.Vector3f;
import org.lwjgl.assimp.AIFace;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.AIVector3D;
import org.lwjgl.assimp.Assimp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class ModelLoader {
	public static Mesh loadModel(String filePath, Material material) {
		String actualPath = filePath;

		// Check if file is in resources
		InputStream resourceStream = ModelLoader.class.getClassLoader().getResourceAsStream(filePath);
		if (resourceStream != null) {
			try {
				// Extract resource to temporary file
				actualPath = extractResourceToTemp(filePath, resourceStream);
			} catch (IOException e) {
				System.err.println("Failed to extract resource: " + filePath);
				e.printStackTrace();
				return null;
			}
		}

		AIScene scene = Assimp.aiImportFile(actualPath,
				Assimp.aiProcess_JoinIdenticalVertices |
						Assimp.aiProcess_Triangulate |
						Assimp.aiProcess_FlipUVs);

		if (scene == null) {
			System.err.println("Couldn't load model at " + filePath);
			System.err.println("Assimp error: " + Assimp.aiGetErrorString());
			return null;
		}

		AIMesh mesh = AIMesh.create(scene.mMeshes().get(0));
		int vertexCount = mesh.mNumVertices();

		AIVector3D.Buffer vertices = mesh.mVertices();
		AIVector3D.Buffer normals = mesh.mNormals();

		Vertex[] vertexList = new Vertex[vertexCount];

		for (int i = 0; i < vertexCount; i++) {
			AIVector3D vertex = vertices.get(i);
			Vector3f meshVertex = new Vector3f(vertex.x(), vertex.y(), vertex.z());

			AIVector3D normal = normals.get(i);
			Vector3f meshNormal = new Vector3f(normal.x(), normal.y(), normal.z());

			Vector2f meshTextureCoord = new Vector2f(0, 0);
			if (mesh.mNumUVComponents().get(0) != 0) {
				AIVector3D texture = mesh.mTextureCoords(0).get(i);
				meshTextureCoord.setX(texture.x());
				meshTextureCoord.setY(texture.y());
			}

			vertexList[i] = new Vertex(meshVertex, meshNormal, meshTextureCoord);
		}

		int faceCount = mesh.mNumFaces();
		AIFace.Buffer indices = mesh.mFaces();
		int[] indicesList = new int[faceCount * 3];

		for (int i = 0; i < faceCount; i++) {
			AIFace face = indices.get(i);
			indicesList[i * 3 + 0] = face.mIndices().get(0);
			indicesList[i * 3 + 1] = face.mIndices().get(1);
			indicesList[i * 3 + 2] = face.mIndices().get(2);
		}

		return new Mesh(vertexList, indicesList, material);
	}

	private static String extractResourceToTemp(String resourcePath, InputStream resourceStream) throws IOException {
		// Create temp file
		String fileName = new File(resourcePath).getName();
		String extension = "";
		int dotIndex = fileName.lastIndexOf('.');
		if (dotIndex > 0) {
			extension = fileName.substring(dotIndex);
			fileName = fileName.substring(0, dotIndex);
		}

		File tempFile = File.createTempFile(fileName, extension);
		tempFile.deleteOnExit(); // Clean up on program exit

		// Copy resource to temp file
		try (FileOutputStream out = new FileOutputStream(tempFile)) {
			byte[] buffer = new byte[1024];
			int bytesRead;
			while ((bytesRead = resourceStream.read(buffer)) != -1) {
				out.write(buffer, 0, bytesRead);
			}
		}

		return tempFile.getAbsolutePath();
	}
}