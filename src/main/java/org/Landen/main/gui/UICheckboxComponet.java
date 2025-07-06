package org.Landen.main.gui;

public class UICheckboxComponet extends UIComponet {
    private boolean checked;
    private String label;
    private UISliderComponet.ValueChangedListener listener;

    public UICheckboxComponet(String id, String label, boolean initialState, UISliderComponet.ValueChangedListener listener) {
        super(id);
        this.label = label;
        this.checked = initialState;
        this.listener = listener;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    @Override
    public void renderImGui() {
        boolean state = checked;
        if (imgui.ImGui.checkbox(label, state)) {
            checked = state;
            if (listener != null) listener.onValueChanged(checked ? 1.0f : 0.0f);
        }
    }
}
