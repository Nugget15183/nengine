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
import org.Landen.main.events.TickEventListener;
import org.Landen.main.gui.Screen;
import org.Landen.main.gui.UIGroupComponet;
import org.Landen.main.gui.UILabelComponet;
import org.Landen.main.gui.UISliderComponet;
import org.Landen.main.presets.presets;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

public class Main implements Runnable {
	private Thread game;
	private Window window;
	private Renderer renderer;
	private Shader shader;
	private static final int WIDTH = 1280, HEIGHT = 760;
	private static boolean looking = true;
	private boolean hasLoaded = false;
	private Skybox skybox;

	private long lastFrameTime = System.nanoTime();
	private float deltaTime;

	private final Camera camera = new Camera(new Vector3f(0, 0, 1), new Vector3f(0, 0, 0));

	private GameObject selectedObject = null;

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
		window.setBackgroundColor(1f, 1f, 1f);
		window.create();
		shader.create();
		renderer.init();
		skybox = new Skybox("common_blue");
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

		camera.update(looking);
		camera.setFirstMouse(!looking);

		EventListenerManager.tick();
		AnimationManager.tick(deltaTime);

		window.mouseState(looking);
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

		EventListenerManager.register(new TickEventListener(() -> {
			if (Input.isButtonPressed(GLFW.GLFW_MOUSE_BUTTON_LEFT) && looking) {
				double mouseX = Input.getMouseX();
				double mouseY = Input.getMouseY();
				int width = window.getWidth();
				int height = window.getHeight();

				Ray ray = camera.createSimpleRayFromMouse(mouseX, mouseY, width, height);

				ArrayList<GameObject> hit = MeshManager.intersects(ray);
				if ((long) hit.size() > 0) {
					selectedObject = hit.getFirst();
				} else {
					selectedObject = null;
				}
			}

            Screen screen = GuiManager.getScreenByID("mainsidebar");
			assert screen != null;
            if(selectedObject != null) {
                UIGroupComponet matgroup = (UIGroupComponet) screen.getComponentByID("materialGroup");
				UIGroupComponet basegroup = (UIGroupComponet) screen.getComponentByID("baseGroup");

				UILabelComponet ulc = (UILabelComponet) basegroup.getComponentByID("selectedobjectdisplay");
				ulc.setText("Selected: " + selectedObject.getName());

				UISliderComponet ursc = (UISliderComponet) matgroup.getComponentByID("reflectiveslider");
				ursc.setValue(selectedObject.getMesh().getMaterial().getReflectiveness());

				UISliderComponet uasc = (UISliderComponet) matgroup.getComponentByID("ambientslider");
				uasc.setValue(selectedObject.getMesh().getMaterial().getAmbient());

				ursc.overrideListener(newValue -> {
                    if(selectedObject != null) {
                        selectedObject.getMesh().getMaterial().setReflectiveness(newValue);
                    }
                });

				uasc.overrideListener(newValue -> {
                    if(selectedObject != null) {
                        selectedObject.getMesh().getMaterial().setAmbient(newValue);
                    }
                });
            } else {

                UIGroupComponet matgroup = (UIGroupComponet) screen.getComponentByID("materialGroup");
				UIGroupComponet basegroup = (UIGroupComponet) screen.getComponentByID("baseGroup");

				UILabelComponet ulc = (UILabelComponet) basegroup.getComponentByID("selectedobjectdisplay");
				ulc.setText("Selected: None");

				UISliderComponet usc = (UISliderComponet) matgroup.getComponentByID("reflectiveslider");
				usc.setValue(0);
			}
		}));
	}

	public static void onKeyPress(int key, int scancode, int action, int mods) {
		if (key == GLFW.GLFW_KEY_ESCAPE) {
			looking = !looking;
		}
	}

	public static void onMousePress(int key, int action, int mods) {

	}
}