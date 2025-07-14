package org.Landen.main;

import imgui.ImGui;
import org.Landen.engine.graphics.Mesh;
import org.Landen.engine.io.Input;
import org.Landen.engine.io.Window;
import org.Landen.engine.maths.Randf;
import org.Landen.engine.maths.Ray;
import org.Landen.engine.objects.Camera;
import org.Landen.engine.objects.GameObject;
import org.Landen.engine.objects.LuaScript;
import org.Landen.engine.objects.Scene;
import org.Landen.main.Managers.EventListenerManager;
import org.Landen.main.Managers.GuiManager;
import org.Landen.main.Managers.MeshManager;
import org.Landen.main.Managers.SceneManager;
import org.Landen.main.events.TickEventListener;
import org.Landen.main.gui.*;
import org.Landen.main.presets.Scenes;
import org.luaj.vm2.Globals;
import org.luaj.vm2.lib.jse.JsePlatform;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;

public class Run {
    private final Globals globals = JsePlatform.standardGlobals();
    private final Window window;
    private static boolean initialized = false;
    public static GameObject selectedObject = null;
    private final Camera camera;
    private static Scene arrows;

    public static boolean looking = true;
    public static boolean lockmouse = false;

    public Run(Window window, Camera camera) {
        initialized = true;
        this.window = window;
        this.camera = camera;
    }

    public void onLoad() {
        if(!initialized) return;

        LuaScript s = new LuaScript("");

        Screen screen = GuiManager.getScreenByID("mainsidebar");
        assert screen != null;

        UIGroupComponet scriptGroup = (UIGroupComponet) screen.getComponentByID("scriptGroup");
        UITextBoxComponet utc = (UITextBoxComponet) scriptGroup.getComponentByID("scriptbox");
        UIButtonComponet rsb = (UIButtonComponet) scriptGroup.getComponentByID("runScriptButton");

        utc.overrideListener(s::updateContents);

        rsb.overrideListener(() -> {
            String currentScript = utc.getText();
            s.updateContents(currentScript);
            try {
                s.run(globals);
            } catch (Exception e) {
                System.err.println("Lua script error: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    public void update() {
        if (!initialized) return;

        looking = !ImGui.getIO().getWantCaptureMouse();
        Screen screen = GuiManager.getScreenByID("mainsidebar");

        camera.update(looking);
        camera.setFirstMouse(!lockmouse);
        window.mouseState(lockmouse);

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
    }

    public static void onKeyPress(int key, int scancode, int action, int mods) {
        if (!initialized) return;

        if (key == GLFW.GLFW_KEY_ESCAPE) {
            lockmouse = !lockmouse;
        }
    }

    public static void onMousePress(int key, int action, int mods) {
        if (!initialized) return;
        if(key == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            if(selectedObject != null) {
                if(arrows == null) {
                    arrows = Scenes.loadDragableArrows(selectedObject.getPosition());
                    SceneManager.insert(arrows, true);
                } else {
                    MeshManager.loadScene(arrows);
                }
            } else {
                if(arrows != null) {
                    MeshManager.unloadScene(arrows);
                }
            }
        }
    }
}
