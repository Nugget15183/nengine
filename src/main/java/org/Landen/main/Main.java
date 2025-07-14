package org.Landen.main;

import org.Landen.engine.graphics.Renderer;
import org.Landen.engine.graphics.Shader;
import org.Landen.engine.io.Input;
import org.Landen.engine.io.Window;
import org.Landen.engine.maths.Ray;
import org.Landen.engine.maths.Vector3f;
import org.Landen.engine.objects.Camera;
import org.Landen.engine.objects.GameObject;
import org.Landen.engine.objects.Skybox;
import org.Landen.main.Managers.*;
import org.Landen.main.presets.presets;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

public class Main implements Runnable {
	private Thread game;
	private Window window;
	private Renderer renderer;
	private Shader shader;
	private static final int WIDTH = 1280, HEIGHT = 760;
	private boolean hasLoaded = false;
	private Skybox skybox;
	private Run run;

	private long lastFrameTime = System.nanoTime();
	private float deltaTime;

	private final Camera camera = new Camera(new Vector3f(0, 0, 1), new Vector3f(0, 0, 0));

	public static void main(String[] args) {
		new Main().start();
	}

	public void start() {
		game = new Thread(this, "game");
		game.start();
	}

	public void run() {
		init();
		while (!window.shouldClose()) {
			if (!hasLoaded && isOpenGLReady()) {
				hasLoaded = true;
				onLoad();
			}
			update();
			render();
			if (Input.isKeyDown(GLFW.GLFW_KEY_F11)) window.setFullscreen(!window.isFullscreen());
		}
		close();
	}

	private void init() {
		window = new Window(WIDTH, HEIGHT, "Game");
		shader = new Shader("/shaders/mainVertex.glsl", "/shaders/mainFragment.glsl");
		renderer = new Renderer(window, shader);
		//window.setBackgroundColor(1f, 1f, 1f);
		window.create();
		shader.create();
		renderer.init();
		skybox = new Skybox("common_blue");
		run = new Run(window, camera);
	}

	private boolean isOpenGLReady() {
		try {
			GL11.glGetString(GL11.GL_VERSION);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private void update() {
		window.update();
		deltaTime = (System.nanoTime() - lastFrameTime) / 1_000_000_000.0f;
		lastFrameTime = System.nanoTime();

		run.update();

		EventListenerManager.tick();
		AnimationManager.tick(deltaTime);
	}

	private void render() {
		skybox.render(camera, window.getProjectionMatrix());

		MeshManager.render(renderer, camera);
		GuiManager.tick();

		window.swapBuffers();
	}

	private void close() {
		window.destroy();
		shader.destroy();
		MeshManager.clear();
	}

	private void onLoad() {
		presets.loadAll();
		run.onLoad();
	}

	public static void onKeyPress(int key, int scancode, int action, int mods) {
		Run.onKeyPress(key,scancode,action,mods);
	}

	public static void onMousePress(int key, int action, int mods) {
		Run.onMousePress(key,action,mods);
	}
}