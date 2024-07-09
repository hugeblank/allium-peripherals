package dev.hugeblank.peripherals.chatmodem;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IDynamicPeripheral;
import dan200.computercraft.api.peripheral.IPeripheral;
import dev.hugeblank.peripherals.chatmodem.state.IModemState;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.HashSet;

public abstract class ChatPeripheral implements IDynamicPeripheral {

    public final PeripheralMethod<?>[] methods;

    private final HashSet<IComputerAccess> m_computers = new HashSet<>();
    private final IModemState state;

    protected ChatPeripheral(IModemState state, PeripheralMethod<?>[] methods) {
        state.setComputers(m_computers);
        this.state = state;
        this.methods = methods;
    }

    public IModemState getState() {
        return state;
    }

    public void onBlockInteraction(PlayerEntity player) {
       state.onBlockInteraction(player);

    }
    @Override
    @NotNull
    public String getType() {
        return "chat_modem";
    }

    @Override
    @NotNull
    public String @NotNull [] getMethodNames() {
                String[] strings = new String[methods.length];
        for (int i = 0; i < strings.length; i++) {
            strings[i] = methods[i].name;
        }
        return strings;

    }

    @Override
    @NotNull
    public MethodResult callMethod(@Nonnull IComputerAccess computer, @Nonnull ILuaContext context, int method, @Nonnull IArguments arguments) throws LuaException {
        //noinspection rawtypes
        PeripheralMethod.Body body = methods[method].body;
        //noinspection unchecked
        return body.execute(this.state, arguments);
    }

    @Override
    public synchronized void attach(@Nonnull IComputerAccess computer) {
        synchronized (m_computers) {
            m_computers.add(computer);
            state.attach();
        }
    }

    @Override
    public synchronized void detach(@Nonnull IComputerAccess computer) {
        synchronized (m_computers) {
            m_computers.remove(computer);
            if (m_computers.isEmpty()) {
                state.detach();
            }
        }
    }

    @Override
    public boolean equals(IPeripheral other) {
        return other == this;
    }

    public void destroy() {
        state.onDestroy();
    }
}
