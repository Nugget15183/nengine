package org.Landen.main.gui;

public abstract class UIComponet {
    public void tick() {}
    public abstract void renderImGui();
    private String id;

    public UIComponet(String id) {
        this.id = id;
    }

    public Object getId() {
        return id;
    }
}
