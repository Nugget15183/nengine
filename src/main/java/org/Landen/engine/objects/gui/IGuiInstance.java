package org.Landen.engine.objects.gui;

import org.Landen.engine.io.Window;
import org.Landen.engine.objects.nonliteral.GuiScene;

public interface IGuiInstance {
    void drawgui();
    boolean handleGuiInput(GuiScene scene, Window window);
}
