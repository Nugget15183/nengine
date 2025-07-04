package org.Landen.engine.graphics;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import org.Landen.engine.maths.Ray;
import org.Landen.engine.maths.Vector3f;
import org.Landen.engine.objects.GameObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

public class Mesh {
	private Vertex[] vertices;
	private int[] indices;
	private Material material;
	private int vao, pbo, ibo, cbo, tbo, nbo;

	public Mesh(Vertex[] vertices, int[] indices, Material material) {
		this.vertices = vertices;
		this.indices = indices;
		this.material = material;
	}

	public void create() {
		material.create();

		vao = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vao);

		FloatBuffer positionBuffer = MemoryUtil.memAllocFloat(vertices.length * 3);
		float[] positionData = new float[vertices.length * 3];
		for (int i = 0; i < vertices.length; i++) {
			positionData[i * 3] = vertices[i].getPosition().getX();
			positionData[i * 3 + 1] = vertices[i].getPosition().getY();
			positionData[i * 3 + 2] = vertices[i].getPosition().getZ();
		}
		positionBuffer.put(positionData).flip();

		pbo = storeData(positionBuffer, 0, 3);

		FloatBuffer textureBuffer = MemoryUtil.memAllocFloat(vertices.length * 2);
		float[] textureData = new float[vertices.length * 2];
		for (int i = 0; i < vertices.length; i++) {
			textureData[i * 2] = vertices[i].getTextureCoord().getX();
			textureData[i * 2 + 1] = vertices[i].getTextureCoord().getY();
		}
		textureBuffer.put(textureData).flip();

		tbo = storeData(textureBuffer, 2, 2);

		FloatBuffer normalBuffer = MemoryUtil.memAllocFloat(vertices.length * 3);
		float[] normalData = new float[vertices.length * 3];
		for (int i = 0; i < vertices.length; i++) {
			normalData[i * 3] = vertices[i].getNormal().getX();
			normalData[i * 3 + 1] = vertices[i].getNormal().getY();
			normalData[i * 3 + 2] = vertices[i].getNormal().getZ();
		}
		normalBuffer.put(normalData).flip();
		nbo = storeData(normalBuffer, 1, 3); // Store normals at attribute index 1

		IntBuffer indicesBuffer = MemoryUtil.memAllocInt(indices.length);
		indicesBuffer.put(indices).flip();

