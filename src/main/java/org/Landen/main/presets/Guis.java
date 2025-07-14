package org.Landen.main.presets;

import org.Landen.main.Managers.GuiManager;
import org.Landen.main.gui.*;

public class Guis {
    public static void LoadSideEditorMenu() {
        Screen screen = new Screen(".", "mainsidebar",true);
        GuiManager.addScreen(screen);

        UIGroupComponet baseGroup = new UIGroupComponet("baseGroup", "info");
        baseGroup.addComponent(new UILabelComponet("selectedobjectdisplay","Selected: None"));

        UIGroupComponet matgroup = new UIGroupComponet("materialGroup", "Material Editor");

        matgroup.addComponent(new UISliderComponet("reflectiveslider","reflectivity", 0.5f, 0.0f, 1000.0f, new UISliderComponet.ValueChangedListener() {
            @Override
            public void onValueChanged(float newValue) {
                System.out.println("Slider Value Changed: " + newValue);
            }
        }));
        matgroup.addComponent(new UISliderComponet("ambientslider","ambient", 0.5f, 0.0f, 1.5f, new UISliderComponet.ValueChangedListener() {
            @Override
            public void onValueChanged(float newValue) {
                System.out.println("Slider Value Changed: " + newValue);
            }
        }));

        screen.addComponent(baseGroup);
        screen.addComponent(matgroup);

        //screen.addComponent(new UITextBoxComponet("Test TextBox", "Default Text", 64, new UITextBoxComponet.TextChangedListener() {
            //@Override
            //public void onTextChanged(String newText) {
                //System.out.println("Text Changed: " + newText);
            //}
        //}));
        //screen.addComponent(new UIButtonComponet("Start Game", () -> {
        //System.out.println("Game Started");
        //}));
    }
}
