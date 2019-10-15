package dev.elexi.hugeblank.peripherals.chatmodem;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.core.apis.ArgumentHelper;
import dan200.computercraft.api.peripheral.IPeripheral;
import dev.elexi.hugeblank.util.LuaPattern;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public abstract class ChatPeripheral implements IPeripheral, IChatCatcher {

    private final Set<IComputerAccess> m_computers = new HashSet<>( 1 );
    public final boolean creative;
    private String[] boundPlayer = new String[2];
    private ChatModemState modem;

    protected ChatPeripheral(ChatModemState modem, boolean creative )
    {
        this.modem = modem;
        this.creative = creative;
        catcher.add(this);
    }

    public ChatModemState getModemState() { return modem; }

    public synchronized void setBoundPlayer(String name, String id) {
        if (name != null && id != null) {
            modem.setBound(true);
        } else {
            modem.setBound(false);
        }
        this.boundPlayer[0] = name;
        this.boundPlayer[1] = id;
    }

    public void setPlayer(PlayerEntity player) {
        if (this.creative) return;
        String[] playerInfo = getBoundPlayer();
        if (!getModemState().isBound()) {
            setBoundPlayer(player.getName().asString(), player.getUuidAsString());
            player.sendMessage(new LiteralText("Bound modem to " + playerInfo[0] + "."));
        } else if (playerInfo[1].equals(player.getUuidAsString())) {
            player.sendMessage(new LiteralText("Unbound modem from player " + playerInfo[0] + "."));
            setBoundPlayer(null, null);
        } else {
            player.sendMessage(new LiteralText("Modem currently bound to player " + playerInfo[0] + "."));
        }
    }

    public String[] getBoundPlayer() {
        return this.boundPlayer;
    }

    @Override
    public String getType() {
        return "chat_modem";
    }

    @Override
    public String[] getMethodNames() {
        if (this.creative) {
            return new String[]{
                "capture",
                "uncapture",
                "getCaptures"
            };
        }else {
            return new String[]{
                "capture",
                "uncapture",
                "getCaptures",
                "say",
                "getBoundPlayer"
            };
        }
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
        if (!this.getModemState().isBound() && !this.creative) {
            return new Object[] { false };
        }
        switch (method) {
            case 0:
                //capture
                String capture = ArgumentHelper.getString(arguments, 0);
                modem.capture(capture);
                return null;
            case 1:
                //uncapture
                if (arguments.length == 0 ) {
                    modem.uncapture(null);
                    return null;
                } else if (ArgumentHelper.getType(arguments[0]).equals("string")) {
                    return new Object[] { modem.uncapture(ArgumentHelper.getString(arguments, 0)) };
                } else {
                    throw new LuaException("Expected string got " + ArgumentHelper.getType(arguments[0]));
                }
            case 2:
                //getCaptures
                String[] captures = modem.getCaptures();
                HashMap<Integer, String> capSet = new HashMap<>();
                for (int i = 0; i < captures.length; i++) {
                    capSet.put(i+1, captures[i]);
                }
                return new Object[] { capSet };
            case 3:
                //say
                if (arguments.length == 0) {
                    throw new LuaException("Invalid argument #1 ( Expected string, got nil)");
                } else if (ArgumentHelper.getType(arguments[0]).equals("string")) {
                    modem.say(this.boundPlayer[1], ArgumentHelper.getString(arguments, 0));
                    return new Object[] { true };
                } else {
                    throw new LuaException("Invalid argument #1 ( Expected string, got " + ArgumentHelper.getType(arguments[0]) + ")");
                }
            case 4:
                //getBoundPlayer
                return new Object[] { this.boundPlayer[0], this.boundPlayer[1] };
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
            if (m_computers.isEmpty()) {
                modem.uncapture(null);

            }
        }
    }

    @Override
    public boolean equals(IPeripheral other) {
        return other == this;
    }
}
