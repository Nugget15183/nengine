package org.Landen.engine.objects;

import org.Landen.engine.graphics.Mesh;
import org.Landen.engine.maths.Vector3f;

public class GameObject {
	private Vector3f position, rotation, scale;
	private Mesh mesh;
	private String name;

	public String getName() {
		return name;
	}

	public GameObject(String name, Vector3f position, Vector3f rotation, Vector3f scale, Mesh mesh) {
		this.name = name;
		this.position = position;
		this.rotation = rotation;
		this.scale = scale;
		this.mesh = mesh;
	}
	
	public void update() {
		position.setZ(position.getZ() - 0.05f);
	}

	public Vector3f getPosition() {
		return position;
	}

	public Vector3f getRotation() {
		return rotation;
	}

	public Vector3f getScale() {
		return scale;
	}

	public Mesh getMesh() {
		return mesh;
	}
}