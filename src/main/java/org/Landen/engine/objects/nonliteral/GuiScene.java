package org.Landen.engine.objects.nonliteral;

import org.Landen.engine.objects.gui.IGuiInstance;

public class GuiScene {
    private IGuiInstance guiInstance;

    public IGuiInstance getGuiInstance() {
        return guiInstance;
    }

    public void setGuiInstance(IGuiInstance guiInstance) {
        this.guiInstance = guiInstance;
    }
}
