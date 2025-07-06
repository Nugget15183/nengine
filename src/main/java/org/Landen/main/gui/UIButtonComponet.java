package org.Landen.main.gui;

public class UIButtonComponet extends UIComponet{
    private String label;
    private Runnable onClick;

    public UIButtonComponet(String label, Runnable onClick) {
        this.label = label;
        this.onClick = onClick;
    }

    @Override
    public void renderImGui() {
        if (imgui.ImGui.button(label)) {
            if (onClick != null) onClick.run();
        }
    }
}
