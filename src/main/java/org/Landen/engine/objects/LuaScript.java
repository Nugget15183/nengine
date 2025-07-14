package org.Landen.engine.objects;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.ast.Str;

public class LuaScript {
    private String contents;

    public LuaScript(String contents) {
        this.contents = contents;
    }

    public void updateContents(String newContents) {
        this.contents = newContents;
    }

    public void run(Globals globals) {
        LuaValue chunk = globals.load(contents);
        chunk.call();
    }

}
