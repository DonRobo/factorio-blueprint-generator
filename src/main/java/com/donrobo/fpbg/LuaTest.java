package com.donrobo.fpbg;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;

public class LuaTest {
    public static void main(String[] args) {
        Globals globals = JsePlatform.standardGlobals();
        LuaValue chunk = globals.loadfile("examples/lua/hello.lua");
        chunk.call();
    }
}
