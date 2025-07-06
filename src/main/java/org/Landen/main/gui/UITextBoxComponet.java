package org.Landen.main.gui;

import imgui.ImGui;
import imgui.type.ImString;

public class UITextBoxComponet extends UIComponet {
    private String label;
    private ImString text;
    private TextChangedListener listener;

    public interface TextChangedListener {
        void onTextChanged(String newText);
    }

    public UITextBoxComponet(String id,String label, String initialText, int bufferSize, TextChangedListener listener) {
        super(id);
        this.label = label;
        this.text = new ImString(initialText, bufferSize);
        this.listener = listener;
    }

    @Override
    public void renderImGui() {
        String oldText = text.get();
        if (ImGui.inputText(label, text)) {
            String newText = text.get();
            if (!newText.equals(oldText) && listener != null) {
                listener.onTextChanged(newText);
            }
        }
    }

    public String getText() { return text.get(); }
    public void setText(String newText) { text.set(newText); }
}