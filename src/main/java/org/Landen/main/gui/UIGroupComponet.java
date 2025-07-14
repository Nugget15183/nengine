package org.Landen.main.gui;

import imgui.ImGui;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;

import java.util.ArrayList;
import java.util.List;

public class UIGroupComponet extends UIComponet {
    private List<UIComponet> children = new ArrayList<>();
    private String title = "";

    public void setTitle(String title) {
        this.title = title;
    }

    public UIGroupComponet(String id, String text) {
        super(id);
        this.title = text;
    }

    public void addComponent(UIComponet component) {
        children.add(component);
    }

    @Override
    public void renderImGui() {
        ImGui.beginGroup();

        if (!title.isEmpty()) {
            ImGui.text(title);
        }

        for (UIComponet child : children) {
            child.renderImGui();
        }
        ImGui.endGroup();
        ImGui.spacing();
    }

    public Object getComponentByID(String id) {
        for (UIComponet child : children) {
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
        for (UIComponet child : children) child.renderImGui();
        ImGui.endChild();
        ImGui.spacing();
    }

}