package org.Landen.main.gui;

import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;

import java.util.ArrayList;
import java.util.List;

public class Screen {
    private String title;
    private List<UIComponet> components = new ArrayList<>();
    private boolean docked = false;
    private String id;

    public Screen(String title, String id,boolean docked) {
        this.title = title;
        this.docked = docked;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void addComponent(UIComponet component) {
        components.add(component);
    }

    public void renderImGui() {
        if(docked) {
            float windowWidth = 300.0f; // Sidebar width
            float windowHeight = ImGui.getIO().getDisplaySizeY();
            float windowPosX = ImGui.getIO().getDisplaySizeX() - windowWidth;
            float windowPosY = 0.0f;

            ImGui.setNextWindowPos(windowPosX, windowPosY);
            ImGui.setNextWindowSize(windowWidth, windowHeight);

            ImGui.begin(title, ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoCollapse);
        } else {
            imgui.ImGui.begin(title);
        }
        for (UIComponet component : components) {
            component.renderImGui();
        }
        imgui.ImGui.end();
    }

    public UIComponet getComponentByID(String selected) {
        for (UIComponet component : components) {
            if (component.getId().equals(selected)) {
                return component;
            }
        }
        return null;
    }
}
