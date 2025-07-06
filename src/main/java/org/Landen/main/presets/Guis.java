package org.Landen.main.presets;

import org.Landen.main.Managers.GuiManager;
import org.Landen.main.gui.*;

public class Guis {
    public static void LoadSideEditorMenu() {
        Screen screen = new Screen(".", "mainsidebar",true);
        screen.addComponent(new UILabelComponet("selectedobjectdisplay","Selected: None"));
        GuiManager.addScreen(screen);
        //screen.addComponent(new UIButtonComponet("Start Game", () -> {
            //System.out.println("Game Started");
        //}));
        //screen.addComponent(new UISliderComponet("Test Slider", 0.5f, 0.0f, 1.0f, new UISliderComponet.ValueChangedListener() {
            //@Override
            //public void onValueChanged(float newValue) {
                //System.out.println("Slider Value Changed: " + newValue);
            //}
        //}));
        //screen.addComponent(new UITextBoxComponet("Test TextBox", "Default Text", 64, new UITextBoxComponet.TextChangedListener() {
            //@Override
            //public void onTextChanged(String newText) {
                //System.out.println("Text Changed: " + newText);
            //}
        //}));

    }
}
