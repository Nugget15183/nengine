package org.Landen.main.gui;

import imgui.ImGui;
import org.Landen.main.Managers.GuiManager;

public class UILabelComponet extends UIComponet{
    private String text;

    public UILabelComponet(String id, String text) {
        super(id);
        this.text = text;
    }

    @Override
    public void renderImGui() {
        ImGui.text(text);
    }

    public String getText() {
        return text;
    }

    public void setText(String newText) {
        this.text = newText;
    }
}
