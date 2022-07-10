package dev.elexi.hugeblank.peripherals.chatmodem;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IDynamicPeripheral;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.entity.player.PlayerEntity;

import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.HashSet;

public abstract class ChatPeripheral implements IDynamicPeripheral {

    private final HashSet<IComputerAccess> m_computers = new HashSet<>();
    public final boolean creative;
    private final String[] boundPlayer = new String[2];
    private final ChatModemState modem;

    protected ChatPeripheral(ChatModemState modem)
    {
        modem.setComputers(m_computers);
        this.modem = modem;
        this.creative = modem.creative;
    }

    public ChatModemState getModemState() { return modem; }

    public synchronized void setBoundPlayer(String name, String id) {
        if (name != null && id != null) {
            modem.setBound(id);
        } else {
            modem.setBound(null);
        }
        this.boundPlayer[0] = name;
        this.boundPlayer[1] = id;
    }

    public void setPlayer(PlayerEntity player) {
        if (this.creative) return;
        String[] playerInfo = getBoundPlayer();
        if (!getModemState().isBound()) {
            setBoundPlayer(player.getName().getString(), player.getUuidAsString());
            player.sendMessage(Text.literal("Bound modem to " + playerInfo[0]), true);
        } else if (playerInfo[1].equals(player.getUuidAsString())) {
            player.sendMessage(Text.literal("Unbound modem from player " + playerInfo[0]), true);
            setBoundPlayer(null, null);
        } else {
            player.sendMessage(Text.literal("Modem currently bound to player " + playerInfo[0]), true);
        }
    }

    public String[] getBoundPlayer() {
        return this.boundPlayer;
    }

    @Override
    @NotNull
    public String getType() {
        return "chat_modem";
    }

    @Override
    @NotNull
    public String @NotNull [] getMethodNames() {
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

    @Override
    @NotNull
    public MethodResult callMethod(@Nonnull IComputerAccess computer, @Nonnull ILuaContext context, int method, @Nonnull IArguments arguments) throws LuaException {
        if (!this.getModemState().isBound() && !this.creative) {
            return MethodResult.of(false);
        }
        switch (method) {
            case 0:
                //capture
                String capture = arguments.getString(0);
                modem.capture(capture);
                return MethodResult.of();
            case 1:
                //uncapture
                String caps = arguments.optString(0, null);
                return MethodResult.of(modem.uncapture(caps));
            case 2:
                //getCaptures
                return MethodResult.of((Object[]) modem.getCaptures());
            case 3:
                //say
                    modem.say(arguments.getString(0));
                    return MethodResult.of(true);
            case 4:
                //getBoundPlayer
                return MethodResult.of(this.boundPlayer[0], this.boundPlayer[1]);
            default:
                return MethodResult.of();
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
