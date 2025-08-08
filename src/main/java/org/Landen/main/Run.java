package org.Landen.main;

import imgui.ImGui;
import org.Landen.engine.graphics.Mesh;
import org.Landen.engine.io.Input;
import org.Landen.engine.io.Window;
import org.Landen.engine.maths.Randf;
import org.Landen.engine.maths.Ray;
import org.Landen.engine.maths.Vector3f;
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
import java.util.Objects;

public class Run {
    private final Globals globals = JsePlatform.standardGlobals();
    private static Window window = null;
    private static boolean initialized = false;

    private static Camera camera = null;
    private static Scene arrows;

    public static boolean looking = true;
    public static boolean lockmouse = false;
    public static GameObject selectedObject = null;

    public Run(Window window, Camera camera) {
        initialized = true;
        Run.window = window;
        Run.camera = camera;
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
        if(screen != null) {
            UIGroupComponet matgroup = (UIGroupComponet) screen.getComponentByID("materialGroup");
            UIGroupComponet basegroup = (UIGroupComponet) screen.getComponentByID("baseGroup");
            UILabelComponet ulc = (UILabelComponet) basegroup.getComponentByID("selectedobjectdisplay");
            if(selectedObject != null) {
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
                ulc.setText("Selected: None");
                UISliderComponet usc = (UISliderComponet) matgroup.getComponentByID("reflectiveslider");
                usc.setValue(0);
            }
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
            if(looking) {
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
                if(arrows == null) {
                    arrows = Scenes.loadDragableArrows(selectedObject.getPosition());
                    SceneManager.insert(arrows, true);
                } else {
                    MeshManager.loadScene(arrows);
                    Vector3f transformedCenter = getTransformedCenter(selectedObject);

                    transformedCenter.print();
                    for(GameObject g : arrows.getGameObjects()) {
                        if(Objects.equals(g.getName(), "left/right-arrows")) {
                            g.getMesh().setPosition(transformedCenter);
                        } else if(Objects.equals(g.getName(), "front/back-arrows")) {
                            g.getMesh().setPosition(transformedCenter);
                        } else if(Objects.equals(g.getName(), "up/down-arrows")) {
                            g.getMesh().setPosition(transformedCenter);
                        }
                    }
                }
            } else {
                if(arrows != null) {
                    MeshManager.unloadScene(arrows);
                }
            }
        }
    }

    private static Vector3f getTransformedCenter(GameObject object) {
        Vector3f center = object.getMesh().getAvgPosition();
        Vector3f scaled = new Vector3f(
                center.getX() * object.getScale().getX(),
                center.getY() * object.getScale().getY(),
                center.getZ() * object.getScale().getZ()
        );

        float rx = (float) Math.toRadians(object.getRotation().getX());
        float ry = (float) Math.toRadians(object.getRotation().getY());
        float rz = (float) Math.toRadians(object.getRotation().getZ());

        float cosX = (float) Math.cos(rx), sinX = (float) Math.sin(rx);
        float cosY = (float) Math.cos(ry), sinY = (float) Math.sin(ry);
        float cosZ = (float) Math.cos(rz), sinZ = (float) Math.sin(rz);

        Vector3f rotated = object.getMesh().rotateVector(scaled, cosX, sinX, cosY, sinY, cosZ, sinZ);
        return Vector3f.add(rotated, object.getPosition());
    }

}
