package org.Landen.main;

import org.Landen.engine.objects.LuaScript;
import org.luaj.vm2.Globals;
import org.luaj.vm2.lib.jse.JsePlatform;

import java.util.ArrayList;
import org.luaj.vm2.Globals;
import org.luaj.vm2.lib.jse.JsePlatform;

public class Build implements Runnable {
    private final Thread thread;
    private final ArrayList<LuaScript> scripts;
    private final ArrayList<Boolean> compileResults = new ArrayList<>();
    private final Globals globals = JsePlatform.standardGlobals();

    public Build(ArrayList<LuaScript> scripts) {
        this.scripts = scripts;
        thread = new Thread(this, "build");
    }

    public void start() {
        thread.start();
    }

    public void shutdown() {
        if (thread.isAlive()) {
            thread.interrupt();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void compileScripts() {
        compileResults.clear();
        for (LuaScript script : scripts) {
            boolean result = script.compile(globals);
            compileResults.add(result);
        }
    }

    public void runScripts() {
        for (int i = 0; i < scripts.size(); i++) {
            if (compileResults.get(i)) {
                scripts.get(i).run(globals);
            }
        }
    }

    @Override
    public void run() {
        compileScripts();
        runScripts();
    }
}