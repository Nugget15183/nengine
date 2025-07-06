package org.Landen.main.gui;

public class UISliderComponet extends UIComponet {
    private String label;
    private float value;
    private float min;
    private float max;
    private ValueChangedListener listener;

    public interface ValueChangedListener {
        void onValueChanged(float newValue);
    }

    public UISliderComponet(String id,String label, float value, float min, float max, ValueChangedListener listener) {
        super(id);
        this.label = label;
        this.value = value;
        this.min = min;
        this.max = max;
        this.listener = listener;
    }

    @Override
    public void renderImGui() {
        float[] v = { value };
        if (imgui.ImGui.sliderFloat(label, v, min, max)) {
            value = v[0];
            if (listener != null) listener.onValueChanged(value);
        }
    }

    public float getValue() { return value; }
    public void setValue(float value) { this.value = value; }
}