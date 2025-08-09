package org.Landen.main.presets;

import org.Landen.engine.graphics.Material;
import org.Landen.engine.maths.Vector3f;
import org.Landen.engine.objects.GameObject;
import org.Landen.engine.objects.Scene;
import org.Landen.main.Managers.GuiManager;
import org.Landen.main.Managers.MeshManager;
import org.Landen.main.Managers.SceneManager;
import org.Landen.main.gui.*;
import imgui.ImGui;
import imgui.flag.ImGuiMouseButton;
import org.lwjgl.opengl.GL11;
import org.lwjgl.stb.STBImage;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.*;

public class Guis {
    private static GameObject selectedObject = null;
    private static Set<String> collapsedObjects = new HashSet<>();
    public static UIGroupComponet scriptGroup;
    public static UIGroupComponet matgroup;

    // Icon cache for textures
    private static final Map<String, Integer> iconCache = new HashMap<>();

    public static void LoadSideEditorMenu() {
        Screen screen = new Screen(".", "mainsidebar", true, ScreenDockPresets.RIGHT);
        GuiManager.addScreen(screen);
        UIGroupComponet baseGroup = new UIGroupComponet("baseGroup", "info") {
            @Override
            public void renderImGui(float slice) {
                String selectedName = selectedObject != null ? selectedObject.getName() : "None";
                UILabelComponet ulc = (UILabelComponet) getComponentByID("selectedobjectdisplay");
                if (ulc != null) ulc.setText("Selected: " + selectedName);
                if (selectedObject != null) {
                    ImGui.separator();
                    ImGui.text("Object Settings:");
                    float[] pos = {selectedObject.getPosition().x, selectedObject.getPosition().y, selectedObject.getPosition().z};
                    if (ImGui.inputFloat3("Position", pos)) {
                        selectedObject.getPosition().x = pos[0];
                        selectedObject.getPosition().y = pos[1];
                        selectedObject.getPosition().z = pos[2];
                    }
                    float[] rot = {selectedObject.getRotation().x, selectedObject.getRotation().y, selectedObject.getRotation().z};
                    if (ImGui.inputFloat3("Rotation", rot)) {
                        selectedObject.getRotation().x = rot[0];
                        selectedObject.getRotation().y = rot[1];
                        selectedObject.getRotation().z = rot[2];
                    }
                    float[] scale = {selectedObject.getScale().x, selectedObject.getScale().y, selectedObject.getScale().z};
                    if (ImGui.inputFloat3("Scale", scale)) {
                        selectedObject.getScale().x = scale[0];
                        selectedObject.getScale().y = scale[1];
                        selectedObject.getScale().z = scale[2];
                    }
                }
            }
        };
        baseGroup.addComponent(new UILabelComponet("selectedobjectdisplay", "Selected: None"));
        matgroup = new UIGroupComponet("materialGroup", "Material Editor");
        matgroup.addComponent(new UISliderComponet("reflectiveslider", "reflectivity", 0.5f, 0.0f, 1000.0f, newValue -> System.out.println("Slider Value Changed: " + newValue)));
        matgroup.addComponent(new UISliderComponet("ambientslider", "ambient", 0.5f, 0.0f, 1.5f, newValue -> System.out.println("Slider Value Changed: " + newValue)));
        scriptGroup = new UIGroupComponet("scriptGroup", "Script Editor");
        scriptGroup.addComponent(new UITextBoxComponet("scriptbox", "Script Editor", "Start Typing Here...", 64, newText -> System.out.println("Script Text Changed: " + newText)));
        scriptGroup.addComponent(new UIButtonComponet("runScriptButton", "Run Script", () -> System.out.println("Run Script Button Pressed")));
        screen.addComponent(baseGroup);
        screen.addComponent(matgroup);
        screen.addComponent(scriptGroup);
    }

    public static void loadHierarchy(Scene scene) {
        GameObject root = scene.getRootObject();
        attachHierarchyListenersRecursive(root, scene);
        UIGroupComponet rootGroup = buildHierarchyGroup(root, scene);
        Screen screen = new Screen("Hierarchy", "hierarchy", true, ScreenDockPresets.LEFT);
        screen.addComponent(rootGroup);
        GuiManager.addScreen(screen);
    }

    private static void attachHierarchyListenersRecursive(GameObject obj, Scene scene) {
        obj.addHierarchyListener(parent -> {
            UIGroupComponet rootGroup = buildHierarchyGroup(scene.getRootObject(), scene);
            Screen screen = GuiManager.getScreen("hierarchy");
            if (screen != null) {
                screen.clearComponents();
                screen.addComponent(rootGroup);
            }
        });
        for (GameObject child : obj.getChildren()) {
            attachHierarchyListenersRecursive(child, scene);
        }
    }

    public static UIGroupComponet buildHierarchyGroup(GameObject obj, Scene scene) {
        return buildHierarchyGroup(obj, scene, 0);
    }

