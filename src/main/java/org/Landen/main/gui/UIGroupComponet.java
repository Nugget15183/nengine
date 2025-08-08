package org.Landen.main.gui;

import imgui.ImGui;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;

import java.util.ArrayList;
import java.util.List;

public class UIGroupComponet extends UIComponet {
    protected List<UIComponet> components = new ArrayList<>();
    private String title = "";

    public void setTitle(String title) {
        this.title = title;
    }

    public UIGroupComponet(String id, String text) {
        super(id);
        this.title = text;
    }

    public void addComponent(UIComponet component) {
        components.add(component);
    }

    @Override
    public void renderImGui() {
        ImGui.beginGroup();

        if (!title.isEmpty()) {
            ImGui.text(title);
        }

        for (UIComponet child : components) {
            child.renderImGui();
        }
        ImGui.endGroup();
        ImGui.spacing();
    }

    public Object getComponentByID(String id) {
        for (UIComponet child : components) {
            if (child.getId().equals(id)) {
                return child;
            }
        }
        return null;
    }

    public void renderImGui(float fixedHeight) {
        if (!title.isEmpty()) {
            ImGui.text(title);
        }

        ImGui.beginChild("group_" + getId(),
                ImGui.getContentRegionAvailX(),
                fixedHeight,
                true);
        for (UIComponet child : components) child.renderImGui();
        ImGui.endChild();
        ImGui.spacing();
    }

    public List<UIComponet> getComponents() {
        return components;
    }

}