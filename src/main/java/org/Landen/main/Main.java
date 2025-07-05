package org.Landen.main;

import org.Landen.engine.graphics.Renderer;
import org.Landen.engine.graphics.Shader;
import org.Landen.engine.io.Input;
import org.Landen.engine.io.Window;
import org.Landen.engine.maths.Vector3f;
import org.Landen.engine.objects.Camera;
import org.Landen.main.Managers.AnimationManager;
import org.Landen.main.Managers.EventListenerManager;
import org.Landen.main.Managers.MeshManager;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

public class Main implements Runnable {
	public Thread game;
	public Window window;
	public Renderer renderer;
	public Shader shader;
	public final int WIDTH = 1280, HEIGHT = 760;
	public static boolean looking = true;
	public boolean hasLoaded = false;

	private long lastFrameTime = System.nanoTime();
	private long currentTime = System.nanoTime();
	private float deltaTime = (currentTime - lastFrameTime) / 1_000_000_000.0f;

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
		window.setBackgroundColor(1f, 1f, 1f);
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
		currentTime = System.nanoTime();
		deltaTime = (currentTime - lastFrameTime) / 1_000_000_000.0f;
		lastFrameTime = currentTime;

		camera.update(looking);
		camera.setFirstMouse(!looking);

		eventListenerManager.tick();
		AnimationManager.tick(deltaTime);

		window.mouseState(looking);
	}


	private void render() {
		MeshManager.render(renderer, camera);
		window.swapBuffers();
	}

	private void close() {
		window.destroy();
		shader.destroy();
		MeshManager.clear();
	}

	private void onLoad() {

	}

	public static void onKeyPress(int key, int scancode, int action, int mods) {
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