		ibo = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, ibo);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL15.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
	}

	private int storeData(FloatBuffer buffer, int index, int size) {
		int bufferID = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, bufferID);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(index, size, GL11.GL_FLOAT, false, 0, 0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		return bufferID;
	}

	public void destroy() {
		GL15.glDeleteBuffers(pbo);
		GL15.glDeleteBuffers(cbo);
		GL15.glDeleteBuffers(ibo);
		GL15.glDeleteBuffers(tbo);
		GL15.glDeleteBuffers(nbo);

		GL30.glDeleteVertexArrays(vao);

		material.destroy();
	}

	public void rotate(Vector3f rotation) {
		float radX = (float) Math.toRadians(rotation.getX());
		float radY = (float) Math.toRadians(rotation.getY());
		float radZ = (float) Math.toRadians(rotation.getZ());

		float cosX = (float) Math.cos(radX);
		float sinX = (float) Math.sin(radX);
		float cosY = (float) Math.cos(radY);
		float sinY = (float) Math.sin(radY);
		float cosZ = (float) Math.cos(radZ);
		float sinZ = (float) Math.sin(radZ);

		for (Vertex vertex : vertices) {
			Vector3f pos = vertex.getPosition();
			Vector3f normal = vertex.getNormal();

			Vector3f rotatedPos = rotateVector(pos, cosX, sinX, cosY, sinY, cosZ, sinZ);
			pos.setX(rotatedPos.getX());
			pos.setY(rotatedPos.getY());
			pos.setZ(rotatedPos.getZ());

			Vector3f rotatedNormal = rotateVector(normal, cosX, sinX, cosY, sinY, cosZ, sinZ);
			normal.setX(rotatedNormal.getX());
			normal.setY(rotatedNormal.getY());
			normal.setZ(rotatedNormal.getZ());
		}

		updatePositionBuffer();
		updateNormalBuffer();
	}

	public void translate(Vector3f position) {
		for (Vertex vertex : vertices) {
			Vector3f pos = vertex.getPosition();
			pos.setX(pos.getX() + position.getX());
			pos.setY(pos.getY() + position.getY());
			pos.setZ(pos.getZ() + position.getZ());
		}

		updatePositionBuffer();
	}

	public void setPosition(Vector3f position) {
		ArrayList<Vector3f> d = new ArrayList<Vector3f>();
		Vector3f center = getAvgPosition();

		int i = 0;

		for (Vertex v : vertices) {
			d.add(Vector3f.subtract(v.getPosition(),center));
		}

		for (Vertex vertex : vertices) {
			Vector3f pos = vertex.getPosition();
			pos.setX(position.getX() + d.get(i).getX());
			pos.setY(position.getY() + d.get(i).getY());
			pos.setZ(position.getZ() + d.get(i).getZ());
			i++;
		}

		updatePositionBuffer();
	}

	public Vector3f getAvgPosition() {
		Vector3f center = new Vector3f(0,0,0);

		int i = 0;
		for (Vertex v : vertices) {
			center = Vector3f.add(center,v.getPosition());
			i++;
		}

		center.set(center.getX() / i, center.getY() / i, center.getZ() / i);
		i = 0;

		return center;
	}

	private Vector3f rotateVector(Vector3f vector, float cosX, float sinX, float cosY, float sinY, float cosZ, float sinZ) {
		float x = vector.getX();
		float y = vector.getY();
		float z = vector.getZ();

		float y1 = y * cosX - z * sinX;
		float z1 = y * sinX + z * cosX;

		float x2 = x * cosY + z1 * sinY;
		float z2 = -x * sinY + z1 * cosY;

		float x3 = x2 * cosZ - y1 * sinZ;
		float y3 = x2 * sinZ + y1 * cosZ;

		return new Vector3f(x3, y3, z2);
	}

	private void updatePositionBuffer() {
		FloatBuffer positionBuffer = MemoryUtil.memAllocFloat(vertices.length * 3);
		float[] positionData = new float[vertices.length * 3];
		for (int i = 0; i < vertices.length; i++) {
			positionData[i * 3] = vertices[i].getPosition().getX();
			positionData[i * 3 + 1] = vertices[i].getPosition().getY();
			positionData[i * 3 + 2] = vertices[i].getPosition().getZ();
		}
		positionBuffer.put(positionData).flip();

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, pbo);
		GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, positionBuffer);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

		MemoryUtil.memFree(positionBuffer);
	}

	private void updateNormalBuffer() {
		FloatBuffer normalBuffer = MemoryUtil.memAllocFloat(vertices.length * 3);
		float[] normalData = new float[vertices.length * 3];
		for (int i = 0; i < vertices.length; i++) {
			normalData[i * 3] = vertices[i].getNormal().getX();
			normalData[i * 3 + 1] = vertices[i].getNormal().getY();
			normalData[i * 3 + 2] = vertices[i].getNormal().getZ();
		}
		normalBuffer.put(normalData).flip();

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, nbo);
		GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, normalBuffer);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

		MemoryUtil.memFree(normalBuffer);
	}

	public boolean intersects(Ray ray, GameObject gameObject) {
		RayIntersection intersection = getIntersection(ray, gameObject);
		return intersection != null;
	}

	public RayIntersection getIntersection(Ray ray, GameObject gameObject) {
		RayIntersection closestIntersection = null;
		float closestDistance = Float.MAX_VALUE;

		Vector3f objectPosition = gameObject.getPosition();
		Vector3f objectRotation = gameObject.getRotation();
		Vector3f objectScale = gameObject.getScale();

		for (int i = 0; i < indices.length; i += 3) {
			Vector3f v0 = transformVertex(vertices[indices[i]].getPosition(), objectPosition, objectRotation, objectScale);
			Vector3f v1 = transformVertex(vertices[indices[i + 1]].getPosition(), objectPosition, objectRotation, objectScale);
			Vector3f v2 = transformVertex(vertices[indices[i + 2]].getPosition(), objectPosition, objectRotation, objectScale);

			RayIntersection intersection = rayTriangleIntersection(ray, v0, v1, v2);

			if (intersection != null && intersection.getDistance() < closestDistance) {
				closestDistance = intersection.getDistance();
				closestIntersection = intersection;
			}
		}

		return closestIntersection;
	}

	private Vector3f transformVertex(Vector3f vertex, Vector3f position, Vector3f rotation, Vector3f scale) {
		// Apply scale
		Vector3f transformed = new Vector3f(
				vertex.getX() * scale.getX(),
				vertex.getY() * scale.getY(),
				vertex.getZ() * scale.getZ()
		);

		float radX = (float) Math.toRadians(rotation.getX());
		float radY = (float) Math.toRadians(rotation.getY());
		float radZ = (float) Math.toRadians(rotation.getZ());

		float cosX = (float) Math.cos(radX);
		float sinX = (float) Math.sin(radX);
		float cosY = (float) Math.cos(radY);
		float sinY = (float) Math.sin(radY);
		float cosZ = (float) Math.cos(radZ);
		float sinZ = (float) Math.sin(radZ);

		transformed = rotateVector(transformed, cosX, sinX, cosY, sinY, cosZ, sinZ);

		return Vector3f.add(transformed, position);
	}

	private RayIntersection rayTriangleIntersection(Ray ray, Vector3f v0, Vector3f v1, Vector3f v2) {
		final float EPSILON = 0.0000001f;

		Vector3f edge1 = Vector3f.subtract(v1, v0);
		Vector3f edge2 = Vector3f.subtract(v2, v0);

		Vector3f h = Vector3f.cross(ray.getDirection(), edge2);
		float a = Vector3f.dot(edge1, h);

		if (a > -EPSILON && a < EPSILON) {
			return null;
		}

		float f = 1.0f / a;
		Vector3f s = Vector3f.subtract(ray.getOrigin(), v0);
		float u = f * Vector3f.dot(s, h);

		if (u < 0.0f || u > 1.0f) {
			return null;
		}

		Vector3f q = Vector3f.cross(s, edge1);
		float v = f * Vector3f.dot(ray.getDirection(), q);

		if (v < 0.0f || u + v > 1.0f) {
			return null;
		}

		float t = f * Vector3f.dot(edge2, q);

		if (t > EPSILON) {
			Vector3f intersectionPoint = ray.getPointAt(t);
			return new RayIntersection(intersectionPoint, t);
		}

		return null;
	}

	public static class RayIntersection {
		private Vector3f point;
		private float distance;

		public RayIntersection(Vector3f point, float distance) {
			this.point = point;
			this.distance = distance;
		}

		public Vector3f getPoint() {
			return point;
		}

		public float getDistance() {
			return distance;
		}
	}
	public Vertex[] getVertices() {
		return vertices;
	}

	public int[] getIndices() {
		return indices;
	}

	public int getVAO() {
		return vao;
	}

	public int getPBO() {
		return pbo;
	}

	public int getCBO() {
		return cbo;
	}

	public int getTBO() {
		return tbo;
	}

	public int getIBO() {
		return ibo;
	}

	public int getNBO() {
		return nbo;
	}

	public Material getMaterial() {
		return material;
	}
}