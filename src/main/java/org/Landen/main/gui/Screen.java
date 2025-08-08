package org.Landen.main.gui;

import imgui.ImGui;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;

import java.util.ArrayList;
import java.util.List;

public class Screen {
    private String title;
    private List<UIComponet> components = new ArrayList<>();
    private boolean docked = false;
    private String id;
    private ScreenDockPresets dockPreset = ScreenDockPresets.NONE;

    public Screen(String title, String id,boolean docked,ScreenDockPresets dockPreset) {
        this.title = title;
        this.docked = docked;
        this.id = id;
        this.dockPreset = dockPreset;
    }

    public void clearComponents() {
        components.clear();
    }

    public String getId() {
        return id;
    }

    public void addComponent(UIComponet component) {
        components.add(component);
    }

    public void renderImGui() {
        if (docked) {
            float windowWidth = 300.0f;
            float windowHeight = ImGui.getIO().getDisplaySizeY();
            float windowPosX = 0.0f;
            float windowPosY = 0.0f;

            switch (dockPreset) {
                case RIGHT:
                    windowPosX = ImGui.getIO().getDisplaySizeX() - windowWidth;
                    windowPosY = 0.0f;
                    break;
                case LEFT:
                    windowPosX = 0.0f;
                    windowPosY = 0.0f;
                    break;
                case TOP:
                    windowPosX = 0.0f;
                    windowPosY = 0.0f;
                    windowWidth = ImGui.getIO().getDisplaySizeX();
                    windowHeight = 300.0f;
                    break;
                case BOTTOM:
                    windowPosX = 0.0f;
                    windowPosY = ImGui.getIO().getDisplaySizeY() - 300.0f;
                    windowWidth = ImGui.getIO().getDisplaySizeX();
                    windowHeight = 300.0f;
                    break;
                case NONE:
                default:
                    break;
            }

            ImGui.setNextWindowPos(windowPosX, windowPosY);
            ImGui.setNextWindowSize(windowWidth, windowHeight);
            ImGui.begin(title, ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse);
        } else {
            ImGui.begin(title);
        }
        ImGui.pushStyleVar(ImGuiStyleVar.FrameBorderSize, 1.0f);
        float availY = ImGui.getContentRegionAvailY();
        int n = components.size();
        float slice = availY / n;

        for (UIComponet comp : components) {
            if (comp instanceof UIGroupComponet)
                ((UIGroupComponet) comp).renderImGui(slice);
            else
                comp.renderImGui();
        }

        ImGui.popStyleVar();
        ImGui.end();
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
