package org.Landen.engine.objects;


import org.Landen.engine.io.Input;
import org.Landen.engine.maths.Ray;
import org.Landen.engine.maths.Vector3f;
import org.lwjgl.glfw.GLFW;


public class Camera {
	private Vector3f position, rotation;
	private float moveSpeed = 0.05f, mouseSensitivity = 0.15f, distance = 2.0f, horizontalAngle = 0, verticalAngle = 0;
	private double oldMouseX = 0, oldMouseY = 0, newMouseX, newMouseY;
	private boolean firstMouse = true;

	public Camera(Vector3f position, Vector3f rotation) {
		this.position = position;
		this.rotation = rotation;
	}

	public void setFirstMouse(boolean firstMouse) {
		this.firstMouse = firstMouse;
	}

	public void update(boolean looking) {
		newMouseX = Input.getMouseX();
		newMouseY = Input.getMouseY();

		if (firstMouse && looking) {
			oldMouseX = newMouseX;
			oldMouseY = newMouseY;
			firstMouse = false;
		}

		float x = (float) Math.sin(Math.toRadians(rotation.getY())) * moveSpeed;
		float z = (float) Math.cos(Math.toRadians(rotation.getY())) * moveSpeed;

		if (Input.isKeyDown(GLFW.GLFW_KEY_A)) position = Vector3f.add(position, new Vector3f(-z, 0, x));
		if (Input.isKeyDown(GLFW.GLFW_KEY_D)) position = Vector3f.add(position, new Vector3f(z, 0, -x));
		if (Input.isKeyDown(GLFW.GLFW_KEY_W)) position = Vector3f.add(position, new Vector3f(-x, 0, -z));
		if (Input.isKeyDown(GLFW.GLFW_KEY_S)) position = Vector3f.add(position, new Vector3f(x, 0, z));
		if (Input.isKeyDown(GLFW.GLFW_KEY_SPACE)) position = Vector3f.add(position, new Vector3f(0, moveSpeed, 0));
		if (Input.isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT)) position = Vector3f.add(position, new Vector3f(0, -moveSpeed, 0));

		float dx = (float) (newMouseX - oldMouseX);
		float dy = (float) (newMouseY - oldMouseY);

		if(looking) {
			float cameraup = -dy * mouseSensitivity;
			float newPitch = rotation.getX() + cameraup;
			float newYaw = rotation.getY() + (-dx * mouseSensitivity);

			// Clamp pitch to prevent looking past straight up/down (in radians)
			newPitch = Math.max(-90f, Math.min(90f, newPitch)); // -π/2 to π/2

			rotation = new Vector3f(newPitch, newYaw, rotation.getZ());

			oldMouseX = newMouseX;
			oldMouseY = newMouseY;
		}
	}
	
	public void update(GameObject object) {
		newMouseX = Input.getMouseX();
		newMouseY = Input.getMouseY();
		
		float dx = (float) (newMouseX - oldMouseX);
		float dy = (float) (newMouseY - oldMouseY);
		
		if (Input.isButtonDown(GLFW.GLFW_MOUSE_BUTTON_LEFT)) {
			verticalAngle -= dy * mouseSensitivity;
			horizontalAngle += dx * mouseSensitivity;
		}
		if (Input.isButtonDown(GLFW.GLFW_MOUSE_BUTTON_RIGHT)) {
			if (distance > 0) {
				distance += dy * mouseSensitivity / 4;
			} else {
				distance = 0.1f;
			}
		}
		
		float horizontalDistance = (float) (distance * Math.cos(Math.toRadians(verticalAngle)));
		float verticalDistance = (float) (distance * Math.sin(Math.toRadians(verticalAngle)));
		
		float xOffset = (float) (horizontalDistance * Math.sin(Math.toRadians(-horizontalAngle)));
		float zOffset = (float) (horizontalDistance * Math.cos(Math.toRadians(-horizontalAngle)));
		
		position.set(object.getPosition().getX() + xOffset, object.getPosition().getY() - verticalDistance, object.getPosition().getZ() + zOffset);
	
		rotation.set(verticalAngle, -horizontalAngle, 0);
		
		oldMouseX = newMouseX;
		oldMouseY = newMouseY;
	}

	// Add this to your Camera class instead of the complex version
	public Ray createRayFromMouse(double mouseX, double mouseY, int screenWidth, int screenHeight) {
		// Convert screen coordinates to normalized coordinates (-1 to 1)
		float x = (float) (2.0 * mouseX / screenWidth - 1.0);
		float y = (float) (1.0 - 2.0 * mouseY / screenHeight);

		// Create direction vector based on field of view
		float fov = 70.0f; // Field of view in degrees - adjust as needed
		float aspect = (float) screenWidth / screenHeight;
		float fovRad = (float) Math.toRadians(fov);

		// Calculate direction in camera space
		Vector3f direction = new Vector3f(
				x * (float) Math.tan(fovRad / 2.0) * aspect,
				y * (float) Math.tan(fovRad / 2.0),
				-1.0f
		);

		// Transform direction by camera rotation
		direction = transformByRotation(direction, this.rotation);

		return new Ray(this.position, direction);
	}

	public Ray createSimpleRayFromMouse(double mouseX, double mouseY, int screenWidth, int screenHeight) {
		// Convert screen coordinates to normalized coordinates
		float x = (float) (2.0 * mouseX / screenWidth - 1.0);
		float y = (float) (1.0 - 2.0 * mouseY / screenHeight);

		Vector3f direction = new Vector3f(x, y, -1.0f);

		// Transform direction by camera rotation
		direction = transformByRotation(direction, this.rotation);

		return new Ray(this.position, direction);
	}

	private Vector3f transformByRotation(Vector3f vector, Vector3f rotation) {
		float radX = (float) Math.toRadians(rotation.getX());
		float radY = (float) Math.toRadians(rotation.getY());
		float radZ = (float) Math.toRadians(rotation.getZ());

		float cosX = (float) Math.cos(radX);
		float sinX = (float) Math.sin(radX);
		float cosY = (float) Math.cos(radY);
		float sinY = (float) Math.sin(radY);
		float cosZ = (float) Math.cos(radZ);
		float sinZ = (float) Math.sin(radZ);

		float x = vector.getX();
		float y = vector.getY();
		float z = vector.getZ();

		// Apply rotations (same as in your mesh rotate method)
		float y1 = y * cosX - z * sinX;
		float z1 = y * sinX + z * cosX;

		float x2 = x * cosY + z1 * sinY;
		float z2 = -x * sinY + z1 * cosY;

		float x3 = x2 * cosZ - y1 * sinZ;
		float y3 = x2 * sinZ + y1 * cosZ;

		return new Vector3f(x3, y3, z2);
	}

	public Vector3f getPosition() {
		return position;
	}

	public Vector3f getRotation() {
		return rotation;
	}
}