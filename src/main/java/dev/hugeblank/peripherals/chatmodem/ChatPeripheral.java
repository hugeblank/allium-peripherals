package dev.hugeblank.peripherals.chatmodem;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IDynamicPeripheral;
import dan200.computercraft.api.peripheral.IPeripheral;
import dev.hugeblank.util.PlayerInfo;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;

public abstract class ChatPeripheral implements IDynamicPeripheral {

    private final HashSet<IComputerAccess> m_computers = new HashSet<>();
    public final boolean creative;
    private final ChatModemState modem;

    protected ChatPeripheral(ChatModemState modem)
    {
        modem.setComputers(m_computers);
        this.modem = modem;
        this.creative = modem.creative;
    }

    public ChatModemState getModemState() { return modem; }

    public synchronized void setBoundPlayer(@Nullable PlayerInfo playerInfo) {
        modem.setBound(playerInfo);
    }

    public void setPlayer(PlayerEntity player) {
        if (this.creative) return;
        PlayerInfo playerInfo = getBoundPlayer();
        if (player.getServer() != null) {
            if (!getModemState().isBound()) {
                setBoundPlayer(new PlayerInfo(player));
                player.sendMessage(Text.literal("Bound modem to " + player.getName().getString()), true);
            } else if (playerInfo.uuid().equals(player.getUuid())) {
                player.sendMessage(Text.literal("Unbound modem from player " + playerInfo.playerName()), true);
                setBoundPlayer(null);
            } else {
                player.sendMessage(Text.literal("Modem currently bound to player " + playerInfo.playerName()), true);
            }
        }
    }

    public PlayerInfo getBoundPlayer() {
        return modem.getBound();
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
                return MethodResult.of(true);
            case 1:
                //uncapture
                String caps = arguments.optString(0, null);
                return MethodResult.of(modem.uncapture(caps));
            case 2:
                //getCaptures
                return MethodResult.of((Object) modem.getCaptures());
            case 3:
                //say
                modem.say(arguments.getString(0));
                return MethodResult.of(true);
            case 4:
                //getBoundPlayer
                PlayerInfo info = getBoundPlayer();
                return MethodResult.of(info.playerName(), info.uuid().toString());
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
