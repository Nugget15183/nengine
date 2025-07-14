package org.Landen.main.presets;

import org.Landen.engine.objects.LuaScript;
import org.luaj.vm2.*;
import org.luaj.vm2.lib.jse.*;

public class General {
    public static void InitLua() {
        Globals globals = JsePlatform.standardGlobals();

        LuaScript script = new LuaScript(
            "function helloWorld()\n" +
            "    print('Hello, World!')\n" +
            "end\n" +
            "helloWorld()"
        );

        script.run(globals);
    }
}
