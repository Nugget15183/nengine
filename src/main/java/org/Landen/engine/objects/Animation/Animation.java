package org.Landen.engine.objects.Animation;

import org.Landen.engine.maths.Vector3f;

import java.util.ArrayList;

public class Animation {
    private ArrayList<Keyframe> keyframes = new ArrayList<>();
    private float currentTime = 0f;
    private int currentKeyframeIndex = 0;
    private boolean isPlaying = false;
    private boolean loop = false;

    private float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }

    public Animation(ArrayList<Keyframe> kfs) {
        this.keyframes = kfs;
    }

    private Vector3f lerpVector3f(Vector3f a, Vector3f b, float t) {
        return new Vector3f(
                lerp(a.getX(), b.getX(), t),
                lerp(a.getY(), b.getY(), t),
                lerp(a.getZ(), b.getZ(), t)
        );
    }

    public void tick(float deltaTime) {
        if (!isPlaying || keyframes.isEmpty()) {
            return;
        }

        currentTime += deltaTime;

        if (keyframes.size() == 1) {
            Keyframe keyframe = keyframes.get(0);
            if (currentTime >= keyframe.getTime()) {
                keyframe.getTarget().getMesh().setPosition(keyframe.getPos());
                keyframe.getTarget().getMesh().setRotation(keyframe.getRot());
                stop();
            }
            return;
        }

        while (currentKeyframeIndex < keyframes.size() - 1 &&
                currentTime >= keyframes.get(currentKeyframeIndex + 1).getTime()) {
            currentKeyframeIndex++;
        }

        if (currentKeyframeIndex >= keyframes.size() - 1) {
            Keyframe finalKeyframe = keyframes.get(keyframes.size() - 1);
            finalKeyframe.getTarget().getMesh().setPosition(finalKeyframe.getPos());
            finalKeyframe.getTarget().getMesh().setRotation(finalKeyframe.getRot());

            if (loop) {
                restart();
            } else {
                stop();
            }
            return;
        }

        Keyframe currentKeyframe = keyframes.get(currentKeyframeIndex);
        Keyframe nextKeyframe = keyframes.get(currentKeyframeIndex + 1);

        float startTime = currentKeyframe.getTime();
        float endTime = nextKeyframe.getTime();
        float t = (currentTime - startTime) / (endTime - startTime);

        t = Math.max(0f, Math.min(1f, t));

        Vector3f interpolatedPos = lerpVector3f(currentKeyframe.getPos(), nextKeyframe.getPos(), t);
        Vector3f interpolatedRot = lerpVector3f(currentKeyframe.getRot(), nextKeyframe.getRot(), t);

        nextKeyframe.getTarget().getMesh().setPosition(interpolatedPos);
        nextKeyframe.getTarget().getMesh().setRotation(interpolatedRot);
    }

    public void addKeyframe(Keyframe keyframe) {
        keyframes.add(keyframe);
        keyframes.sort((k1, k2) -> Float.compare(k1.getTime(), k2.getTime()));
    }

    public void play() {
        isPlaying = true;
    }

    public void stop() {
        isPlaying = false;
    }

    public void restart() {
        currentTime = 0f;
        currentKeyframeIndex = 0;
        isPlaying = true;
    }

    public void setLoop(boolean loop) {
        this.loop = loop;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public float getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(float time) {
        this.currentTime = time;
        currentKeyframeIndex = 0;
        while (currentKeyframeIndex < keyframes.size() - 1 &&
                time >= keyframes.get(currentKeyframeIndex + 1).getTime()) {
            currentKeyframeIndex++;
        }
    }
}

