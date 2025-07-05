package org.Landen.main.Managers;

import org.Landen.engine.objects.Animation.Animation;

import java.util.ArrayList;

public class AnimationManager {
    private static ArrayList<Animation> animations = new ArrayList<>();

    public static void register(Animation anim) {
        animations.add(anim);
    }

    public static void tick(float deltatime) {
        for(Animation anim  : animations) {
            anim.tick(deltatime);
        }
    }
}
