package dev.hugeblank.api.base;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;

public interface Method {
    MethodResult run(IComputerAccess computer, ILuaContext context, IArguments args) throws LuaException;
}
