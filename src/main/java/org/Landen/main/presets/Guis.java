package org.Landen.main.presets;
import org.Landen.engine.objects.GameObject;
import org.Landen.engine.objects.Scene;
import org.Landen.main.Managers.GuiManager;
import org.Landen.main.gui.*;
import imgui.ImGui;

public class Guis {
    public static void LoadSideEditorMenu() {
        Screen screen = new Screen(".", "mainsidebar", true, ScreenDockPresets.RIGHT);
        GuiManager.addScreen(screen);
        UIGroupComponet baseGroup = new UIGroupComponet("baseGroup", "info");
        baseGroup.addComponent(new UILabelComponet("selectedobjectdisplay", "Selected: None"));
        UIGroupComponet matgroup = new UIGroupComponet("materialGroup", "Material Editor");
        matgroup.addComponent(new UISliderComponet("reflectiveslider", "reflectivity", 0.5f, 0.0f, 1000.0f, new UISliderComponet.ValueChangedListener() {
            @Override public void onValueChanged(float newValue) { System.out.println("Slider Value Changed: " + newValue); }
        }));
        matgroup.addComponent(new UISliderComponet("ambientslider", "ambient", 0.5f, 0.0f, 1.5f, new UISliderComponet.ValueChangedListener() {
            @Override public void onValueChanged(float newValue) { System.out.println("Slider Value Changed: " + newValue); }
        }));
        UIGroupComponet scriptGroup = new UIGroupComponet("scriptGroup", "Script Editor");
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
                ImGui.text(getIconForType(obj.getName()) + " " + obj.getName());
                ImGui.sameLine();
                String popupName = "contextPopup_" + id;
                boolean buttonPressed = ImGui.button("+");
                if (buttonPressed) ImGui.openPopup(popupName);
                if (ImGui.beginPopup(popupName)) {
                    if (ImGui.menuItem("Add GameObject")) {
                        GameObject newObj = new GameObject("GameObject", obj.getPosition(), obj.getRotation(), obj.getScale(), null);
                        obj.addChild(newObj);
                        scene.addObject(newObj);
                        attachHierarchyListenersRecursive(newObj, scene);
                        obj.notifyListeners();
                    }
                    if (ImGui.menuItem("Add Script")) {
                        GameObject scriptObj = new GameObject("Script", obj.getPosition(), obj.getRotation(), obj.getScale(), null);
                        obj.addChild(scriptObj);
                        scene.addObject(scriptObj);
                        attachHierarchyListenersRecursive(scriptObj, scene);
                        obj.notifyListeners();
                    }
                    ImGui.endPopup();
                }
                for (UIComponet comp : getComponents()) {
                    if (comp instanceof UIGroupComponet) {
                        ((UIGroupComponet) comp).renderImGui(slice);
                    } else {
                        comp.renderImGui();
                    }
                }
                ImGui.popID();
                if (indentAmount > 0) ImGui.unindent(indentAmount);
            }
        };
        for (GameObject child : obj.getChildren()) {
            group.addComponent(buildHierarchyGroup(child, scene, indentLevel + 1));
        }
        return group;
    }

    private static String getIconForType(String name) {
        if ("Script".equalsIgnoreCase(name)) return "\uD83D\uDCC4";
        if ("GameObject".equalsIgnoreCase(name) || "monkey".equalsIgnoreCase(name)) return "\u25A3";
        return "\u25A1";
    }
}