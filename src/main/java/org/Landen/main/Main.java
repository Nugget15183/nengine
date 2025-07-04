package org.Landen.main;

import org.Landen.engine.graphics.Material;
import org.Landen.engine.graphics.Renderer;
import org.Landen.engine.graphics.Shader;
import org.Landen.engine.io.Input;
import org.Landen.engine.io.Window;
import org.Landen.engine.maths.Vector3f;
import org.Landen.engine.maths.Vector4f;
import org.Landen.engine.objects.Camera;
import org.Landen.engine.objects.GameObject;
import org.Landen.main.Managers.EventListenerManager;
import org.Landen.main.Managers.MeshManager;
import org.lwjgl.glfw.GLFW;

import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

public class Main implements Runnable {
	public Thread game;
	public Window window;
	public Renderer renderer;
	public Shader shader;
	public final int WIDTH = 1280, HEIGHT = 760;
	public static boolean looking = true;
	public boolean hasLoaded = false;

	public MeshManager meshManager = new MeshManager();
	public EventListenerManager eventListenerManager = new EventListenerManager();

	public Camera camera = new Camera(new Vector3f(0, 0, 1), new Vector3f(0, 0, 0));

	public void start() {
		game = new Thread(this, "game");
		game.start();
	}

	private boolean isOpenGLReady() {
		try {
			GL11.glGetString(GL11.GL_VERSION);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public void init() {
		window = new Window(WIDTH, HEIGHT, "Game");
		shader = new Shader("/shaders/mainVertex.glsl", "/shaders/mainFragment.glsl");
		renderer = new Renderer(window, shader);
		window.setBackgroundColor(1.0f, 1.0f, 1.0f);
		window.create();
		shader.create();
		renderer.init();
		System.setProperty("org.lwjgl.util.Debug", "true");
	}

	public void run() {
		init();
		while (!window.shouldClose()) {
			if(!hasLoaded && isOpenGLReady()){
				hasLoaded = true;
				onLoad();
			}
			update();
			render();
			if (Input.isKeyDown(GLFW.GLFW_KEY_F11)) window.setFullscreen(!window.isFullscreen());
		}
		close();
	}

	private void update() {
		window.update();

		camera.update(looking);
		camera.setFirstMouse(!looking);

		eventListenerManager.tick();
		window.mouseState(looking);
	}

	private void render() {
		// Render 3D scene first
		MeshManager.render(renderer, camera);

		window.swapBuffers();
	}

	private void close() {
		window.destroy();
		shader.destroy();
		MeshManager.clear();
	}

	private void onLoad() {
		Material gm = new Material(new Vector4f(0.5f,0.5f,0.5f,1f));

		GameObject g = meshManager.instanciateMesh("models/usermodels/monkey.obj",
				gm,
				new Vector3f(0,0,-5),
				new Vector3f(0,0,0),
				new Vector3f(1,1,1));

		Material pm = new Material(new Vector4f(0.5f,0.5f,0.5f,0.5f));

		GameObject p = meshManager.instanciateMesh("models/models/plane.obj",
				pm,
				new Vector3f(0,-2.5f,0),
				new Vector3f(-90,0,0),
				new Vector3f(1,1,1));

	}

	public static void onKeyPress(int key, int scancode,int action, int mods) {
		if(key == GLFW.GLFW_KEY_ESCAPE) {
			looking = !looking;
		}
	}

	public static void onMousePress(int key,int action, int mods) {

	}

	public static void main(String[] args) {
		new Main().start();
	}
}