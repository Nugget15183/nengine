package org.Landen.engine.objects;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.ast.Str;

public class LuaScript extends GameObject {
    private String contents;

    public LuaScript(String contents) {
        this.contents = contents;
    }

    public void updateContents(String newContents) {
        this.contents = newContents;
    }

    public boolean compile(Globals globals) {
        try {
            globals.load(contents);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void run(Globals globals) {
        LuaValue chunk = globals.load(contents);
        chunk.call();
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }
}
