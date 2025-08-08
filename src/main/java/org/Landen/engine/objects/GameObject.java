package org.Landen.engine.objects;

import org.Landen.engine.graphics.Mesh;
import org.Landen.engine.maths.Vector3f;
import org.Landen.main.events.HierarchyListener;

import java.util.ArrayList;
import java.util.List;

public class GameObject {
	private Vector3f position, rotation, scale;
	private Mesh mesh;
	private String id;
	private String name;
	private List<GameObject> children = new ArrayList<>();
	private List<HierarchyListener> listeners = new ArrayList<>();

	public String getId() { return id; }
	public String getName() { return name; }
	public List<GameObject> getChildren() { return children; }

	public void addChild(GameObject child) {
		children.add(child);
		notifyListeners();
	}

	public void removeChild(GameObject child) {
		children.remove(child);
		notifyListeners();
	}

	public void addHierarchyListener(HierarchyListener listener) {
		listeners.add(listener);
	}

	public void removeHierarchyListener(HierarchyListener listener) {
		listeners.remove(listener);
	}

	public void notifyListeners() {
		for (HierarchyListener listener : listeners) {
			listener.onHierarchyChanged(this);
		}
	}

	public GameObject(String name, Vector3f position, Vector3f rotation, Vector3f scale, Mesh mesh) {
		this.name = name;
		this.position = position;
		this.rotation = rotation;
		this.scale = scale;
		this.mesh = mesh;
		this.id = name + "_" + System.identityHashCode(this); // fallback id
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