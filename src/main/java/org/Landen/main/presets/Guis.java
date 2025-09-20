package org.Landen.main.presets;

import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImString;
import org.Landen.engine.maths.Vector3f;
import org.Landen.engine.objects.GameObject;
import org.Landen.engine.objects.LuaScript;
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
    private static final Set<String> collapsedObjects = new HashSet<>();
    public static UIGroupComponet scriptGroup;
    public static UIGroupComponet matgroup;

    private static final Map<String, Integer> iconCache = new HashMap<>();
    private static final Map<Class<? extends GameObject>, ObjectSettingsRenderer> settingsRenderers = new HashMap<>();

    public static void registerSettings(Class<? extends GameObject> type, ObjectSettingsRenderer renderer) {
        settingsRenderers.put(type, renderer);
    }

    private static final Map<String, Runnable> topTabs = new LinkedHashMap<>();
    private static String activeTab = null;

    public static void registerTopTab(String name, Runnable content) {
        topTabs.put(name, content);
        if (activeTab == null) activeTab = name;
    }

    public static void renderTopBar() {
        // Full width of display
        float width = ImGui.getIO().getDisplaySizeX();
        float height = 25; // adjust for your font size / style

        // Pin window to top
        ImGui.setNextWindowPos(0, 0);
        ImGui.setNextWindowSize(width, height);

        int flags = ImGuiWindowFlags.NoDecoration
                | ImGuiWindowFlags.NoMove
                | ImGuiWindowFlags.NoDocking
                | ImGuiWindowFlags.NoSavedSettings
                | ImGuiWindowFlags.NoBackground; // transparent background if you want overlay look

        // Begin fixed bar window
        ImGui.begin("TopBar", flags);

        if (ImGui.beginTabBar("TopTabs")) {
            for (Map.Entry<String, Runnable> entry : topTabs.entrySet()) {
                String tabName = entry.getKey();
                if (ImGui.beginTabItem(tabName)) {
                    activeTab = tabName;
                    if (entry.getValue() != null) {
                        entry.getValue().run();
                    }
                    ImGui.endTabItem();
                }
            }
            ImGui.endTabBar();
        }

        ImGui.end(); // close "TopBar" window
    }


    public static void loadTopBarDefaults() {
        registerTopTab("File", () -> {
            if (ImGui.menuItem("New Scene")) {
                System.out.println("New Scene created!");
            }
            if (ImGui.menuItem("Save")) {
                System.out.println("Scene saved!");
            }
            if (ImGui.menuItem("Exit")) {
                System.exit(0);
            }
        });

        registerTopTab("Edit", () -> {
            if (ImGui.menuItem("Undo")) {
                System.out.println("Undo action");
            }
            if (ImGui.menuItem("Redo")) {
                System.out.println("Redo action");
            }
        });

        registerTopTab("View", () -> {
            if (ImGui.menuItem("Toggle Hierarchy")) {
                System.out.println("Toggled hierarchy window");
            }
        });
    }

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
                    renderObjectSettings(selectedObject);
                }
            }
        };

        baseGroup.addComponent(new UILabelComponet("selectedobjectdisplay", "Selected: None"));

        matgroup = new UIGroupComponet("materialGroup", "Material Editor");
        matgroup.addComponent(new UISliderComponet("reflectiveslider", "reflectivity", 0.5f, 0.0f, 1000.0f, v -> System.out.println("Reflectivity: " + v)));
        matgroup.addComponent(new UISliderComponet("ambientslider", "ambient", 0.5f, 0.0f, 1.5f, v -> System.out.println("Ambient: " + v)));

        scriptGroup = new UIGroupComponet("scriptGroup", "Script Editor");
        scriptGroup.addComponent(new UITextBoxComponet("scriptbox", "Script Editor", "Start Typing Here...", 64, t -> System.out.println("Script changed: " + t)));
        scriptGroup.addComponent(new UIButtonComponet("runScriptButton", "Run Script", () -> System.out.println("Run Script Button Pressed")));

        screen.addComponent(baseGroup);
        screen.addComponent(matgroup);
        screen.addComponent(scriptGroup);

        // --- Register default settings ---
        registerSettings(GameObject.class, Guis::renderDefaultTransformSettings);

        // --- Register LuaScript settings (no transform) ---
        registerSettings(LuaScript.class, obj -> {
            ImGui.separator();
            ImGui.text("Script Settings");

            LuaScript script = (LuaScript) obj;
            ImString buffer = new ImString(script.getContents(), 1024);

            if (ImGui.inputTextMultiline("Script", buffer)) {
                script.setContents(buffer.get());
            }
        });
    }

    public static void loadHierarchy(Scene scene) {
        ImGui.setNextWindowPos(0, 25); // push it down
        ImGui.setNextWindowSize(
                ImGui.getIO().getDisplaySizeX(),
                ImGui.getIO().getDisplaySizeY() - 25
        );
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

                if (ImGui.isWindowHovered() && !ImGui.isAnyItemHovered()) {
                    if (ImGui.isMouseClicked(ImGuiMouseButton.Left)) {
                        selectedObject = null;
                    }
                    if (ImGui.isMouseClicked(ImGuiMouseButton.Right)) {
                        ImGui.openPopup("hierarchyContextMenu");
                    }
                }

                if (ImGui.beginPopup("hierarchyContextMenu")) {
                    if (selectedObject != null) {
                        if (ImGui.beginMenu("Add GameObject")) {
                            for (MeshManager.MeshPreset preset : MeshManager.MeshPreset.meshPresets) {
                                if (ImGui.menuItem(preset.displayName)) {
                                    GameObject newObj = MeshManager.createGameObjectFromMesh(
                                            preset.displayName,
                                            preset.filePath,
                                            new org.Landen.engine.graphics.Material(),
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
                            LuaScript scriptObj = new LuaScript("print('Hello world')");
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
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, w[0], h[0], 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, image);
            STBImage.stbi_image_free(image);

            iconCache.put(type, texID);
            return texID;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private static void renderObjectSettings(GameObject obj) {
        ObjectSettingsRenderer renderer = settingsRenderers.getOrDefault(obj.getClass(), Guis::renderDefaultTransformSettings);
        renderer.render(obj);
    }

    private static void renderDefaultTransformSettings(GameObject selectedObject) {
        ImGui.separator();
        ImGui.text("Transform");

        float[] pos = {selectedObject.getPosition().x, selectedObject.getPosition().y, selectedObject.getPosition().z};
        if (ImGui.inputFloat3("Position", pos)) {
            selectedObject.getPosition().set(pos[0], pos[1], pos[2]);
        }

        float[] rot = {selectedObject.getRotation().x, selectedObject.getRotation().y, selectedObject.getRotation().z};
        if (ImGui.inputFloat3("Rotation", rot)) {
            selectedObject.getRotation().set(rot[0], rot[1], rot[2]);
        }

        float[] scale = {selectedObject.getScale().x, selectedObject.getScale().y, selectedObject.getScale().z};
        if (ImGui.inputFloat3("Scale", scale)) {
            selectedObject.getScale().set(scale[0], scale[1], scale[2]);
        }
    }


}
