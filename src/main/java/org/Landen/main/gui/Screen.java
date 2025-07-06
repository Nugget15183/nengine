package org.Landen.main.gui;

import java.util.ArrayList;
import java.util.List;

public class Screen {
    private String title;
    private List<UIComponet> components = new ArrayList<>();

    public Screen(String title) {
        this.title = title;
    }

    public void addComponent(UIComponet component) {
        components.add(component);
    }

    public void renderImGui() {
        imgui.ImGui.begin(title);
        for (UIComponet component : components) {
            component.renderImGui();
        }
        imgui.ImGui.end();
    }

    public void tick() {
        for (UIComponet component : components) {
            component.tick();
        }
    }
}