    private static UIGroupComponet buildHierarchyGroup(GameObject obj, Scene scene, int indentLevel) {
        UIGroupComponet group = new UIGroupComponet(obj.getId() != null ? obj.getId() : obj.getName(), obj.getName()) {
            @Override
            public void renderImGui(float slice) {
                float indentAmount = indentLevel * 20.0f;
                if (indentAmount > 0) ImGui.indent(indentAmount);
                String id = obj.getId() != null ? obj.getId() : obj.getName();
                ImGui.pushID(id);

                boolean isCollapsed = collapsedObjects.contains(id);
                boolean collapseClicked = ImGui.button(isCollapsed ? "▶" : "▼");
                ImGui.sameLine();

                int iconTexID = getIconForType(obj);
                if (iconTexID != 0) {
                    ImGui.image(iconTexID, 16, 16);
                    ImGui.sameLine();
                }

                boolean selected = selectedObject == obj;
                boolean selectableClicked = ImGui.selectable(obj.getName(), selected);

                if (selectableClicked) {
                    selectedObject = obj;
                }

                if (collapseClicked) {
                    if (isCollapsed) collapsedObjects.remove(id);
                    else collapsedObjects.add(id);
                }

                if (!collapsedObjects.contains(id)) {
                    for (UIComponet comp : getComponents()) {
                        if (comp instanceof UIGroupComponet) {
                            ((UIGroupComponet) comp).renderImGui(slice);
                        } else {
                            comp.renderImGui();
                        }
                    }
                }

                ImGui.popID();
                if (indentAmount > 0) ImGui.unindent(indentAmount);

                // --- Detect empty space click ---
                if (ImGui.isWindowHovered() && !ImGui.isAnyItemHovered()) {
                    // Left click empty space -> deselect
                    if (ImGui.isMouseClicked(ImGuiMouseButton.Left)) {
                        selectedObject = null;
                    }
                    // Right click empty space -> deselect + open context menu
                    if (ImGui.isMouseClicked(ImGuiMouseButton.Right)) {
                        //selectedObject = null;
                        ImGui.openPopup("hierarchyContextMenu");
                    }
                }

                // --- Context menu ---
                if (ImGui.beginPopup("hierarchyContextMenu")) {
                    if (selectedObject != null) {

                        if (ImGui.beginMenu("Add GameObject")) {
                            for (MeshManager.MeshPreset preset : MeshManager.MeshPreset.meshPresets) {
                                if (ImGui.menuItem(preset.displayName)) {
                                    GameObject newObj = MeshManager.createGameObjectFromMesh(
                                            preset.displayName,
                                            preset.filePath,
                                            new Material(),
                                            new Vector3f(0, 0, 0),
                                            new Vector3f(0, 0, 0),
                                            new Vector3f(1, 1, 1)
                                    );
                                    selectedObject.addChild(newObj);
                                    SceneManager.getCurrectScene().addObject(newObj);
                                    MeshManager.registerGameObject(newObj);
                                    attachHierarchyListenersRecursive(newObj, scene);
                                    selectedObject.notifyListeners();

                                }
                            }
                            ImGui.endMenu();
                        }

                        if (ImGui.menuItem("Add Script")) {
                            GameObject scriptObj = new GameObject(
                                    "Script",
                                    selectedObject.getPosition(),
                                    selectedObject.getRotation(),
                                    selectedObject.getScale(),
                                    null
                            );
                            selectedObject.addChild(scriptObj);
                            scene.addObject(scriptObj);
                            attachHierarchyListenersRecursive(scriptObj, scene);
                            selectedObject.notifyListeners();
                        }

                    } else {
                        ImGui.textDisabled("No object selected");
                    }
                    ImGui.endPopup();
                }


            }
        };

        for (GameObject child : obj.getChildren()) {
            group.addComponent(buildHierarchyGroup(child, scene, indentLevel + 1));
        }

        return group;
    }

    private static int getIconForType(GameObject obj) {
        String type;
        if ("Script".equalsIgnoreCase(obj.getName())) {
            type = "script";
        } else if ("GameObject".equalsIgnoreCase(obj.getName()) || obj.getName().toLowerCase().contains("monkey")) {
            type = "gameobject";
        } else {
            type = "default";
        }

        if (iconCache.containsKey(type)) {
            return iconCache.get(type);
        }

        String path = "/textures/" + type + ".png";
        try (InputStream is = Guis.class.getResourceAsStream(path)) {
            if (is == null) {
                System.err.println("Icon not found for type: " + type);
                return 0;
            }

            STBImage.stbi_set_flip_vertically_on_load(true);
            byte[] bytes = is.readAllBytes();
            ByteBuffer imageBuffer = ByteBuffer.allocateDirect(bytes.length).put(bytes);
            imageBuffer.flip();

            int[] w = new int[1];
            int[] h = new int[1];
            int[] channels = new int[1];
            ByteBuffer image = STBImage.stbi_load_from_memory(imageBuffer, w, h, channels, 4);
            if (image == null) {
                System.err.println("Failed to load icon: " + path);
                return 0;
            }

            int texID = GL11.glGenTextures();
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, texID);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA,
                    w[0], h[0], 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, image);
            STBImage.stbi_image_free(image);

            iconCache.put(type, texID);
            return texID;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
