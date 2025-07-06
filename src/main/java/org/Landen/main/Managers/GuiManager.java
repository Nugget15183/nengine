package org.Landen.main.Managers;

import imgui.ImGui;
import org.Landen.main.gui.Screen;
import org.Landen.engine.io.Window;

import java.util.ArrayList;
import java.util.Objects;


public class GuiManager {
    private static ArrayList<Screen> screens = new ArrayList<>();

     public static void addScreen(Screen screen) {
        screens.add(screen);
    }

    public static void tick() {
        Window.getImGuiGlfw().newFrame();
        ImGui.newFrame();

        for (Screen screen : screens) {
            screen.renderImGui();
        }

        ImGui.render();
        Window.getImGuiGl3().renderDrawData(ImGui.getDrawData());
    }

    public static Screen getScreenByID(String id) {
        for (Screen screen : screens) {
            if (Objects.equals(screen.getId(), id)) {
                return screen;
            }
        }
        return null;
    }

    public static void test() {
        Window.getImGuiGlfw().newFrame();
        ImGui.newFrame();

        ImGui.begin("Demo");
        ImGui.text("Hello, ImGui!");
        ImGui.end();

        ImGui.render();
        Window.getImGuiGl3().renderDrawData(ImGui.getDrawData());
    }
}
