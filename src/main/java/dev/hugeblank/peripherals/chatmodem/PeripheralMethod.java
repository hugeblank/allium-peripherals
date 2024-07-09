package dev.hugeblank.peripherals.chatmodem;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.MethodResult;
import dev.hugeblank.peripherals.chatmodem.state.IModemState;

import javax.annotation.Nonnull;

public class PeripheralMethod<StateType extends IModemState> {
    public final String name;
    public final Body<StateType> body;

    public PeripheralMethod(String name, Body<StateType> body) {
        this.name = name;
        this.body = body;
    }

    public static <StateType extends IModemState> PeripheralMethod<StateType> create(String name, Body<StateType> body) {
        return new PeripheralMethod<>(name, body);
    }

    public interface Body<StateType extends IModemState> {
        MethodResult execute(@Nonnull StateType state, @Nonnull IArguments arguments) throws LuaException;
    }
}
