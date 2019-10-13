package dev.elexi.hugeblank.peripherals.chatmodem;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import dev.elexi.hugeblank.util.LuaPattern;
import net.minecraft.server.network.ServerPlayerEntity;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;

public abstract class ChatPeripheral implements IPeripheral, IChatCatcher {

    private final Set<IComputerAccess> m_computers = new HashSet<>( 1 );
    public final boolean creative;
    private ChatModemState modem;

    protected ChatPeripheral(ChatModemState modem, boolean creative )
    {
        this.modem = modem;
        this.creative = creative;
        catcher.add(this);
    }

    public ChatModemState getModemState() { return modem; }

    @Override
    public String getType() {
        return "chat_modem";
    }

    @Override
    public String[] getMethodNames() {
        return new String[]{
            "capture",
            "uncapture",
            "getCaptures"
        };
    }

    public synchronized boolean handleChatEvents(String message, ServerPlayerEntity player) {
        boolean out = false;
        String username = player.getEntityName();
        String id = player.getUuid().toString();
        String[] captures = modem.getCaptures();
        for (IComputerAccess computer : m_computers) {
            computer.queueEvent("chat_message", new Object[]{username, message, id});
            for(int i = 0; i < captures.length; i++) {
                if (LuaPattern.matches(message, captures[i])) {
                    computer.queueEvent("chat_capture", new Object[]{message, captures[i], username, id});
                    out = true;
                }
            }
        }
        return out;
    }

    @Override
    public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws LuaException, InterruptedException {
        switch (method) {
            case 0:
                if (arguments[0] != null) {
                    modem.capture((String)arguments[0]);
                }
                return null;
            case 1:
                if (arguments[0] != null) {
                    modem.uncapture((String)arguments[0]);
                } else {
                    modem.uncapture(null);
                }
                return null;
            case 2:
                if (arguments[0] != null) {
                    return modem.getCaptures();
                }
            default:
                return null;
        }
    }

    @Override
    public synchronized void attach( @Nonnull IComputerAccess computer )
    {
        synchronized( m_computers )
        {
            m_computers.add( computer );
        }
    }

    @Override
    public synchronized void detach( @Nonnull IComputerAccess computer )
    {
        synchronized( m_computers )
        {
            m_computers.remove( computer );
            modem.uncapture(null);
        }
    }

    @Override
    public boolean equals(IPeripheral other) {
        return other == this;
    }
}
